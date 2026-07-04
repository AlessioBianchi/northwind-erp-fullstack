package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name="SHIPPERS")
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shipperId;

    private String companyName;
    private String phone;

    public Shipper() {}

    public Shipper(int shipperId, String companyName, String phone) {
        this.shipperId = shipperId;
        this.companyName = companyName;
        this.phone = phone;
    }

    public int getShipperId() { return shipperId; }
    public String getCompanyName() { return companyName; }
    public String getPhone() { return phone; }
}