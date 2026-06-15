package com.github.alessiobianchi.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name="ORDER_DETAILS")
@IdClass(OrderDetailId.class)
public class OrderDetail {

    @Id
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @Id
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private Double unitPrice;
    private Integer quantity;
    private Double discount;

    public OrderDetail() {}

    public OrderDetail(Order order,
                       Product product,
                       Double unitPrice,
                       Integer quantity,
                       Double discount) {
        this.order = order;
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public Order getOrder() { return order; }
    public Product getProduct() { return product; }
    public Double getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public Double getDiscount() { return discount; }
}