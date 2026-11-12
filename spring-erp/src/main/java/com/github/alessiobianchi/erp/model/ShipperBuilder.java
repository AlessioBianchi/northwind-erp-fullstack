package com.github.alessiobianchi.erp.model;

import java.time.Instant;

public class ShipperBuilder {

    private Integer shipperId;
    private String companyName;
    private String phone;
    private Instant createdAt;

    public ShipperBuilder() {}

    public ShipperBuilder(Shipper shipper) {
        this.shipperId = shipper.getShipperId();
        this.companyName = shipper.getCompanyName();
        this.phone = shipper.getPhone();
        this.createdAt = shipper.getCreatedAt();
    }

    public ShipperBuilder withShipperId(Integer shipperId) {
        this.shipperId = shipperId;
        return this;
    }

    public ShipperBuilder withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public ShipperBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ShipperBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Shipper build() {
        Shipper shipper = new Shipper(shipperId, companyName, phone);
        shipper.setCreatedAt(createdAt);
        return shipper;
    }
}