package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.converter.OrderBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.OrderDetailsBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.OrderDetailsDtoToBeanConverter;
import com.github.alessiobianchi.erp.converter.OrderDtoToBeanConverter;
import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    private final EmployeesDAO employeesDao;
    private final OrdersDAO ordersDao;
    private final OrderDetailsDAO orderDetailsDao;
    private final ProductsDAO productsDao;

    public OrdersService(EmployeesDAO employeesDao,
                         OrdersDAO ordersDao,
                         OrderDetailsDAO orderDetailsDao,
                         ProductsDAO productsDao) {
        this.employeesDao = employeesDao;
        this.ordersDao = ordersDao;
        this.orderDetailsDao = orderDetailsDao;
        this.productsDao = productsDao;
    }

    public Page<OrderDTO> findAllByOrderByOrderIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return ordersDao.findAllByOrderByOrderIdDesc(pageable)
                .map(OrderBeanToDtoConverter::convert);
    }

    public OrderDTO create(OrderDTO orderDto, String username) {
        Employee userLogged = employeesDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        Order orderToSave = new OrderBuilder(OrderDtoToBeanConverter.convert(orderDto))
                .withEmployee(userLogged)
                .build();

        return OrderBeanToDtoConverter.convert(ordersDao.save(orderToSave));
    }

    public OrderDTO update(int orderId, OrderDTO orderDto) {
        Order orderToUpdate = new OrderBuilder(OrderDtoToBeanConverter.convert(orderDto))
                .withOrderId(orderId)
                .build();

        return OrderBeanToDtoConverter.convert(ordersDao.save(orderToUpdate));
    }

    public boolean delete(int orderId) {
        Order orderToDelete = new OrderBuilder()
                .withOrderId(orderId)
                .build();

        if (!orderDetailsDao.findAllByOrder(orderToDelete).isEmpty()) {
            return false;
        }

        ordersDao.delete(orderToDelete);
        return true;
    }

    public List<OrderDetailsDTO> findAllOrderDetailsByOrderId(int orderId) {
        Order order = new OrderBuilder()
                .withOrderId(orderId)
                .build();
        return orderDetailsDao.findAllByOrder(order).stream()
                .map(OrderDetailsBeanToDtoConverter::convert)
                .toList();
    }

    public OrderDetailsDTO create(OrderDetailsDTO orderDetailDto) {
        OrderDetail orderDetail = OrderDetailsDtoToBeanConverter.convert(orderDetailDto);
        Product product = productsDao.findById(orderDetail.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderDetail orderDetailToSave = new OrderDetailBuilder(orderDetail)
                .withProduct(product)
                .withUnitPrice(product.getUnitPrice())
                .build();

        return OrderDetailsBeanToDtoConverter.convert(orderDetailsDao.save(orderDetailToSave));
    }

    public void delete(OrderDetailsDTO orderDetailDto) {
        orderDetailsDao.delete(OrderDetailsDtoToBeanConverter.convert(orderDetailDto));
    }
}
