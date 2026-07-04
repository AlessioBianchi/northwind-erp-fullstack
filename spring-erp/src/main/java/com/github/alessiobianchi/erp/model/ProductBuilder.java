package com.github.alessiobianchi.erp.model;

public class ProductBuilder {

    private int productId;
    private String productName;
    private Supplier supplier;
    private Category category;
    private String quantityPerUnit;
    private Double unitPrice;
    private Integer unitsInStock;
    private Integer unitsOnOrder;
    private Integer reorderLevel;
    private boolean discontinued;

    public ProductBuilder() {}

    public ProductBuilder(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.category = product.getCategory();
        this.supplier = product.getSupplier();
        this.quantityPerUnit = product.getQuantityPerUnit();
        this.unitPrice = product.getUnitPrice();
        this.unitsInStock = product.getUnitsInStock();
        this.unitsOnOrder = product.getUnitsOnOrder();
        this.reorderLevel = product.getReorderLevel();
        this.discontinued = product.getDiscontinued();
    }

    public ProductBuilder withProductId(int productId) {
        this.productId = productId;
        return this;
    }

    public ProductBuilder withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public ProductBuilder withSupplier(Supplier supplier) {
        this.supplier = supplier;
        return this;
    }

    public ProductBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public ProductBuilder withQuantityPerUnit(String quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
        return this;
    }

    public ProductBuilder withUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public ProductBuilder withUnitsInStock(Integer unitsInStock) {
        this.unitsInStock = unitsInStock;
        return this;
    }

    public ProductBuilder withUnitsOnOrder(Integer unitsOnOrder) {
        this.unitsOnOrder = unitsOnOrder;
        return this;
    }

    public ProductBuilder withReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
        return this;
    }

    public ProductBuilder withDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
        return this;
    }

    public Product build() {
        return new Product(
                productId,
                productName,
                supplier,
                category,
                quantityPerUnit,
                unitPrice,
                unitsInStock,
                unitsOnOrder,
                reorderLevel,
                discontinued
        );
    }
}