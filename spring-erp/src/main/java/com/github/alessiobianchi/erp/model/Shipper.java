package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="SHIPPERS")
public class Shipper extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shipperId;

    @NotBlank
    @Size(max = 40)
    private String companyName;

    @Size(max = 24)
    private String phone;

    public Shipper() {}

    public Shipper(Integer shipperId, String companyName, String phone) {
        this.shipperId = shipperId;
        this.companyName = companyName;
        this.phone = phone;
    }

    public Integer getShipperId() { return shipperId; }
    public String getCompanyName() { return companyName; }
    public String getPhone() { return phone; }
}