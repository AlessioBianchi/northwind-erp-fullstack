package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.model.CustomerBuilder;
import com.github.alessiobianchi.erp.model.EmployeeBuilder;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.OrderBuilder;
import com.github.alessiobianchi.erp.model.ShipperBuilder;

public class OrderDtoToBeanConverter {

    public static Order convert(OrderDTO dto) {
        return new OrderBuilder()
                .withOrderId(dto.orderId())
                .withCustomer(dto.customerId() != null ? new CustomerBuilder().withCustomerId(dto.customerId()).build() : null)
                .withEmployee(dto.employeeId() != null ? new EmployeeBuilder().withEmployeeId(dto.employeeId()).build() : null)
                .withOrderDate(dto.orderDate())
                .withRequiredDate(dto.requiredDate())
                .withShippedDate(dto.shippedDate())
                .withShipper(dto.shipperId() != null ? new ShipperBuilder().withShipperId(dto.shipperId()).build() : null)
                .withFreight(dto.freight())
                .withShipName(dto.shipName())
                .withShipAddress(dto.shipAddress())
                .withShipCity(dto.shipCity())
                .withShipRegion(dto.shipRegion())
                .withShipPostalCode(dto.shipPostalCode())
                .withShipCountry(dto.shipCountry())
                .build();
    }
}
