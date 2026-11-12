package com.github.alessiobianchi.erp.service;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrdersService {

    List<OrderDTO> findAllByOrderByOrderIdDesc();

    Page<OrderDTO> findAllByOrderByOrderIdDesc(int pageNumber, int pageSize);

    OrderDTO create(OrderDTO orderDto, String username);

    OrderDTO update(int orderId, OrderDTO orderDto);

    void delete(int orderId);

    List<OrderDetailsDTO> findAllOrderDetailsByOrderId(int orderId);

    OrderDetailsDTO create(int orderId, OrderDetailsDTO orderDetailDto);

    void delete(int orderId, int productId);

    byte[] getOrdersReport(int orderId);
}
