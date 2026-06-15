package com.github.alessiobianchi.erp.model;

public class CustomerBuilder {

    private Long customerId;
    private String customerCode;
    private String companyName;
    private String contactName;
    private String contactTitle;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String phone;
    private String fax;

    public CustomerBuilder() {
    }

    public CustomerBuilder(Customer customer) {
        this.customerId = customer.getCustomerId();
        this.customerCode = customer.getCustomerCode();
        this.companyName = customer.getCompanyName();
        this.contactName = customer.getContactName();
        this.contactTitle = customer.getContactTitle();
        this.address = customer.getAddress();
        this.city = customer.getCity();
        this.region = customer.getRegion();
        this.postalCode = customer.getPostalCode();
        this.country = customer.getCountry();
        this.phone = customer.getPhone();
        this.fax = customer.getFax();
    }

    public CustomerBuilder withCustomerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public CustomerBuilder withCustomerCode(String customerCode) {
        this.customerCode = customerCode;
        return this;
    }

    public CustomerBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public CustomerBuilder withContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public CustomerBuilder withContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
        return this;
    }

    public CustomerBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public CustomerBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public CustomerBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public CustomerBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public CustomerBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public CustomerBuilder withFax(String fax) {
        this.fax = fax;
        return this;
    }

    public Customer build() {
        return new Customer(
                customerId,
                customerCode,
                companyName,
                contactName,
                contactTitle,
                address,
                city,
                region,
                postalCode,
                country,
                phone,
                fax
        );
    }
}