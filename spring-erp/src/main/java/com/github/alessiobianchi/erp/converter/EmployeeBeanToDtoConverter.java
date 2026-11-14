package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;

public class EmployeeBeanToDtoConverter {

    public static EmployeeDTO convert(Employee employee) {
        return new EmployeeDTO(
                employee.getEmployeeId(),
                employee.getLastName(),
                employee.getFirstName(),
                employee.getTitle(),
                employee.getTitleOfCourtesy(),
                employee.getBirthDate(),
                employee.getHireDate(),
                employee.getAddress(),
                employee.getCity(),
                employee.getRegion(),
                employee.getPostalCode(),
                employee.getCountry(),
                employee.getHomePhone(),
                employee.getExtension(),
                employee.getNotes(),
                employee.getReportsTo() != null ? convert(employee.getReportsTo()) : null,
                employee.getUsername(),
                null,
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }
}
