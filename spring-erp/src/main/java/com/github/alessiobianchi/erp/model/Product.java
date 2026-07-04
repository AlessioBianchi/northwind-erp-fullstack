package com.github.alessiobianchi.erp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name="PRODUCTS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    private String productName;

    @ManyToOne
    @JoinColumn(name="supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    private String quantityPerUnit;
    private Double unitPrice;
    private Integer unitsInStock;
    private Integer unitsOnOrder;
    private Integer reorderLevel;
    private boolean discontinued;

    public Product() {}

    public Product(int productId,
                   String productName,
                   Supplier supplier,
                   Category category,
                   String quantityPerUnit,
                   Double unitPrice,
                   Integer unitsInStock,
                   Integer unitsOnOrder,
                   Integer reorderLevel,
                   boolean discontinued) {
        this.productId = productId;
        this.productName = productName;
        this.supplier = supplier;
        this.category = category;
        this.quantityPerUnit = quantityPerUnit;
        this.unitPrice = unitPrice;
        this.unitsInStock = unitsInStock;
        this.unitsOnOrder = unitsOnOrder;
        this.reorderLevel = reorderLevel;
        this.discontinued = discontinued;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Supplier getSupplier() { return supplier; }
    public Category getCategory() { return category; }
    public String getQuantityPerUnit() { return quantityPerUnit; }
    public Double getUnitPrice() { return unitPrice; }
    public Integer getUnitsInStock() { return unitsInStock; }
    public Integer getUnitsOnOrder() { return unitsOnOrder; }
    public Integer getReorderLevel() { return reorderLevel; }
    public boolean getDiscontinued() { return discontinued; }
}