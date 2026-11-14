package com.github.alessiobianchi.erp.dto;

import java.util.List;
import java.util.Map;

public record DashboardDTO(
        Long totalOrdersCount,
        Double totalRevenue,
        Long lastMonthOrdersCount,
        Double lastMonthRevenue,
        List<OrderDTO> lastTenOrders,
        Map<String, Integer> ordersByCategory,
        List<ProductDTO> productStock) {
}
