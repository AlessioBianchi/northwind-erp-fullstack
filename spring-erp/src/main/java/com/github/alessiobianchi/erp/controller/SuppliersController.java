package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Supplier;
import com.github.alessiobianchi.erp.service.SuppliersService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SuppliersController {

    private final SuppliersService service;

    public SuppliersController(SuppliersService service) {
        this.service = service;
    }

    @GetMapping
    public List<Supplier> findAllByOrderBySupplierIdDesc() {
        return service.findAllByOrderBySupplierIdDesc();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public Page<Supplier> findAllByOrderBySupplierIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderBySupplierIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    public ResponseEntity<Supplier> create(@Valid @RequestBody Supplier supplier) {
        Supplier supplierSaved = service.create(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierSaved);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<Supplier> update(@PathVariable int supplierId, @Valid @RequestBody Supplier supplier) {
        Supplier supplierSaved = service.update(supplierId, supplier);
        return ResponseEntity.ok(supplierSaved);
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<Void> delete(@PathVariable int supplierId) {
        service.delete(supplierId);
        return ResponseEntity.noContent().build();
    }
}
