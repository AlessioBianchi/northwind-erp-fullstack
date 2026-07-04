package com.github.alessiobianchi.erp.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderDetailId implements Serializable {

    private int order;
    private int product;

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        OrderDetailId that = (OrderDetailId) obj;
        return Objects.equals(order, that.order) &&
                Objects.equals(product, that.product);
    }

    public int getOrderId() {
        return order;
    }

    public int getProductId() {
        return product;
    }
}
