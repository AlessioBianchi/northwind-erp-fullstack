package it.zerob.erp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name="ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NW_ORDERS")
    @SequenceGenerator(
            name = "SEQ_NW_ORDERS",
            sequenceName = "SEQ_NW_ORDERS",
            allocationSize = 1
    )
    private Long orderId;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date orderDate;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date requiredDate;

    @DateTimeFormat(pattern = "yyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date shippedDate;

    @ManyToOne
    @JoinColumn(name="ship_via")
    private Shipper shipper;

    private Double freight;
    private String shipName;
    private String shipAddress;
    private String shipCity;
    private String shipRegion;
    private String shipPostalCode;
    private String shipCountry;

    public Order() {}

    public Order(Long orderId,
                 Customer customer,
                 Employee employee,
                 Date orderDate,
                 Date requiredDate,
                 Date shippedDate,
                 Shipper shipper,
                 Double freight,
                 String shipName,
                 String shipAddress,
                 String shipCity,
                 String shipRegion,
                 String shipPostalCode,
                 String shipCountry) {
        this.orderId = orderId;
        this.customer = customer;
        this.employee = employee;
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.shippedDate = shippedDate;
        this.shipper = shipper;
        this.freight = freight;
        this.shipName = shipName;
        this.shipAddress = shipAddress;
        this.shipCity = shipCity;
        this.shipRegion = shipRegion;
        this.shipPostalCode = shipPostalCode;
        this.shipCountry = shipCountry;
    }

    public Long getOrderId() { return orderId; }
    public Customer getCustomer() { return customer; }
    public Employee getEmployee() { return employee; }
    public Date getOrderDate() { return orderDate; }
    public Date getRequiredDate() { return requiredDate; }
    public Date getShippedDate() { return shippedDate; }
    public Shipper getShipper() { return shipper; }
    public Double getFreight() { return freight; }
    public String getShipName() { return shipName; }
    public String getShipAddress() { return shipAddress; }
    public String getShipCity() { return shipCity; }
    public String getShipRegion() { return shipRegion; }
    public String getShipPostalCode() { return shipPostalCode; }
    public String getShipCountry() { return shipCountry; }
}