package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name="SHIPPERS")
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NW_SHIPPERS")
    @SequenceGenerator(
            name = "SEQ_NW_SHIPPERS",
            sequenceName = "SEQ_NW_SHIPPERS",
            allocationSize = 1
    )
    private Long shipperId;

    private String companyName;
    private String phone;

    public Shipper() {}

    public Shipper(Long shipperId, String companyName, String phone) {
        this.shipperId = shipperId;
        this.companyName = companyName;
        this.phone = phone;
    }

    public Long getShipperId() { return shipperId; }
    public String getCompanyName() { return companyName; }
    public String getPhone() { return phone; }
}