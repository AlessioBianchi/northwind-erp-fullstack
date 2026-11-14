package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeesService {

    EmployeeDTO findByUsername(String username);

    List<EmployeeDTO> findAllByOrderByEmployeeIdDesc();

    Page<EmployeeDTO> findAllByOrderByEmployeeIdDesc(int pageNumber, int pageSize, String search);

    EmployeeDTO create(EmployeeDTO employeeDto);

    EmployeeDTO update(int employeeId, EmployeeDTO employeeDto);

    void delete(int employeeId);

    void changeOwnPassword(String username, String currentPassword, String newPassword);
}
