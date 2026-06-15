package com.github.alessiobianchi.erp.dto;

import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.Product;

import java.util.List;
import java.util.Map;

public class DashboardDTOBuilder {

    private Long totalOrdersCount;
    private Double totalRevenue;
    private Long lastMonthOrdersCount;
    private Double lastMonthRevenue;
    private List<Order> lastTenOrders;
    private Map<String, Integer> ordersByCategory;
    private List<Product> productStock;

    public DashboardDTOBuilder() {
    }

    public DashboardDTOBuilder(DashboardDTO dto) {
        this.totalOrdersCount = dto.getTotalOrdersCount();
        this.totalRevenue = dto.getTotalRevenue();
        this.lastMonthOrdersCount = dto.getLastMonthOrdersCount();
        this.lastMonthRevenue = dto.getLastMonthRevenue();
        this.lastTenOrders = dto.getLastTenOrders();
        this.ordersByCategory = dto.getOrdersByCategory();
        this.productStock = dto.getProductStock();
    }

    public DashboardDTOBuilder withTotalOrdersCount(Long totalOrdersCount) {
        this.totalOrdersCount = totalOrdersCount;
        return this;
    }

    public DashboardDTOBuilder withTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
        return this;
    }

    public DashboardDTOBuilder withLastMonthOrdersCount(Long lastMonthOrdersCount) {
        this.lastMonthOrdersCount = lastMonthOrdersCount;
        return this;
    }

    public DashboardDTOBuilder withLastMonthRevenue(Double lastMonthRevenue) {
        this.lastMonthRevenue = lastMonthRevenue;
        return this;
    }

    public DashboardDTOBuilder withLastTenOrders(List<Order> lastTenOrders) {
        this.lastTenOrders = lastTenOrders;
        return this;
    }

    public DashboardDTOBuilder withOrdersByCategory(Map<String, Integer> ordersByCategory) {
        this.ordersByCategory = ordersByCategory;
        return this;
    }

    public DashboardDTOBuilder withProductStock(List<Product> productStock) {
        this.productStock = productStock;
        return this;
    }

    public DashboardDTO build() {
        return new DashboardDTO(
                totalOrdersCount,
                totalRevenue,
                lastMonthOrdersCount,
                lastMonthRevenue,
                lastTenOrders,
                ordersByCategory,
                productStock
        );
    }
}