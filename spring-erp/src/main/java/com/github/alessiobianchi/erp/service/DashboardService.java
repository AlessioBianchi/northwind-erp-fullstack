package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dto.DashboardDTO;
import com.github.alessiobianchi.erp.dto.DashboardDTOBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final OrdersDAO ordersDao;
    private final OrderDetailsDAO orderDetailsDao;
    private final ProductsDAO productsDao;

    public DashboardService(OrdersDAO ordersDao, OrderDetailsDAO orderDetailsDao, ProductsDAO productsDao) {
        this.ordersDao = ordersDao;
        this.orderDetailsDao = orderDetailsDao;
        this.productsDao = productsDao;
    }

    public DashboardDTO getStats() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);

        return new DashboardDTOBuilder()
                .withTotalOrdersCount(ordersDao.countAllOrders())
                .withTotalRevenue(orderDetailsDao.calculateTotalRevenue())
                .withLastMonthOrdersCount(ordersDao.countLastMonthOrders(startOfMonth))
                .withLastMonthRevenue(orderDetailsDao.calculateMonthlyRevenue(startOfMonth))
                .withLastTenOrders(ordersDao.findFirst10ByOrderByOrderIdDesc())
                .withOrdersByCategory(
                        orderDetailsDao
                                .findOrdersCountByCategory()
                                .stream()
                                .collect(Collectors.toMap(
                                        row -> (String) row[0],
                                        row -> ((Long) row[1]).intValue()
                                )))
                .withProductStock(productsDao.findByUnitsInStockLessThan(10))
                .build();
    }
}
