package com.github.alessiobianchi.erp.model;

import java.time.Instant;

public class SupplierBuilder {

    private Integer supplierId;
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
    private String homepage;
    private Instant createdAt;

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
        this.homepage = supplier.getHomepage();
        this.createdAt = supplier.getCreatedAt();
    }

    public SupplierBuilder withSupplierId(Integer supplierId) {
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

    public SupplierBuilder withHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }

    public SupplierBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Supplier build() {
        Supplier supplier = new Supplier(
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
                homepage
        );
        supplier.setCreatedAt(createdAt);
        return supplier;
    }
}