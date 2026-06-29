package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.Order;
import com.github.alessiobianchi.erp.model.OrderDetail;
import com.github.alessiobianchi.erp.model.OrderDetailId;
import com.github.alessiobianchi.erp.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderDetailsDAO extends CrudRepository<OrderDetail, OrderDetailId> {

    List<OrderDetail> findAllByOrder(Order order);

    List<OrderDetail> findAllByProduct(Product product);

    @Query("SELECT SUM(d.quantity * d.unitPrice * (1 - d.discount)) FROM OrderDetail d")
    Double calculateTotalRevenue();

    @Query("SELECT SUM(d.quantity * d.unitPrice * (1 - d.discount)) FROM OrderDetail d " +
            "WHERE d.order.orderDate >= :startDate")
    Double calculateMonthlyRevenue(@Param("startDate") Date startDate);

    @Query("SELECT d.product.category.categoryName, COUNT(DISTINCT d.order.orderId) " +
            "FROM OrderDetail d " +
            "GROUP BY d.product.category.categoryName")
    List<Object[]> findOrdersCountByCategory();
}
