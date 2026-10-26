package com.github.alessiobianchi.erp.dto;

public class OrderDetailsDTOBuilder {

    private Integer orderId;
    private Integer productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double discount;

    public OrderDetailsDTOBuilder() {
    }

    public OrderDetailsDTOBuilder(OrderDetailsDTO dto) {
        this.orderId = dto.getOrderId();
        this.productId = dto.getProductId();
        this.productName = dto.getProductName();
        this.unitPrice = dto.getUnitPrice();
        this.quantity = dto.getQuantity();
        this.discount = dto.getDiscount();
    }

    public OrderDetailsDTOBuilder withOrderId(Integer orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderDetailsDTOBuilder withProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public OrderDetailsDTOBuilder withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public OrderDetailsDTOBuilder withUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public OrderDetailsDTOBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderDetailsDTOBuilder withDiscount(Double discount) {
        this.discount = discount;
        return this;
    }

    public OrderDetailsDTO build() {
        return new OrderDetailsDTO(
                orderId,
                productId,
                productName,
                unitPrice,
                quantity,
                discount
        );
    }
}
