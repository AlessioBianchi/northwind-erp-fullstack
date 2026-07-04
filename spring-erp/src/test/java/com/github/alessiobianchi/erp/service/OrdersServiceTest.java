package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private EmployeesDAO employeesDao;
    @Mock
    private OrdersDAO ordersDao;
    @Mock
    private OrderDetailsDAO orderDetailsDao;
    @Mock
    private ProductsDAO productsDao;

    @InjectMocks
    private OrdersService ordersService;

    @Test
    void findAllByOrderByOrderIdDesc_ShouldReturnPage() {
        // Arrange
        int pageNum = 0;
        int pageSize = 10;
        Page<Order> orderPage = new PageImpl<>(List.of(new Order()));
        when(ordersDao.findAllByOrderByOrderIdDesc(any(PageRequest.class))).thenReturn(orderPage);

        // Act
        Page<Order> result = ordersService.findAllByOrderByOrderIdDesc(pageNum, pageSize);

        // Assert
        assertEquals(1, result.getContent().size());
        verify(ordersDao).findAllByOrderByOrderIdDesc(PageRequest.of(pageNum, pageSize));
    }

    @Test
    void createOrder_ShouldAssociateUserAndSave() {
        // Arrange
        String username = "admin";
        Employee mockEmployee = new EmployeeBuilder()
                .withUsername(username)
                .build();
        Order inputOrder = new Order();

        when(employeesDao.findByUsername(username)).thenReturn(Optional.of(mockEmployee));
        when(ordersDao.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Order createdOrder = ordersService.create(inputOrder, username);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(mockEmployee, createdOrder.getEmployee());
        verify(employeesDao).findByUsername(username);
        verify(ordersDao).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldUpdate() {
        // Arrange
        int orderId = 1;
        Double freight = 10.0;
        Order orderUpdated = new OrderBuilder()
                .withOrderId(orderId)
                .withFreight(freight)
                .build();

        when(ordersDao.save(any(Order.class))).thenReturn(orderUpdated);

        // Act
        Order createdOrder = ordersService.update(orderId, new Order());

        // Assert
        assertNotNull(createdOrder);
        assertEquals(orderId, createdOrder.getOrderId());
        assertEquals(freight, createdOrder.getFreight());
        verify(ordersDao).save(any(Order.class));
    }

    @Test
    void deleteOrder_WhenHasDetails_ShouldReturnFalse() {
        // Arrange
        int orderId = 1;
        when(orderDetailsDao.findAllByOrder(any(Order.class))).thenReturn(List.of(new OrderDetail()));

        // Act
        boolean result = ordersService.delete(orderId);

        // Assert
        assertFalse(result);
        verify(ordersDao, never()).delete(any(Order.class));
    }

    @Test
    void createOrderDetail_ShouldSetUnitPriceFromProduct() {
        // Arrange
        Product mockProduct = new ProductBuilder()
                .withProductId(50)
                .withUnitPrice(25.50)
                .build();

        OrderDetail inputDetail = new OrderDetailBuilder()
                .withProduct(mockProduct)
                .build();

        when(productsDao.findById(50)).thenReturn(Optional.of(mockProduct));
        when(orderDetailsDao.save(any(OrderDetail.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        OrderDetail createdDetail = ordersService.create(inputDetail);

        // Assert
        assertEquals(25.50, createdDetail.getUnitPrice());
        verify(orderDetailsDao).save(any(OrderDetail.class));
    }

    @Test
    void saveOrderDetail_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        Product p = new ProductBuilder()
                .withProductId(99)
                .build();
        OrderDetail detail = new OrderDetailBuilder()
                .withProduct(p)
                .build();

        when(productsDao.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ordersService.create(detail));
    }

    @Test
    void findAllOrderDetailsByOrderId_ShouldReturnList() {
        // Arrange
        when(orderDetailsDao.findAllByOrder(any(Order.class))).thenReturn(List.of(new OrderDetail()));

        // Act
        List<OrderDetail> details = ordersService.findAllOrderDetailsByOrderId(1);

        // Assert
        assertEquals(1, details.size());
        verify(orderDetailsDao).findAllByOrder(any(Order.class));
    }
}