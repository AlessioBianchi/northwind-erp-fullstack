package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.service.CustomersService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

    private final CustomersService service;

    public CustomersController(CustomersService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> findAllByOrderByCustomerIdDesc() {
        return service.findAllByOrderByCustomerIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<Customer> findAllByOrderByCustomerIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByCustomerIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        Customer customerSaved = service.create(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerSaved);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> update(@PathVariable int customerId, @Valid @RequestBody Customer customer) {
        Customer customerSaved = service.update(customerId, customer);
        return ResponseEntity.ok(customerSaved);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable int customerId) {
        service.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
