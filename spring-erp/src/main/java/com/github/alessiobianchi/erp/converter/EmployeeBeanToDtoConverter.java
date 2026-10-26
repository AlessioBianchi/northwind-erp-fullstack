package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.dto.EmployeeDTOBuilder;
import com.github.alessiobianchi.erp.model.Employee;

public class EmployeeBeanToDtoConverter {

    public static EmployeeDTO convert(Employee employee) {
        return new EmployeeDTOBuilder()
                .withEmployeeId(employee.getEmployeeId())
                .withLastname(employee.getLastName())
                .withFirstname(employee.getFirstName())
                .withTitle(employee.getTitle())
                .withTitleOfCourtesy(employee.getTitleOfCourtesy())
                .withBirthDate(employee.getBirthDate())
                .withHireDate(employee.getHireDate())
                .withAddress(employee.getAddress())
                .withCity(employee.getCity())
                .withRegion(employee.getRegion())
                .withPostalCode(employee.getPostalCode())
                .withCountry(employee.getCountry())
                .withHomePhone(employee.getHomePhone())
                .withExtension(employee.getExtension())
                .withNotes(employee.getNotes())
                .withReportsTo(employee.getReportsTo() != null ? convert(employee.getReportsTo()) : null)
                .withUsername(employee.getUsername())
                .build();
    }
}
