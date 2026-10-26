package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.service.OrdersService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService service;

    public OrdersController(OrdersService service) {
        this.service = service;
    }

    @GetMapping("/paginated")
    public Page<OrderDTO> findAllByOrderByOrderIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByOrderIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO order, Principal principal) {
        OrderDTO orderSaved = service.create(order, principal.getName());
        return ResponseEntity.ok(orderSaved);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> update(@PathVariable int orderId, @RequestBody OrderDTO order, Principal principal) {
        OrderDTO orderSaved = service.update(orderId, order);
        return ResponseEntity.ok(orderSaved);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int orderId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted!");

        if(!service.delete(orderId)) {
            response.put("message", "There are details on this order. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{orderId}")
    public List<OrderDetailsDTO> findAllOrderDetailsByOrderId(@PathVariable int orderId) {
        return service.findAllOrderDetailsByOrderId(orderId);
    }

    @PostMapping("/details")
    public ResponseEntity<OrderDetailsDTO> create(@RequestBody OrderDetailsDTO orderDetail) {
        OrderDetailsDTO orderDetailSaved = service.create(orderDetail);
        return ResponseEntity.ok(orderDetailSaved);
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<Map<String, String>> delete(@RequestBody OrderDetailsDTO orderDetail) {
        service.delete(orderDetail);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Detail deleted!");
        return ResponseEntity.ok(response);
    }
}
