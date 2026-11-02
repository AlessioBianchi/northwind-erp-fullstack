package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.dto.OrderDTO;
import com.github.alessiobianchi.erp.dto.OrderDetailsDTO;
import com.github.alessiobianchi.erp.service.OrdersService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService service;

    public OrdersController(OrdersService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderDTO> findAllByOrderByOrderIdDesc() {
        return service.findAllByOrderByOrderIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<OrderDTO> findAllByOrderByOrderIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByOrderIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO order, Principal principal) {
        OrderDTO orderSaved = service.create(order, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderSaved);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> update(@PathVariable int orderId, @Valid @RequestBody OrderDTO order) {
        OrderDTO orderSaved = service.update(orderId, order);
        return ResponseEntity.ok(orderSaved);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable int orderId) {
        service.delete(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}/details")
    public List<OrderDetailsDTO> findAllOrderDetailsByOrderId(@PathVariable int orderId) {
        return service.findAllOrderDetailsByOrderId(orderId);
    }

    @PostMapping("/{orderId}/details")
    public ResponseEntity<OrderDetailsDTO> create(@PathVariable int orderId, @Valid @RequestBody OrderDetailsDTO orderDetail) {
        OrderDetailsDTO orderDetailSaved = service.create(orderId, orderDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailSaved);
    }

    @DeleteMapping("/{orderId}/details/{productId}")
    public ResponseEntity<Void> delete(@PathVariable int orderId, @PathVariable int productId) {
        service.delete(orderId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> getOrdersReport(@PathVariable("id") int orderId) {
        byte[] pdfContent = service.getOrdersReport(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("attachment", "order_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfContent);
    }
}
