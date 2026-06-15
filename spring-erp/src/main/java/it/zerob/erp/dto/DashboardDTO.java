package it.zerob.erp.dto;

import it.zerob.erp.model.Order;
import it.zerob.erp.model.Product;

import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private Long totalOrdersCount;
    private Double totalRevenue;
    private Long lastMonthOrdersCount;
    private Double lastMonthRevenue;
    private List<Order> lastTenOrders;
    private Map<String, Integer> ordersByCategory;
    private List<Product> productStock;

    public DashboardDTO() {}

    public DashboardDTO(Long totalOrdersCount,
                        Double totalRevenue,
                        Long lastMonthOrdersCount,
                        Double lastMonthRevenue,
                        List<Order> lastTenOrders,
                        Map<String, Integer> ordersByCategory,
                        List<Product> productStock) {
        this.totalOrdersCount = totalOrdersCount;
        this.totalRevenue = totalRevenue;
        this.lastMonthOrdersCount = lastMonthOrdersCount;
        this.lastMonthRevenue = lastMonthRevenue;
        this.lastTenOrders = lastTenOrders;
        this.ordersByCategory = ordersByCategory;
        this.productStock = productStock;
    }

    public Long getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public Long getLastMonthOrdersCount() {
        return lastMonthOrdersCount;
    }

    public Double getLastMonthRevenue() {
        return lastMonthRevenue;
    }

    public List<Order> getLastTenOrders() {
        return lastTenOrders;
    }

    public Map<String, Integer> getOrdersByCategory() {
        return ordersByCategory;
    }

    public List<Product> getProductStock() {
        return productStock;
    }
}
