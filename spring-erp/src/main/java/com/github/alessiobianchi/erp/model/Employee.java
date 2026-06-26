package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name="EMPLOYEES")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NW_EMPLOYEES")
    @SequenceGenerator(
            name = "SEQ_NW_EMPLOYEES",
            sequenceName = "SEQ_NW_EMPLOYEES",
            allocationSize = 1
    )
    private Long employeeId;

    private String lastname;
    private String firstname;
    private String title;
    private String titleOfCourtesy;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    private Date birthdate;

    @DateTimeFormat(pattern = "yyy-MM-dd")
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


    @ManyToOne
    @JoinColumn(name = "reports_to")
    private Employee reportsTo;

    private String username;
    private String password;

    public Employee() {}

    public Employee(Long employeeId,
                String lastname,
                String firstname,
                String title,
                String titleOfCourtesy,
                Date birthdate,
                Date hiredate,
                String address,
                String city,
                String region,
                String postalCode,
                String country,
                String homePhone,
                String extension,
                String photo,
                String notes,
                Employee reportsTo,
                String username,
                String password) {
        this.employeeId = employeeId;
        this.lastname = lastname;
        this.firstname = firstname;
        this.title = title;
        this.titleOfCourtesy = titleOfCourtesy;
        this.birthdate = birthdate;
        this.hiredate = hiredate;
        this.address = address;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.homePhone = homePhone;
        this.extension = extension;
        this.photo = photo;
        this.notes = notes;
        this.reportsTo = reportsTo;
        this.username = username;
        this.password = password;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleOfCourtesy() {
        return titleOfCourtesy;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public Date getHiredate() {
        return hiredate;
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

    public String getPhoto() {
        return photo;
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
