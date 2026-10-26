package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTOBuilder;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.OrderDetail;
import com.github.alessiobianchi.erp.model.Product;

public class OrderDetailsBeanToDtoConverter {

    public static OrderDetailsDTO convert(OrderDetail orderDetail) {
        Order order = orderDetail.getOrder();
        Product product = orderDetail.getProduct();

        return new OrderDetailsDTOBuilder()
                .withOrderId(order != null ? order.getOrderId() : null)
                .withProductId(product != null ? product.getProductId() : null)
                .withProductName(product != null ? product.getProductName() : null)
                .withUnitPrice(orderDetail.getUnitPrice())
                .withQuantity(orderDetail.getQuantity())
                .withDiscount(orderDetail.getDiscount())
                .build();
    }
}
