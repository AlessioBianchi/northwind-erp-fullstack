package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
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

    public Page<Order> findAllByOrderByOrderIdDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return ordersDao.findAllByOrderByOrderIdDesc(pageable);
    }

    public Order create(Order order, String username) {
        Employee userLogged = employeesDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        Order orderToSave = new OrderBuilder(order)
                .withEmployee(userLogged)
                .build();

        return ordersDao.save(orderToSave);
    }

    public Order update(Long orderId, Order order) {
        Order orderToUpdate = new OrderBuilder(order)
                .withOrderId(orderId)
                .build();

        return ordersDao.save(orderToUpdate);
    }

    public boolean delete(Long orderId) {
        Order orderToDelete = new OrderBuilder()
                .withOrderId(orderId)
                .build();

        if (!orderDetailsDao.findAllByOrder(orderToDelete).isEmpty()) {
            return false;
        }

        ordersDao.delete(orderToDelete);
        return true;
    }

    public List<OrderDetail> findAllOrderDetailsByOrderId(Long orderId) {
        Order order = new OrderBuilder()
                .withOrderId(orderId)
                .build();
        return orderDetailsDao.findAllByOrder(order);
    }

    public OrderDetail create(OrderDetail orderDetail) {
        Product product = productsDao.findById(orderDetail.getProduct().getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderDetail orderDetailToSave = new OrderDetailBuilder(orderDetail)
                .withUnitPrice(product.getUnitPrice())
                .build();

        return orderDetailsDao.save(orderDetailToSave);
    }

    public void delete(OrderDetail orderDetail) {
        orderDetailsDao.delete(orderDetail);
    }
}
