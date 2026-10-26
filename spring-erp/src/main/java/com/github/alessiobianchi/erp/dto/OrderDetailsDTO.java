package com.github.alessiobianchi.erp.dto;

public class OrderDetailsDTO {

    private Integer orderId;
    private Integer productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
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
