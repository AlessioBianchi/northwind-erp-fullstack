package it.zerob.erp.controller;

import it.zerob.erp.model.Customer;
import it.zerob.erp.model.Supplier;
import it.zerob.erp.service.SuppliersService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/suppliers")
public class SuppliersController {

    private final SuppliersService service;

    public SuppliersController(SuppliersService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public List<Supplier> findAllByOrderBySupplierIdDesc() {
        return service.findAllByOrderBySupplierIdDesc();
    }

    @GetMapping("/paginated")
    @ResponseBody
    public Page<Supplier> findAllByOrderBySupplierIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderBySupplierIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<Supplier> create(@RequestBody Supplier supplier) {
        Supplier supplierSaved = service.create(supplier);
        return ResponseEntity.ok(supplierSaved);
    }

    @PutMapping("/{supplierId}")
    @ResponseBody
    public ResponseEntity<Supplier> update(@PathVariable Long supplierId, @RequestBody Supplier supplier) {
        Supplier supplierSaved = service.update(supplierId, supplier);
        return ResponseEntity.ok(supplierSaved);
    }

    @DeleteMapping("/delete/{supplierId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long supplierId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier deleted!");

        if(!service.delete(supplierId)) {
            response.put("message", "There are products connected to this supplier. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
