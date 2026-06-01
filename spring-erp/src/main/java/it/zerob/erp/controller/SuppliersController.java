package it.zerob.erp.controller;

import it.zerob.erp.model.Order;
import it.zerob.erp.model.Supplier;
import it.zerob.erp.service.SuppliersService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/suppliers")
public class SuppliersController {

    private final SuppliersService service;

    public SuppliersController(SuppliersService service) {
        this.service = service;
    }

    @GetMapping
    public String showSuppliers(Model model) { return "suppliers"; }

    @GetMapping("/all")
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

    @PostMapping("/save")
    @ResponseBody
    public Supplier save(@RequestBody Supplier supplier) {
        return service.save(supplier);
    }

    @GetMapping("/delete/{supplierId}")
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
