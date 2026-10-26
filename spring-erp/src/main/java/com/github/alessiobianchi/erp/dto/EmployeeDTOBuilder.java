package com.github.alessiobianchi.erp.dto;

import java.time.LocalDate;

public class EmployeeDTOBuilder {

    private int employeeId;
    private String lastName;
    private String firstName;
    private String title;
    private String titleOfCourtesy;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String homePhone;
    private String extension;
    private String notes;
    private EmployeeDTO reportsTo;
    private String username;
    private String password;

    public EmployeeDTOBuilder() {
    }

    public EmployeeDTOBuilder(EmployeeDTO dto) {
        this.employeeId = dto.getEmployeeId();
        this.lastName = dto.getLastName();
        this.firstName = dto.getFirstName();
        this.title = dto.getTitle();
        this.titleOfCourtesy = dto.getTitleOfCourtesy();
        this.birthDate = dto.getBirthDate();
        this.hireDate = dto.getHireDate();
        this.address = dto.getAddress();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.postalCode = dto.getPostalCode();
        this.country = dto.getCountry();
        this.homePhone = dto.getHomePhone();
        this.extension = dto.getExtension();
        this.notes = dto.getNotes();
        this.reportsTo = dto.getReportsTo();
        this.username = dto.getUsername();
        this.password = dto.getPassword();
    }

    public EmployeeDTOBuilder withEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public EmployeeDTOBuilder withLastname(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public EmployeeDTOBuilder withFirstname(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public EmployeeDTOBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public EmployeeDTOBuilder withTitleOfCourtesy(String titleOfCourtesy) {
        this.titleOfCourtesy = titleOfCourtesy;
        return this;
    }

    public EmployeeDTOBuilder withBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public EmployeeDTOBuilder withHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
        return this;
    }

    public EmployeeDTOBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public EmployeeDTOBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public EmployeeDTOBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public EmployeeDTOBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public EmployeeDTOBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public EmployeeDTOBuilder withHomePhone(String homePhone) {
        this.homePhone = homePhone;
        return this;
    }

    public EmployeeDTOBuilder withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public EmployeeDTOBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public EmployeeDTOBuilder withReportsTo(EmployeeDTO reportsTo) {
        this.reportsTo = reportsTo;
        return this;
    }

    public EmployeeDTOBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public EmployeeDTOBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public EmployeeDTO build() {
        return new EmployeeDTO(
                employeeId,
                lastName,
                firstName,
                title,
                titleOfCourtesy,
                birthDate,
                hireDate,
                address,
                city,
                region,
                postalCode,
                country,
                homePhone,
                extension,
                notes,
                reportsTo,
                username,
                password
        );
    }
}
