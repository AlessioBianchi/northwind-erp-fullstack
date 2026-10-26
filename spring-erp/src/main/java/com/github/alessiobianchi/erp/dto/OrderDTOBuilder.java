package com.github.alessiobianchi.erp.dto;

import java.time.LocalDate;

public class OrderDTOBuilder {

    private Integer orderId;
    private Integer customerId;
    private String customerName;
    private Integer employeeId;
    private String employeeName;
    private LocalDate orderDate;
    private LocalDate requiredDate;
    private LocalDate shippedDate;
    private Integer shipperId;
    private String shipperName;
    private Double freight;
    private String shipName;
    private String shipAddress;
    private String shipCity;
    private String shipRegion;
    private String shipPostalCode;
    private String shipCountry;

    public OrderDTOBuilder() {
    }

    public OrderDTOBuilder(OrderDTO dto) {
        this.orderId = dto.getOrderId();
        this.customerId = dto.getCustomerId();
        this.customerName = dto.getCustomerName();
        this.employeeId = dto.getEmployeeId();
        this.employeeName = dto.getEmployeeName();
        this.orderDate = dto.getOrderDate();
        this.requiredDate = dto.getRequiredDate();
        this.shippedDate = dto.getShippedDate();
        this.shipperId = dto.getShipperId();
        this.shipperName = dto.getShipperName();
        this.freight = dto.getFreight();
        this.shipName = dto.getShipName();
        this.shipAddress = dto.getShipAddress();
        this.shipCity = dto.getShipCity();
        this.shipRegion = dto.getShipRegion();
        this.shipPostalCode = dto.getShipPostalCode();
        this.shipCountry = dto.getShipCountry();
    }

    public OrderDTOBuilder withOrderId(Integer orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderDTOBuilder withCustomerId(Integer customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderDTOBuilder withCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public OrderDTOBuilder withEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public OrderDTOBuilder withEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        return this;
    }

    public OrderDTOBuilder withOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public OrderDTOBuilder withRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
        return this;
    }

    public OrderDTOBuilder withShippedDate(LocalDate shippedDate) {
        this.shippedDate = shippedDate;
        return this;
    }

    public OrderDTOBuilder withShipperId(Integer shipperId) {
        this.shipperId = shipperId;
        return this;
    }

    public OrderDTOBuilder withShipperName(String shipperName) {
        this.shipperName = shipperName;
        return this;
    }

    public OrderDTOBuilder withFreight(Double freight) {
        this.freight = freight;
        return this;
    }

    public OrderDTOBuilder withShipName(String shipName) {
        this.shipName = shipName;
        return this;
    }

    public OrderDTOBuilder withShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
        return this;
    }

    public OrderDTOBuilder withShipCity(String shipCity) {
        this.shipCity = shipCity;
        return this;
    }

    public OrderDTOBuilder withShipRegion(String shipRegion) {
        this.shipRegion = shipRegion;
        return this;
    }

    public OrderDTOBuilder withShipPostalCode(String shipPostalCode) {
        this.shipPostalCode = shipPostalCode;
        return this;
    }

    public OrderDTOBuilder withShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
        return this;
    }

    public OrderDTO build() {
        return new OrderDTO(
                orderId,
                customerId,
                customerName,
                employeeId,
                employeeName,
                orderDate,
                requiredDate,
                shippedDate,
                shipperId,
                shipperName,
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
