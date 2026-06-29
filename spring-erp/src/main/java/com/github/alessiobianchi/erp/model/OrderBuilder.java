package com.github.alessiobianchi.erp.model;

import java.util.Date;

public class OrderBuilder {

    private Long orderId;
    private Customer customer;
    private Employee employee;
    private Date orderDate;
    private Date requiredDate;
    private Date shippedDate;
    private Shipper shipper;
    private Double freight;
    private String shipName;
    private String shipAddress;
    private String shipCity;
    private String shipRegion;
    private String shipPostalCode;
    private String shipCountry;

    public OrderBuilder() {
    }

    public OrderBuilder(Order order) {
        this.orderId = order.getOrderId();
        this.customer = order.getCustomer();
        this.employee = order.getEmployee();
        this.orderDate = order.getOrderDate();
        this.requiredDate = order.getRequiredDate();
        this.shippedDate = order.getShippedDate();
        this.shipper = order.getShipper();
        this.freight = order.getFreight();
        this.shipName = order.getShipName();
        this.shipAddress = order.getShipAddress();
        this.shipCity = order.getShipCity();
        this.shipRegion = order.getShipRegion();
        this.shipPostalCode = order.getShipPostalCode();
        this.shipCountry = order.getShipCountry();
    }

    public OrderBuilder withOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderBuilder withCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderBuilder withEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public OrderBuilder withOrderDate(Date orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public OrderBuilder withRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
        return this;
    }

    public OrderBuilder withShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
        return this;
    }

    public OrderBuilder withShipper(Shipper shipper) {
        this.shipper = shipper;
        return this;
    }

    public OrderBuilder withFreight(Double freight) {
        this.freight = freight;
        return this;
    }

    public OrderBuilder withShipName(String shipName) {
        this.shipName = shipName;
        return this;
    }

    public OrderBuilder withShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
        return this;
    }

    public OrderBuilder withShipCity(String shipCity) {
        this.shipCity = shipCity;
        return this;
    }

    public OrderBuilder withShipRegion(String shipRegion) {
        this.shipRegion = shipRegion;
        return this;
    }

    public OrderBuilder withShipPostalCode(String shipPostalCode) {
        this.shipPostalCode = shipPostalCode;
        return this;
    }

    public OrderBuilder withShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
        return this;
    }

    public Order build() {
        return new Order(
                orderId,
                customer,
                employee,
                orderDate,
                requiredDate,
                shippedDate,
                shipper,
                freight,
                shipName,
                shipAddress,
                shipCity,
                shipRegion,
                shipPostalCode,
                shipCountry
        );
    }
}