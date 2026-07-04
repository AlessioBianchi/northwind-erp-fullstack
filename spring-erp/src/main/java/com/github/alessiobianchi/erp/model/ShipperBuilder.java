package com.github.alessiobianchi.erp.model;

public class ShipperBuilder {

    private int shipperId;
    private String companyName;
    private String phone;

    public ShipperBuilder() {}

    public ShipperBuilder(Shipper shipper) {
        this.shipperId = shipper.getShipperId();
        this.companyName = shipper.getCompanyName();
        this.phone = shipper.getPhone();
    }

    public ShipperBuilder withShipperId(int shipperId) {
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

    public Shipper build() {
        return new Shipper(shipperId, companyName, phone);
    }
}