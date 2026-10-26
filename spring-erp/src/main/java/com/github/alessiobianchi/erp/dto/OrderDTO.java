package com.github.alessiobianchi.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class OrderDTO {

    private Integer orderId;
    private Integer customerId;
    private String customerName;
    private Integer employeeId;
    private String employeeName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate requiredDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate shippedDate;

    private Integer shipperId;
    private String shipperName;

    @PositiveOrZero
    private Double freight;

    @Size(max = 40)
    private String shipName;

    @Size(max = 60)
    private String shipAddress;

    @Size(max = 15)
    private String shipCity;

    @Size(max = 15)
    private String shipRegion;

    @Size(max = 10)
    private String shipPostalCode;

    @Size(max = 15)
    private String shipCountry;

    public OrderDTO() {}

    public OrderDTO(Integer orderId,
                    Integer customerId,
                    String customerName,
                    Integer employeeId,
                    String employeeName,
                    LocalDate orderDate,
                    LocalDate requiredDate,
                    LocalDate shippedDate,
                    Integer shipperId,
                    String shipperName,
                    Double freight,
                    String shipName,
                    String shipAddress,
                    String shipCity,
                    String shipRegion,
                    String shipPostalCode,
                    String shipCountry) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.shippedDate = shippedDate;
        this.shipperId = shipperId;
        this.shipperName = shipperName;
        this.freight = freight;
        this.shipName = shipName;
        this.shipAddress = shipAddress;
        this.shipCity = shipCity;
        this.shipRegion = shipRegion;
        this.shipPostalCode = shipPostalCode;
        this.shipCountry = shipCountry;
    }

    public Integer getOrderId() { return orderId; }
    public Integer getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public Integer getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getOrderDate() { return orderDate; }
    public LocalDate getRequiredDate() { return requiredDate; }
    public LocalDate getShippedDate() { return shippedDate; }
    public Integer getShipperId() { return shipperId; }
    public String getShipperName() { return shipperName; }
    public Double getFreight() { return freight; }
    public String getShipName() { return shipName; }
    public String getShipAddress() { return shipAddress; }
    public String getShipCity() { return shipCity; }
    public String getShipRegion() { return shipRegion; }
    public String getShipPostalCode() { return shipPostalCode; }
    public String getShipCountry() { return shipCountry; }
}
