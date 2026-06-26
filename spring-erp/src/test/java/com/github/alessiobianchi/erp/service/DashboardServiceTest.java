package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dto.DashboardDTO;
import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private OrdersDAO ordersDao;

    @Mock
    private OrderDetailsDAO orderDetailsDao;

    @Mock
    private ProductsDAO productsDao;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getStats_ShouldAggregateDataCorrectly() {
        // Arrange
        Long totalOrders = 100L;
        Double totalRevenue = 5000.0;
        Long lastMonthOrders = 10L;
        Double lastMonthRevenue = 500.0;

        when(ordersDao.countAllOrders()).thenReturn(totalOrders);
        when(orderDetailsDao.calculateTotalRevenue()).thenReturn(totalRevenue);
        when(ordersDao.countLastMonthOrders(any(Date.class))).thenReturn(lastMonthOrders);
        when(orderDetailsDao.calculateMonthlyRevenue(any(Date.class))).thenReturn(lastMonthRevenue);

        when(ordersDao.findFirst10ByOrderByOrderIdDesc()).thenReturn(List.of(new Order()));

        Object[] categoryRow = new Object[]{"Beverage", 5L};
        List<Object[]> categoryRowList = new ArrayList<>();
        categoryRowList.add(categoryRow);
        when(orderDetailsDao.findOrdersCountByCategory()).thenReturn(categoryRowList);

        when(productsDao.findByUnitsInStockLessThan(10)).thenReturn(List.of(new Product()));

        // Act
        DashboardDTO stats = dashboardService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(totalOrders, stats.getTotalOrdersCount());
        assertEquals(totalRevenue, stats.getTotalRevenue());
        assertEquals(lastMonthOrders, stats.getLastMonthOrdersCount());
        assertEquals(lastMonthRevenue, stats.getLastMonthRevenue());

        assertNotNull(stats.getOrdersByCategory());
        assertEquals(5, stats.getOrdersByCategory().get("Beverage"));

        assertNotNull(stats.getProductStock());
        assertFalse(stats.getProductStock().isEmpty());

        verify(ordersDao).countAllOrders();
        verify(orderDetailsDao).calculateTotalRevenue();
        verify(productsDao).findByUnitsInStockLessThan(10);
    }
}