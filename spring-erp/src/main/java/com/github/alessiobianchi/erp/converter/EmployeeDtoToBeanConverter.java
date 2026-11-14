package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;

public class EmployeeDtoToBeanConverter {

    public static Employee convert(EmployeeDTO dto) {
        return new EmployeeBuilder()
                .withEmployeeId(dto.employeeId())
                .withLastname(dto.lastName())
                .withFirstname(dto.firstName())
                .withTitle(dto.title())
                .withTitleOfCourtesy(dto.titleOfCourtesy())
                .withBirthDate(dto.birthDate())
                .withHireDate(dto.hireDate())
                .withAddress(dto.address())
                .withCity(dto.city())
                .withRegion(dto.region())
                .withPostalCode(dto.postalCode())
                .withCountry(dto.country())
                .withHomePhone(dto.homePhone())
                .withExtension(dto.extension())
                .withNotes(dto.notes())
                .withReportsTo(dto.reportsTo() != null ? convert(dto.reportsTo()) : null)
                .withUsername(dto.username())
                .withPassword(dto.password())
                .build();
    }
}
