package com.github.alessiobianchi.erp.model;

public class SupplierBuilder {

    private Long supplierId;
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
    private String homePage;

    public SupplierBuilder() {
    }

    public SupplierBuilder(Supplier supplier) {
        this.supplierId = supplier.getSupplierId();
        this.companyName = supplier.getCompanyName();
        this.contactName = supplier.getContactName();
        this.contactTitle = supplier.getContactTitle();
        this.address = supplier.getAddress();
        this.city = supplier.getCity();
        this.region = supplier.getRegion();
        this.postalCode = supplier.getPostalCode();
        this.country = supplier.getCountry();
        this.phone = supplier.getPhone();
        this.fax = supplier.getFax();
        this.homePage = supplier.getHomePage();
    }

    public SupplierBuilder withSupplierId(Long supplierId) {
        this.supplierId = supplierId;
        return this;
    }

    public SupplierBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public SupplierBuilder withContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public SupplierBuilder withContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
        return this;
    }

    public SupplierBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public SupplierBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public SupplierBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public SupplierBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public SupplierBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public SupplierBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public SupplierBuilder withFax(String fax) {
        this.fax = fax;
        return this;
    }

    public SupplierBuilder withHomePage(String homePage) {
        this.homePage = homePage;
        return this;
    }

    public Supplier build() {
        return new Supplier(
                supplierId,
                companyName,
                contactName,
                contactTitle,
                address,
                city,
                region,
                postalCode,
                country,
                phone,
                fax,
                homePage
        );
    }
}