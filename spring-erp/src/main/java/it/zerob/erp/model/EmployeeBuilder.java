package it.zerob.erp.model;

import java.util.Date;

public class EmployeeBuilder {

    private Long employeeId;
    private String lastname;
    private String firstname;
    private String title;
    private String titleOfCourtesy;
    private Date birthdate;
    private Date hiredate;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String homePhone;
    private String extension;
    private String photo;
    private String notes;
    private Employee reportsTo;
    private String username;
    private String password;

    public EmployeeBuilder() {
    }

    public EmployeeBuilder(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.lastname = employee.getLastname();
        this.firstname = employee.getFirstname();
        this.title = employee.getTitle();
        this.titleOfCourtesy = employee.getTitleOfCourtesy();
        this.birthdate = employee.getBirthdate();
        this.hiredate = employee.getHiredate();
        this.address = employee.getAddress();
        this.city = employee.getCity();
        this.region = employee.getRegion();
        this.postalCode = employee.getPostalCode();
        this.country = employee.getCountry();
        this.homePhone = employee.getHomePhone();
        this.extension = employee.getExtension();
        this.photo = employee.getPhoto();
        this.notes = employee.getNotes();
        this.reportsTo = employee.getReportsTo();
        this.username = employee.getUsername();
        this.password = employee.getPassword();
    }

    public EmployeeBuilder withEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public EmployeeBuilder withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public EmployeeBuilder withFirstname(String firstname) {
        this.firstname = firstname;
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

    public EmployeeBuilder withBirthdate(Date birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public EmployeeBuilder withHiredate(Date hiredate) {
        this.hiredate = hiredate;
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

    public EmployeeBuilder withPhoto(String photo) {
        this.photo = photo;
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
                lastname,
                firstname,
                title,
                titleOfCourtesy,
                birthdate,
                hiredate,
                address,
                city,
                region,
                postalCode,
                country,
                homePhone,
                extension,
                photo,
                notes,
                reportsTo,
                username,
                password
        );
    }
}
