package com.github.alessiobianchi.erp.model;

public class OrderDetailBuilder {

    private Order order;
    private Product product;
    private Double unitPrice;
    private Integer quantity;
    private Double discount;

    public OrderDetailBuilder() {
    }

    public OrderDetailBuilder(OrderDetail orderDetail) {
        this.order = orderDetail.getOrder();
        this.product = orderDetail.getProduct();
        this.unitPrice = orderDetail.getUnitPrice();
        this.quantity = orderDetail.getQuantity();
        this.discount = orderDetail.getDiscount();
    }

    public OrderDetailBuilder withOrder(Order order) {
        this.order = order;
        return this;
    }

    public OrderDetailBuilder withProduct(Product product) {
        this.product = product;
        return this;
    }

    public OrderDetailBuilder withUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public OrderDetailBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderDetailBuilder withDiscount(Double discount) {
        this.discount = discount;
        return this;
    }

    public OrderDetail build() {
        return new OrderDetail(
                order,
                product,
                unitPrice,
                quantity,
                discount
        );
    }
}