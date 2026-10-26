package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDTOBuilder;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Shipper;

public class OrderBeanToDtoConverter {

    public static OrderDTO convert(Order order) {
        Customer customer = order.getCustomer();
        Employee employee = order.getEmployee();
        Shipper shipper = order.getShipper();

        return new OrderDTOBuilder()
                .withOrderId(order.getOrderId())
                .withCustomerId(customer != null ? customer.getCustomerId() : null)
                .withCustomerName(customer != null ? customer.getCompanyName() : null)
                .withEmployeeId(employee != null ? employee.getEmployeeId() : null)
                .withEmployeeName(employee != null ? employee.getFirstName() + " " + employee.getLastName() : null)
                .withOrderDate(order.getOrderDate())
                .withRequiredDate(order.getRequiredDate())
                .withShippedDate(order.getShippedDate())
                .withShipperId(shipper != null ? shipper.getShipperId() : null)
                .withShipperName(shipper != null ? shipper.getCompanyName() : null)
                .withFreight(order.getFreight())
                .withShipName(order.getShipName())
                .withShipAddress(order.getShipAddress())
                .withShipCity(order.getShipCity())
                .withShipRegion(order.getShipRegion())
                .withShipPostalCode(order.getShipPostalCode())
                .withShipCountry(order.getShipCountry())
                .build();
    }
}
