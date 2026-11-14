package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.OrderDetail;
import com.github.alessiobianchi.erp.model.Product;

public class OrderDetailsBeanToDtoConverter {

    public static OrderDetailsDTO convert(OrderDetail orderDetail) {
        Order order = orderDetail.getOrder();
        Product product = orderDetail.getProduct();

        return new OrderDetailsDTO(
                order != null ? order.getOrderId() : null,
                product != null ? product.getProductId() : null,
                product != null ? product.getProductName() : null,
                orderDetail.getUnitPrice(),
                orderDetail.getQuantity(),
                orderDetail.getDiscount(),
                orderDetail.getCreatedAt(),
                orderDetail.getUpdatedAt());
    }
}
