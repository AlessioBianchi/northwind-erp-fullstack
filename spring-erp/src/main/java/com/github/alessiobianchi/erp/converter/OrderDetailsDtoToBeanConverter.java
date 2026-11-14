package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.model.OrderBuilder;
import com.github.alessiobianchi.erp.model.OrderDetail;
import com.github.alessiobianchi.erp.model.OrderDetailBuilder;
import com.github.alessiobianchi.erp.model.ProductBuilder;

public class OrderDetailsDtoToBeanConverter {

    public static OrderDetail convert(OrderDetailsDTO dto) {
        return new OrderDetailBuilder()
                .withOrder(dto.orderId() != null ? new OrderBuilder().withOrderId(dto.orderId()).build() : null)
                .withProduct(dto.productId() != null ? new ProductBuilder().withProductId(dto.productId()).build() : null)
                .withUnitPrice(dto.unitPrice())
                .withQuantity(dto.quantity())
                .withDiscount(dto.discount())
                .build();
    }
}
