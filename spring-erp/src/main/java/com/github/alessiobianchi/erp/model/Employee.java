package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name="EMPLOYEES")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeId;

    @NotBlank
    @Size(max = 20)
    private String lastName;

    @NotBlank
    @Size(max = 10)
    private String firstName;

    @Size(max = 30)
    private String title;

    @Size(max = 25)
    private String titleOfCourtesy;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    private LocalDate birthDate;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    private LocalDate hireDate;

    @Size(max = 60)
    private String address;

    @Size(max = 15)
    private String city;

    @Size(max = 15)
    private String region;

    @Size(max = 10)
    private String postalCode;

    @Size(max = 15)
    private String country;

    @Size(max = 24)
    private String homePhone;

    @Size(max = 4)
    private String extension;

    private String notes;


    @ManyToOne
    @JoinColumn(name = "reports_to")
    private Employee reportsTo;

    @Size(max = 50)
    private String username;

    // BCrypt only uses the first 72 bytes of the raw password; the stored hash is 60 chars.
    @Size(max = 72)
    private String password;

    public Employee() {}

    public Employee(int employeeId,
                String lastName,
                String firstName,
                String title,
                String titleOfCourtesy,
                LocalDate birthDate,
                LocalDate hireDate,
                String address,
                String city,
                String region,
                String postalCode,
                String country,
                String homePhone,
                String extension,
                String notes,
                Employee reportsTo,
                String username,
                String password) {
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.title = title;
        this.titleOfCourtesy = titleOfCourtesy;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.homePhone = homePhone;
        this.extension = extension;
        this.notes = notes;
        this.reportsTo = reportsTo;
        this.username = username;
        this.password = password;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleOfCourtesy() {
        return titleOfCourtesy;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getExtension() {
        return extension;
    }

    public String getNotes() {
        return notes;
    }

    public Employee getReportsTo() {
        return reportsTo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
