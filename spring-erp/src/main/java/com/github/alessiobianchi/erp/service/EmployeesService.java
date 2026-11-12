package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeesService {

    EmployeeDTO findByUsername(String username);

    List<EmployeeDTO> findAllByOrderByEmployeeIdDesc();

    Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(int pageNumber, int pageSize);

    EmployeeDTO create(Employee employee);

    EmployeeDTO update(int employeeId, Employee employee);

    void delete(int employeeId);

    void changeOwnPassword(String username, String currentPassword, String newPassword);
}
