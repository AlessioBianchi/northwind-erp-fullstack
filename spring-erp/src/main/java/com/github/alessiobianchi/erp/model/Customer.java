package com.github.alessiobianchi.erp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="CUSTOMERS")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;

    @NotBlank
    @Size(max = 5)
    private String customerCode;

    @NotBlank
    @Size(max = 40)
    private String companyName;

    @Size(max = 30)
    private String contactName;

    @Size(max = 30)
    private String contactTitle;

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
    private String phone;

    @Size(max = 24)
    private String fax;

    public Customer() {
    }

    public Customer(int customerId, String customerCode, String companyName, String contactName,
                    String contactTitle, String address, String city, String region,
                    String postalCode, String country, String phone, String fax) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.companyName = companyName;
        this.contactName = contactName;
        this.contactTitle = contactTitle;
        this.address = address;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.phone = phone;
        this.fax = fax;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactTitle() {
        return contactTitle;
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

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }
}