package it.zerob.erp.service;

import it.zerob.erp.dao.OrderDetailsDAO;
import it.zerob.erp.dao.OrdersDAO;
import it.zerob.erp.dao.ProductsDAO;
import it.zerob.erp.dto.DashboardDTO;
import it.zerob.erp.dto.DashboardDTOBuilder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = cal.getTime();

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
