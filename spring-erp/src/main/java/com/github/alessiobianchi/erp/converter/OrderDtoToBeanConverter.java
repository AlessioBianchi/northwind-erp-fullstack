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
                .withOrderId(dto.getOrderId())
                .withCustomer(dto.getCustomerId() != null ? new CustomerBuilder().withCustomerId(dto.getCustomerId()).build() : null)
                .withEmployee(dto.getEmployeeId() != null ? new EmployeeBuilder().withEmployeeId(dto.getEmployeeId()).build() : null)
                .withOrderDate(dto.getOrderDate())
                .withRequiredDate(dto.getRequiredDate())
                .withShippedDate(dto.getShippedDate())
                .withShipper(dto.getShipperId() != null ? new ShipperBuilder().withShipperId(dto.getShipperId()).build() : null)
                .withFreight(dto.getFreight())
                .withShipName(dto.getShipName())
                .withShipAddress(dto.getShipAddress())
                .withShipCity(dto.getShipCity())
                .withShipRegion(dto.getShipRegion())
                .withShipPostalCode(dto.getShipPostalCode())
                .withShipCountry(dto.getShipCountry())
                .build();
    }
}
