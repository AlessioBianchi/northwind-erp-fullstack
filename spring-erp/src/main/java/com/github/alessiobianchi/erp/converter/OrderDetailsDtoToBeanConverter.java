package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.model.OrderBuilder;
import com.github.alessiobianchi.erp.model.OrderDetail;
import com.github.alessiobianchi.erp.model.OrderDetailBuilder;
import com.github.alessiobianchi.erp.model.ProductBuilder;

public class OrderDetailsDtoToBeanConverter {

    public static OrderDetail convert(OrderDetailsDTO dto) {
        return new OrderDetailBuilder()
                .withOrder(dto.getOrderId() != null ? new OrderBuilder().withOrderId(dto.getOrderId()).build() : null)
                .withProduct(dto.getProductId() != null ? new ProductBuilder().withProductId(dto.getProductId()).build() : null)
                .withUnitPrice(dto.getUnitPrice())
                .withQuantity(dto.getQuantity())
                .withDiscount(dto.getDiscount())
                .build();
    }
}
