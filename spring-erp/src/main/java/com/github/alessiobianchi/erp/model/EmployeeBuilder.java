package com.github.alessiobianchi.erp.model;

import java.time.LocalDate;

public class EmployeeBuilder {

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
    private Employee reportsTo;
    private String username;
    private String password;

    public EmployeeBuilder() {
    }

    public EmployeeBuilder(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.lastName = employee.getLastName();
        this.firstName = employee.getFirstName();
        this.title = employee.getTitle();
        this.titleOfCourtesy = employee.getTitleOfCourtesy();
        this.birthDate = employee.getBirthDate();
        this.hireDate = employee.getHireDate();
        this.address = employee.getAddress();
        this.city = employee.getCity();
        this.region = employee.getRegion();
        this.postalCode = employee.getPostalCode();
        this.country = employee.getCountry();
        this.homePhone = employee.getHomePhone();
        this.extension = employee.getExtension();
        this.notes = employee.getNotes();
        this.reportsTo = employee.getReportsTo();
        this.username = employee.getUsername();
        this.password = employee.getPassword();
    }

    public EmployeeBuilder withEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public EmployeeBuilder withLastname(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public EmployeeBuilder withFirstname(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public EmployeeBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public EmployeeBuilder withTitleOfCourtesy(String titleOfCourtesy) {
        this.titleOfCourtesy = titleOfCourtesy;
        return this;
    }

    public EmployeeBuilder withBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public EmployeeBuilder withHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
        return this;
    }

    public EmployeeBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public EmployeeBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public EmployeeBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public EmployeeBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public EmployeeBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public EmployeeBuilder withHomePhone(String homePhone) {
        this.homePhone = homePhone;
        return this;
    }

    public EmployeeBuilder withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public EmployeeBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public EmployeeBuilder withReportsTo(Employee reportsTo) {
        this.reportsTo = reportsTo;
        return this;
    }

    public EmployeeBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public EmployeeBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public Employee build() {
        return new Employee(
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
