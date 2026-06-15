package it.zerob.erp.controller;

import it.zerob.erp.model.Order;
import it.zerob.erp.model.OrderDetail;
import it.zerob.erp.service.OrdersService;
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
    public Page<Order> findAllByOrderByOrderIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByOrderIdDesc(pageNumber, pageSize);
    }

    @PostMapping("/save")
    public ResponseEntity<Order> save(@RequestBody Order order, Principal principal) {
        Order orderSaved = service.save(order, principal.getName());
        return ResponseEntity.ok(orderSaved);
    }

    @PutMapping("/save")
    public ResponseEntity<Order> update(@RequestBody Order order, Principal principal) {
        Order orderSaved = service.save(order, principal.getName());
        return ResponseEntity.ok(orderSaved);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long orderId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted!");

        if(!service.delete(orderId)) {
            response.put("message", "There are details on this order. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{orderId}")
    public List<OrderDetail> findAllOrderDetailsByOrderId(@PathVariable Long orderId) {
        return service.findAllOrderDetailsByOrderId(orderId);
    }

    @PostMapping("/details/save")
    public ResponseEntity<OrderDetail> save(@RequestBody OrderDetail orderDetail) {
        OrderDetail orderDetailSaved = service.save(orderDetail);
        return ResponseEntity.ok(orderDetailSaved);
    }

    @PutMapping("/details/save")
    public ResponseEntity<OrderDetail> update(@RequestBody OrderDetail orderDetail) {
        OrderDetail orderDetailSaved = service.save(orderDetail);
        return ResponseEntity.ok(orderDetailSaved);
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<Map<String, String>> delete(@RequestBody OrderDetail orderDetail) {
        service.delete(orderDetail);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Detail deleted!");
        return ResponseEntity.ok(response);
    }
}
