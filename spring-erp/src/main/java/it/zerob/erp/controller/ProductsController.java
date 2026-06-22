package it.zerob.erp.controller;

import it.zerob.erp.model.Product;
import it.zerob.erp.service.ProductsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/products")
public class ProductsController {

    private final ProductsService service;

    public ProductsController(ProductsService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public List<Product> findAllByOrderByProductIdDesc() {
        return service.findAllByOrderByProductIdDesc();
    }

    @GetMapping("/paginated")
    @ResponseBody
    public Page<Product> findAllByOrderByProductIdDesc(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        return service.findAllByOrderByProductIdDesc(pageNumber, pageSize);
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product productSaved = service.create(product);
        return ResponseEntity.ok(productSaved);
    }

    @PutMapping("/{productId}")
    @ResponseBody
    public ResponseEntity<Product> update(@PathVariable Long productId, @RequestBody Product product) {
        Product productSaved = service.update(productId, product);
        return ResponseEntity.ok(productSaved);
    }

    @DeleteMapping("/delete/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long productId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted!");

        if(!service.delete(productId)) {
            response.put("message", "This product is connected to an order. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
