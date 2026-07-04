package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.service.CustomersService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/customers")
public class CustomersController {

    private final CustomersService service;

    public CustomersController(CustomersService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public List<Customer> findAllByOrderByCustomerIdDesc() {
        return service.findAllByOrderByCustomerIdDesc();
    }

    @GetMapping("/paginated")
    @ResponseBody
    public Page<Customer> findAllByOrderByCustomerIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByCustomerIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        Customer customerSaved = service.create(customer);
        return ResponseEntity.ok(customerSaved);
    }

    @PutMapping("/{customerId}")
    @ResponseBody
    public ResponseEntity<Customer> update(@PathVariable int customerId, @RequestBody Customer customer) {
        Customer customerSaved = service.update(customerId, customer);
        return ResponseEntity.ok(customerSaved);
    }

    @DeleteMapping("/delete/{customerId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable int customerId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted!");

        if(!service.delete(customerId)) {
            response.put("message", "This customer is connected to an order. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
