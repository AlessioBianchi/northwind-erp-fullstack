package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.model.Category;
import com.github.alessiobianchi.erp.model.CategoryBuilder;
import com.github.alessiobianchi.erp.service.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/categories")
public class CategoriesController {

    private final CategoriesService service;

    public CategoriesController(CategoriesService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public List<Category> findAllByOrderByCategoryIdDesc() {
        return service.findAllByOrderByCategoryIdDesc();
    }

    @PostMapping()
    @ResponseBody
    public ResponseEntity<Category> create(@RequestBody Category category) {
        Category categorySaved = service.create(category);
        return ResponseEntity.ok(categorySaved);
    }

    @PutMapping("/{categoryId}")
    @ResponseBody
    public ResponseEntity<Category> update(@PathVariable int categoryId, @RequestBody Category category) {
        Category categorySaved = service.update(categoryId, category);
        return ResponseEntity.ok(categorySaved);
    }

    @DeleteMapping("/delete/{categoryId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable int categoryId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category deleted!");

        if(!service.delete(categoryId)) {
            response.put("message", "This category is connected to a product. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
