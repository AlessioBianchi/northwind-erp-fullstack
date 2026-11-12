package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.model.Customer;
import com.github.alessiobianchi.erp.service.CustomersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.github.alessiobianchi.erp.controller.Paging.MAX_PAGE_SIZE;

@Tag(name = "Customers", description = "Customers who place orders.")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

    private final CustomersService service;

    public CustomersController(CustomersService service) {
        this.service = service;
    }

    @Operation(summary = "List customers",
            description = "Returns all customers, newest first." +
                    "\n\nWith `pageNumber` and `pageSize` it returns a page object instead of an array.")
    @ApiResponse(responseCode = "200", description = "Customer list, or a page of customers when paginated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @GetMapping
    public List<Customer> findAllByOrderByCustomerIdDesc() {
        return service.findAllByOrderByCustomerIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<Customer> findAllByOrderByCustomerIdDesc(
            @Parameter(description = "0-based page index (0 or greater); send together with pageSize")
            @RequestParam @Min(value = 0, message = "must be 0 or greater") int pageNumber,
            @Parameter(description = "Page size (1 to " + MAX_PAGE_SIZE + "); send together with pageNumber")
            @RequestParam @Min(value = 1, message = "must be at least 1")
            @Max(value = MAX_PAGE_SIZE, message = "must be at most {value}") int pageSize) {
        return service.findAllByOrderByCustomerIdDesc(pageNumber, pageSize);
    }

    @Operation(summary = "Create a customer", description = "Creates a new customer.")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        Customer customerSaved = service.create(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerSaved);
    }

    @Operation(summary = "Update a customer", description = "Updates the customer.\n\n- 404 if the customer does not exist")
    @ApiResponse(responseCode = "200", description = "Customer updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> update(@PathVariable int customerId, @Valid @RequestBody Customer customer) {
        Customer customerSaved = service.update(customerId, customer);
        return ResponseEntity.ok(customerSaved);
    }

    @Operation(summary = "Delete a customer",
            description = "Deletes the customer.\n\n- 404 if the customer does not exist\n" +
                    "- 409 if the customer has orders")
    @ApiResponse(responseCode = "204", description = "Customer deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable int customerId) {
        service.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}
