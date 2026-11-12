package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.dao.EmployeesDAO;
import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTOBuilder;
import com.github.alessiobianchi.erp.exception.BadRequestException;
import com.github.alessiobianchi.erp.exception.ConflictException;
import com.github.alessiobianchi.erp.exception.ResourceNotFoundException;
import com.github.alessiobianchi.erp.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    private OrdersServiceImpl ordersService;

    @Test
    void findAllByOrderByOrderIdDesc_ShouldReturnPage() {
        // Arrange
        int pageNum = 0;
        int pageSize = 10;
        Page<Order> orderPage = new PageImpl<>(List.of(new Order()));
        when(ordersDao.findAllByOrderByOrderIdDesc(any(PageRequest.class))).thenReturn(orderPage);

        // Act
        Page<OrderDTO> result = ordersService.findAllByOrderByOrderIdDesc(pageNum, pageSize);

        // Assert
        assertEquals(1, result.getContent().size());
        verify(ordersDao).findAllByOrderByOrderIdDesc(PageRequest.of(pageNum, pageSize));
    }

    @Test
    void findAllByOrderByOrderIdDesc_ShouldReturnList() {
        // Arrange
        Order order = new OrderBuilder().withOrderId(1).build();
        when(ordersDao.findAllByOrderByOrderIdDesc()).thenReturn(List.of(order));

        // Act
        List<OrderDTO> result = ordersService.findAllByOrderByOrderIdDesc();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getOrderId());
    }

    @Test
    void createOrder_WhenUserNotFound_ShouldThrow() {
        // Arrange
        when(employeesDao.findByUsername("ghost")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> ordersService.create(new OrderDTO(), "ghost"));
        verify(ordersDao, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldAssociateUserAndSave() {
        // Arrange
        String username = "admin";
        Employee mockEmployee = new EmployeeBuilder()
                .withEmployeeId(7)
                .withFirstname("John")
                .withLastname("Doe")
                .withUsername(username)
                .build();
        OrderDTO inputOrder = new OrderDTO();

        when(employeesDao.findByUsername(username)).thenReturn(Optional.of(mockEmployee));
        when(ordersDao.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        OrderDTO createdOrder = ordersService.create(inputOrder, username);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(7, createdOrder.getEmployeeId());
        assertEquals("John Doe", createdOrder.getEmployeeName());
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

        when(ordersDao.existsById(orderId)).thenReturn(true);
        when(ordersDao.save(any(Order.class))).thenReturn(orderUpdated);

        // Act
        OrderDTO createdOrder = ordersService.update(orderId, new OrderDTO());

        // Assert
        assertNotNull(createdOrder);
        assertEquals(orderId, createdOrder.getOrderId());
        assertEquals(freight, createdOrder.getFreight());
        verify(ordersDao).save(any(Order.class));
    }

    @Test
    void createOrderDetail_ShouldSetUnitPriceFromProduct() {
        // Arrange
        Product mockProduct = new ProductBuilder()
                .withProductId(50)
                .withProductName("Chai")
                .withUnitPrice(25.50)
                .build();

        OrderDetailsDTO inputDetail = new OrderDetailsDTOBuilder()
                .withOrderId(1)
                .withProductId(50)
                .build();

        when(ordersDao.existsById(1)).thenReturn(true);
        when(productsDao.findById(50)).thenReturn(Optional.of(mockProduct));
        when(orderDetailsDao.save(any(OrderDetail.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        OrderDetailsDTO createdDetail = ordersService.create(1, inputDetail);

        // Assert
        assertEquals(25.50, createdDetail.getUnitPrice());
        assertEquals("Chai", createdDetail.getProductName());
        verify(orderDetailsDao).save(any(OrderDetail.class));
    }

    @Test
    void saveOrderDetail_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        OrderDetailsDTO detail = new OrderDetailsDTOBuilder()
                .withOrderId(1)
                .withProductId(99)
                .build();

        when(ordersDao.existsById(1)).thenReturn(true);
        when(productsDao.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ordersService.create(1, detail));
    }

    @Test
    void createOrderDetail_WhenOrderNotFound_ShouldThrowNotFound() {
        // Arrange
        OrderDetailsDTO detail = new OrderDetailsDTOBuilder()
                .withOrderId(999)
                .withProductId(50)
                .build();

        when(ordersDao.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ordersService.create(999, detail));
        verifyNoInteractions(productsDao, orderDetailsDao);
    }

    @Test
    void createOrderDetail_WhenOrderIdMismatch_ShouldThrowBadRequest() {
        // Arrange
        OrderDetailsDTO detail = new OrderDetailsDTOBuilder()
                .withOrderId(7)
                .withProductId(50)
                .build();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> ordersService.create(5, detail));
        verifyNoInteractions(productsDao, orderDetailsDao);
    }

    @Test
    void findAllOrderDetailsByOrderId_ShouldReturnList() {
        // Arrange
        when(orderDetailsDao.findAllByOrder(any(Order.class))).thenReturn(List.of(new OrderDetail()));

        // Act
        List<OrderDetailsDTO> details = ordersService.findAllOrderDetailsByOrderId(1);

        // Assert
        assertEquals(1, details.size());
        verify(orderDetailsDao).findAllByOrder(any(Order.class));
    }

    @Test
    void getOrdersReport_ShouldReturnValidPdfByteArray() {
        // Arrange
        Customer customer = new CustomerBuilder()
                .withCustomerId(1)
                .withContactName("Mario Rossi")
                .build();
        Order order = new OrderBuilder()
                .withOrderId(100)
                .withOrderDate(LocalDate.now())
                .withFreight(15.50)
                .withCustomer(customer)
                .build();
        OrderDetail detail = new OrderDetailBuilder()
                .withProduct(new ProductBuilder()
                        .withProductId(99)
                        .withProductName("Test")
                        .build())
                .withUnitPrice(100.0)
                .withQuantity(2)
                .withDiscount(0.01)
                .build();

        when(ordersDao.findById(100)).thenReturn(Optional.of(order));
        when(orderDetailsDao.findAllByOrder(order)).thenReturn(List.of(detail));

        // Act
        byte[] pdfContent = ordersService.getOrdersReport(100);

        // Assert
        assertNotNull(pdfContent);
        assertEquals("%PDF", new String(pdfContent, 0, 4));
        verify(ordersDao).findById(100);
        verify(orderDetailsDao).findAllByOrder(order);
    }

    @Test
    void getOrdersReport_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(ordersDao.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ordersService.getOrdersReport(999));
    }

    @Test
    void update_WhenOrderNotFound_ShouldThrowNotFound() {
        // Arrange
        when(ordersDao.existsById(1)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ordersService.update(1, new OrderDTO()));
        verify(ordersDao, never()).save(any(Order.class));
    }

    @Test
    void delete_WhenOrderNotFound_ShouldThrowNotFound() {
        // Arrange
        when(ordersDao.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ordersService.delete(1));
        verify(ordersDao, never()).delete(any(Order.class));
    }

    @Test
    void delete_WhenOrderHasDetails_ShouldThrowConflict() {
        // Arrange
        Order order = new OrderBuilder().withOrderId(1).build();
        when(ordersDao.findById(1)).thenReturn(Optional.of(order));
        when(orderDetailsDao.findAllByOrder(order)).thenReturn(List.of(new OrderDetail()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> ordersService.delete(1));
        verify(ordersDao, never()).delete(any(Order.class));
    }

    @Test
    void delete_WhenOrderHasNoDetails_ShouldDelete() {
        // Arrange
        Order order = new OrderBuilder().withOrderId(1).build();
        when(ordersDao.findById(1)).thenReturn(Optional.of(order));
        when(orderDetailsDao.findAllByOrder(order)).thenReturn(List.of());

        // Act
        ordersService.delete(1);

        // Assert
        verify(ordersDao).delete(order);
    }

    @Test
    void deleteOrderDetail_WhenExists_ShouldDelete() {
        // Arrange
        when(orderDetailsDao.existsByOrderOrderIdAndProductProductId(1, 50)).thenReturn(true);

        // Act
        ordersService.delete(1, 50);

        // Assert
        verify(orderDetailsDao).delete(any(OrderDetail.class));
    }

    @Test
    void deleteOrderDetail_WhenNotFound_ShouldThrowNotFound() {
        // Arrange
        when(orderDetailsDao.existsByOrderOrderIdAndProductProductId(1, 50)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ordersService.delete(1, 50));
        verify(orderDetailsDao, never()).delete(any(OrderDetail.class));
    }
}
