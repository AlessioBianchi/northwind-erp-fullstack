package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Product;
import com.github.alessiobianchi.erp.service.ProductsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {

    private final ProductsService service;

    public ProductsController(ProductsService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> findAllByOrderByProductIdDesc() {
        return service.findAllByOrderByProductIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<Product> findAllByOrderByProductIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByProductIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product productSaved = service.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productSaved);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> update(@PathVariable int productId, @Valid @RequestBody Product product) {
        Product productSaved = service.update(productId, product);
        return ResponseEntity.ok(productSaved);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable int productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
