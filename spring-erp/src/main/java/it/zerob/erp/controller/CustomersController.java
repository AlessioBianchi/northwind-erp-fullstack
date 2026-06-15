package it.zerob.erp.controller;

import it.zerob.erp.model.Customer;
import it.zerob.erp.service.CustomersService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customers")
public class CustomersController {

    private final CustomersService service;

    public CustomersController(CustomersService service) {
        this.service = service;
    }

    @GetMapping
    public String showCustomers(Model model){
        return "customers";
    }

    @GetMapping("/all")
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

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Customer> save(@RequestBody Customer customer) {
        Customer customerSaved = service.save(customer);
        return ResponseEntity.ok(customerSaved);
    }

    @GetMapping("/delete/{customerId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long customerId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted!");

        if(!service.delete(customerId)) {
            response.put("message", "This customer is connected to an order. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
