package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.CustomersDAO;
import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportsServiceTest {

    @Mock
    private OrdersDAO ordersDao;

    @Mock
    private OrderDetailsDAO orderDetailsDao;

    @Mock
    private CustomersDAO customersDao;

    @InjectMocks
    private ReportsService reportsService;

    private Order sampleOrder;
    private Customer sampleCustomer;
    private OrderDetail sampleDetail;

    @BeforeEach
    void setUp() {
        sampleCustomer = new CustomerBuilder()
                .withCustomerId(1L)
                .withContactName("Mario Rossi")
                .build();

        sampleOrder = new OrderBuilder()
                .withOrderId(100L)
                .withOrderDate(new Date())
                .withFreight(15.50)
                .withCustomer(sampleCustomer)
                .build();

        Product product = new ProductBuilder()
                .withProductId(99L)
                .withProductName("Test")
                .build();

        sampleDetail = new OrderDetailBuilder()
                .withProduct(product)
                .withUnitPrice(100.0)
                .withQuantity(2)
                .withDiscount(0.01) //10%
                .build();
    }

    @Test
    void getOrdersReport_ShouldReturnValidPdfByteArray() {
        // Arrange
        Long orderId = 100L;
        when(ordersDao.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(orderDetailsDao.findAllByOrder(sampleOrder)).thenReturn(List.of(sampleDetail));
        when(customersDao.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        // Act
        byte[] pdfContent = reportsService.getOrdersReport(orderId);

        // Assert
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);

        String pdfHeader = new String(pdfContent, 0, 4);
        assertEquals("%PDF", pdfHeader);

        verify(ordersDao).findById(orderId);
        verify(orderDetailsDao).findAllByOrder(sampleOrder);
        verify(customersDao).findById(1L);
    }

    @Test
    void getOrdersReport_WhenOrderNotFound_ShouldThrowException() {
        // Arrange
        when(ordersDao.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reportsService.getOrdersReport(999L));
    }

    @Test
    void getOrdersReport_WhenCustomerNotFound_ShouldThrowException() {
        // Arrange
        when(ordersDao.findById(100L)).thenReturn(Optional.of(sampleOrder));
        when(orderDetailsDao.findAllByOrder(sampleOrder)).thenReturn(List.of(sampleDetail));
        when(customersDao.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reportsService.getOrdersReport(100L));
    }
}