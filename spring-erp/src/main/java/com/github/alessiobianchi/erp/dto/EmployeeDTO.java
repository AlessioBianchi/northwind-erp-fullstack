package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public class EmployeeDTO {

    private Integer employeeId;

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

    private LocalDate birthDate;
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
    private EmployeeDTO reportsTo;

    @Size(max = 50)
    private String username;

    // BCrypt only uses the first 72 bytes of the raw password.
    @Size(max = 72)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;

    public EmployeeDTO() {}

    public EmployeeDTO(Integer employeeId,
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
                       EmployeeDTO reportsTo,
                       String username,
                       String password,
                       Instant createdAt,
                       Instant updatedAt) {
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getEmployeeId() {
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

    public EmployeeDTO getReportsTo() {
        return reportsTo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
