package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.service.ProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Products", description = "Products for sale, each with a category and a supplier.")
@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {

    private final ProductsService service;

    public ProductsController(ProductsService service) {
        this.service = service;
    }

    @Operation(summary = "List products",
            description = "Returns all products, newest first." +
                    "\n\nWith `pageNumber` and `pageSize` it returns a page object instead of an array.")
    @ApiResponse(responseCode = "200", description = "Product list, or a page of products when paginated")
    @GetMapping
    public List<Product> findAllByOrderByProductIdDesc() {
        return service.findAllByOrderByProductIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<Product> findAllByOrderByProductIdDesc(
            @Parameter(description = "0-based page index; send together with pageSize") @RequestParam int pageNumber,
            @Parameter(description = "Page size; send together with pageNumber") @RequestParam int pageSize) {
        return service.findAllByOrderByProductIdDesc(pageNumber, pageSize);
    }

    @Operation(summary = "Create a product", description = "Creates a new product.")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product productSaved = service.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productSaved);
    }

    @Operation(summary = "Update a product", description = "Updates the product.\n\n- 404 if the product does not exist")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{productId}")
    public ResponseEntity<Product> update(@PathVariable int productId, @Valid @RequestBody Product product) {
        Product productSaved = service.update(productId, product);
        return ResponseEntity.ok(productSaved);
    }

    @Operation(summary = "Delete a product",
            description = "Deletes the product.\n\n- 404 if the product does not exist\n" +
                    "- 409 if the product appears in any order")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable int productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
