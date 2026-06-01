package it.zerob.erp.controller;

import it.zerob.erp.model.Category;
import it.zerob.erp.service.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService service;

    public CategoriesController(CategoriesService service) {
        this.service = service;
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Category> findAllByOrderByCategoryIdDesc() {
        return service.findAllByOrderByCategoryIdDesc();
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Category> save(@RequestBody Category category) {
        Category categorySaved = service.save(category);
        return ResponseEntity.ok(categorySaved);
    }

    @GetMapping("/delete/{categoryId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long categoryId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category deleted!");

        if(!service.delete(categoryId)) {
            response.put("message", "This category is connected to a product. It can't be deleted.");
        }

        return ResponseEntity.ok(response);
    }
}
