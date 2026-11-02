package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.service.CategoriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {

    private final CategoriesService service;

    public CategoriesController(CategoriesService service) {
        this.service = service;
    }

    @GetMapping
    public List<Category> findAllByOrderByCategoryIdDesc() {
        return service.findAllByOrderByCategoryIdDesc();
    }

    @PostMapping()
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) {
        Category categorySaved = service.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categorySaved);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> update(@PathVariable int categoryId, @Valid @RequestBody Category category) {
        Category categorySaved = service.update(categoryId, category);
        return ResponseEntity.ok(categorySaved);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable int categoryId) {
        service.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
