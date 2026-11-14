package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.EmployeeBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.EmployeeDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.exception.BadRequestException;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;
import com.github.alessiobianchi.erp.service.EmployeesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeesServiceImpl implements EmployeesService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    // BCrypt only hashes the first 72 bytes and Spring's encoder rejects longer input.
    private static final int MAX_PASSWORD_BYTES = 72;

    private final EmployeesDAO employeesDao;
    private final OrdersDAO ordersDao;
    private final PasswordEncoder passwordEncoder;

    public EmployeesServiceImpl(EmployeesDAO employeesDao, OrdersDAO ordersDao, PasswordEncoder passwordEncoder) {
        this.employeesDao = employeesDao;
        this.ordersDao = ordersDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeDTO findByUsername(String username) {
        Employee employee = employeesDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return EmployeeBeanToDtoConverter.convert(employee);
    }

    @Override
    public List<EmployeeDTO> findAllByOrderByEmployeeIdDesc() {
        return employeesDao.findAllByOrderByEmployeeIdDesc().stream()
                .map(EmployeeBeanToDtoConverter::convert)
                .toList();
    }

    @Override
    public Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(int pageNumber, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Employee> page = StringUtils.hasText(search)
                ? employeesDao.search(search.trim(), pageable)
                : employeesDao.findAllByOrderByEmployeeIdDesc(pageable);
        return page.map(EmployeeBeanToDtoConverter::convert);
    }

    @Override
    @Transactional
    public EmployeeDTO create(EmployeeDTO employeeDto) {
        Employee employee = EmployeeDtoToBeanConverter.convert(employeeDto);
        if (!StringUtils.hasText(employee.getPassword())) {
            throw new BadRequestException("Password is required.");
        }
        validatePassword(employee.getPassword());

        Employee employeeToSave = new EmployeeBuilder(employee)
                .withPassword(passwordEncoder.encode(employee.getPassword()))
                .build();

        return EmployeeBeanToDtoConverter.convert(employeesDao.save(employeeToSave));
    }

    @Override
    @Transactional
    public EmployeeDTO update(int employeeId, EmployeeDTO employeeDto) {
        Employee existing = getEmployee(employeeId);
        Employee employee = EmployeeDtoToBeanConverter.convert(employeeDto);
        String newPassword = employee.getPassword();
        if (StringUtils.hasText(newPassword)) {
            validatePassword(newPassword);
        }

        Employee employeeToUpdate = new EmployeeBuilder(employee)
                .withEmployeeId(employeeId)
                .withPassword(StringUtils.hasText(newPassword) ? passwordEncoder.encode(newPassword) : existing.getPassword())
                .withCreatedAt(existing.getCreatedAt())
                .build();

        return EmployeeBeanToDtoConverter.convert(employeesDao.save(employeeToUpdate));
    }

    @Override
    @Transactional
    public void delete(int employeeId) {
        Employee employeeToDelete = getEmployee(employeeId);

        if (!ordersDao.findAllByEmployee(employeeToDelete).isEmpty()) {
            throw new ConflictException("There are orders connected to this employee. It can't be deleted.");
        }

        employeesDao.delete(employeeToDelete);
    }

    @Override
    @Transactional
    public void changeOwnPassword(String username, String currentPassword, String newPassword) {
        Employee employee = employeesDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!passwordEncoder.matches(currentPassword, employee.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }
        validatePassword(newPassword);

        employeesDao.save(new EmployeeBuilder(employee)
                .withPassword(passwordEncoder.encode(newPassword))
                .build());
    }

    private Employee getEmployee(int employeeId) {
        return employeesDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private static void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new BadRequestException("Password is too long (max " + MAX_PASSWORD_BYTES + " bytes).");
        }
    }
}
