package com.github.alessiobianchi.erp.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderDetailsDTO {

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer productId;

    private String productName;
    private Double unitPrice;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @DecimalMin("0")
    @DecimalMax("1")
    private Double discount;

    public OrderDetailsDTO() {}

    public OrderDetailsDTO(Integer orderId,
                           Integer productId,
                           String productName,
                           Double unitPrice,
                           Integer quantity,
                           Double discount) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public Integer getOrderId() { return orderId; }
    public Integer getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public Double getDiscount() { return discount; }
}
