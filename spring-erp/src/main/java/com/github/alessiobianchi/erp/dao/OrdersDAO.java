package com.github.alessiobianchi.erp.dao;

import com.github.alessiobianchi.erp.model.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrdersDAO extends CrudRepository<Order, Long> {

    Page<Order> findAllByOrderByOrderIdDesc(Pageable pageable);

    List<Order> findAllByCustomer(Customer customer);

    List<Order> findAllByShipper(Shipper shipper);

    List<Order> findAllByEmployee(Employee employee);

    @Query("SELECT COUNT(o) FROM Order o")
    Long countAllOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate")
    Long countLastMonthOrders(@Param("startDate") Date startDate);

    List<Order> findFirst10ByOrderByOrderIdDesc();
}
