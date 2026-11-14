package com.github.alessiobianchi.erp.service.impl;

import com.github.alessiobianchi.erp.converter.OrderBeanToDtoConverter;
import com.github.alessiobianchi.erp.converter.ProductBeanToDtoConverter;
import com.github.alessiobianchi.erp.dao.OrderDetailsDAO;
import com.github.alessiobianchi.erp.dao.OrdersDAO;
import com.github.alessiobianchi.erp.dao.ProductsDAO;
import com.github.alessiobianchi.erp.dto.DashboardDTO;
import com.github.alessiobianchi.erp.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final OrdersDAO ordersDao;
    private final OrderDetailsDAO orderDetailsDao;
    private final ProductsDAO productsDao;

    public DashboardServiceImpl(OrdersDAO ordersDao, OrderDetailsDAO orderDetailsDao, ProductsDAO productsDao) {
        this.ordersDao = ordersDao;
        this.orderDetailsDao = orderDetailsDao;
        this.productsDao = productsDao;
    }

    @Override
    public DashboardDTO getStats() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);

        return new DashboardDTO(
                ordersDao.countAllOrders(),
                orderDetailsDao.calculateTotalRevenue(),
                ordersDao.countLastMonthOrders(startOfMonth),
                orderDetailsDao.calculateMonthlyRevenue(startOfMonth),
                ordersDao.findFirst10ByOrderByOrderIdDesc().stream()
                        .map(OrderBeanToDtoConverter::convert)
                        .toList(),
                orderDetailsDao.findOrdersCountByCategory().stream()
                        .collect(Collectors.toMap(
                                row -> (String) row[0],
                                row -> ((Long) row[1]).intValue())),
                productsDao.findByUnitsInStockLessThan(10).stream()
                        .map(ProductBeanToDtoConverter::convert)
                        .toList());
    }
}
