package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.service.SuppliersService;
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

@Tag(name = "Suppliers", description = "Suppliers that provide the products.")
@RestController
@RequestMapping("/api/v1/suppliers")
public class SuppliersController {

    private final SuppliersService service;

    public SuppliersController(SuppliersService service) {
        this.service = service;
    }

    @Operation(summary = "List suppliers",
            description = "Returns all suppliers, newest first." +
                    "\n\nWith `pageNumber` and `pageSize` it returns a page object instead of an array.")
    @ApiResponse(responseCode = "200", description = "Supplier list, or a page of suppliers when paginated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @GetMapping
    public List<Supplier> findAllByOrderBySupplierIdDesc() {
        return service.findAllByOrderBySupplierIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<Supplier> findAllByOrderBySupplierIdDesc(
            @Parameter(description = "0-based page index (0 or greater); send together with pageSize")
            @RequestParam @Min(value = 0, message = "must be 0 or greater") int pageNumber,
            @Parameter(description = "Page size (1 to " + MAX_PAGE_SIZE + "); send together with pageNumber")
            @RequestParam @Min(value = 1, message = "must be at least 1")
            @Max(value = MAX_PAGE_SIZE, message = "must be at most {value}") int pageSize) {
        return service.findAllByOrderBySupplierIdDesc(pageNumber, pageSize);
    }

    @Operation(summary = "Create a supplier", description = "Creates a new supplier.")
    @ApiResponse(responseCode = "201", description = "Supplier created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<Supplier> create(@Valid @RequestBody Supplier supplier) {
        Supplier supplierSaved = service.create(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierSaved);
    }

    @Operation(summary = "Update a supplier", description = "Updates the supplier.\n\n- 404 if the supplier does not exist")
    @ApiResponse(responseCode = "200", description = "Supplier updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{supplierId}")
    public ResponseEntity<Supplier> update(@PathVariable int supplierId, @Valid @RequestBody Supplier supplier) {
        Supplier supplierSaved = service.update(supplierId, supplier);
        return ResponseEntity.ok(supplierSaved);
    }

    @Operation(summary = "Delete a supplier",
            description = "Deletes the supplier.\n\n- 404 if the supplier does not exist\n" +
                    "- 409 if the supplier still has products")
    @ApiResponse(responseCode = "204", description = "Supplier deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> delete(@PathVariable int supplierId) {
        service.delete(supplierId);
        return ResponseEntity.noContent().build();
    }
}
