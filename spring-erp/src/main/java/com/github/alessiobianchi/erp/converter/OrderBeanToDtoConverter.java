package com.github.alessiobianchi.erp.converter;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.model.Employee;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Shipper;

public class OrderBeanToDtoConverter {

    public static OrderDTO convert(Order order) {
        Customer customer = order.getCustomer();
        Employee employee = order.getEmployee();
        Shipper shipper = order.getShipper();

        return new OrderDTO(
                order.getOrderId(),
                customer != null ? customer.getCustomerId() : null,
                customer != null ? customer.getCompanyName() : null,
                employee != null ? employee.getEmployeeId() : null,
                employee != null ? employee.getFirstName() + " " + employee.getLastName() : null,
                order.getOrderDate(),
                order.getRequiredDate(),
                order.getShippedDate(),
                shipper != null ? shipper.getShipperId() : null,
                shipper != null ? shipper.getCompanyName() : null,
                order.getFreight(),
                order.getShipName(),
                order.getShipAddress(),
                order.getShipCity(),
                order.getShipRegion(),
                order.getShipPostalCode(),
                order.getShipCountry(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
