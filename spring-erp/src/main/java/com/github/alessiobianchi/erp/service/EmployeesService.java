package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.config.PasswordConfig;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<Employee> findByUsername(String username) { return employeesDao.findByUsername(username); }

    public List<Employee> findAllByOrderByEmployeeIdDesc() {
        return employeesDao.findAllByOrderByEmployeeIdDesc();
    }

    public Page<Employee> findAllByOrderByEmployeeIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return employeesDao.findAllByOrderByEmployeeIdDesc(pageable);
    }

    public Employee create(Employee employee) {
        Employee employeeToSave = new EmployeeBuilder(employee)
                .withPassword(passwordEncoder.encode(employee.getPassword()))
                .build();

        return employeesDao.save(employeeToSave);
    }

    public Employee update(int employeeId, Employee employee) {
        String oldPassword = employeesDao.findById(employeeId)
                .map(Employee::getPassword)
                .orElse(null);
        String newPassword = passwordEncoder.encode(employee.getPassword());

        EmployeeBuilder employeeToUpdateBuilder = new EmployeeBuilder(employee)
                .withEmployeeId(employeeId);
        if (!Objects.equals(newPassword, oldPassword)) {
            employeeToUpdateBuilder.withPassword(newPassword);
        }

        return employeesDao.save(employeeToUpdateBuilder.build());
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
