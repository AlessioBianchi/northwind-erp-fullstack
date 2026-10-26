package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.EmployeeDTO;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;

public class EmployeeDtoToBeanConverter {

    public static Employee convert(EmployeeDTO dto) {
        return new EmployeeBuilder()
                .withEmployeeId(dto.getEmployeeId())
                .withLastname(dto.getLastName())
                .withFirstname(dto.getFirstName())
                .withTitle(dto.getTitle())
                .withTitleOfCourtesy(dto.getTitleOfCourtesy())
                .withBirthDate(dto.getBirthDate())
                .withHireDate(dto.getHireDate())
                .withAddress(dto.getAddress())
                .withCity(dto.getCity())
                .withRegion(dto.getRegion())
                .withPostalCode(dto.getPostalCode())
                .withCountry(dto.getCountry())
                .withHomePhone(dto.getHomePhone())
                .withExtension(dto.getExtension())
                .withNotes(dto.getNotes())
                .withReportsTo(dto.getReportsTo() != null ? convert(dto.getReportsTo()) : null)
                .withUsername(dto.getUsername())
                .withPassword(dto.getPassword())
                .build();
    }
}
