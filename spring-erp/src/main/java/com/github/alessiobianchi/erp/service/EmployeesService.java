package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.converter.EmployeeBeanToDtoConverter;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeesService {

    private final EmployeesDAO employeesDao;
    private final OrdersDAO ordersDao;
    private final PasswordEncoder passwordEncoder;

    public EmployeesService(EmployeesDAO employeesDao, OrdersDAO ordersDao, PasswordEncoder passwordEncoder) {
        this.employeesDao = employeesDao;
        this.ordersDao = ordersDao;
        this.passwordEncoder = passwordEncoder;
    }

    public EmployeeDTO findByUsername(String username) {
        Employee employee = employeesDao.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return EmployeeBeanToDtoConverter.convert(employee);
    }

    public List<EmployeeDTO> findAllByOrderByEmployeeIdDesc() {
        return employeesDao.findAllByOrderByEmployeeIdDesc().stream()
                .map(EmployeeBeanToDtoConverter::convert)
                .toList();
    }

    public Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return employeesDao.findAllByOrderByEmployeeIdDesc(pageable)
                .map(EmployeeBeanToDtoConverter::convert);
    }

    public EmployeeDTO create(Employee employee) {
        Employee employeeToSave = new EmployeeBuilder(employee)
                .withPassword(passwordEncoder.encode(employee.getPassword()))
                .build();

        return EmployeeBeanToDtoConverter.convert(employeesDao.save(employeeToSave));
    }

    public EmployeeDTO update(int employeeId, Employee employee) {
        String oldPassword = employeesDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"))
                .getPassword();
        String newPassword = employee.getPassword();

        Employee employeeToUpdate = new EmployeeBuilder(employee)
                .withEmployeeId(employeeId)
                .withPassword(newPassword == null || newPassword.isBlank() ? oldPassword : passwordEncoder.encode(newPassword))
                .build();

        return EmployeeBeanToDtoConverter.convert(employeesDao.save(employeeToUpdate));
    }

    public boolean delete(int employeeId) {
        Employee employeeToDelete = new EmployeeBuilder()
                .withEmployeeId(employeeId)
                .build();

        if (!ordersDao.findAllByEmployee(employeeToDelete).isEmpty()) {
            return false;
        }

        employeesDao.delete(employeeToDelete);
        return true;
    }
}
