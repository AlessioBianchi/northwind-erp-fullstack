package com.github.alessiobianchi.erp.controller;

import com.github.alessiobianchi.erp.config.OpenApiConfig;
import com.github.alessiobianchi.erp.dto.CategoryDTO;
import com.github.alessiobianchi.erp.service.CategoriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "Product categories (e.g. Beverages, Condiments).")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {

    private final CategoriesService service;

    public CategoriesController(CategoriesService service) {
        this.service = service;
    }

    @Operation(summary = "List categories", description = "Returns all categories, newest first.")
    @ApiResponse(responseCode = "200", description = "Category list")
    @GetMapping
    public List<CategoryDTO> findAllByOrderByCategoryIdDesc() {
        return service.findAllByOrderByCategoryIdDesc();
    }

    @Operation(summary = "Create a category", description = "Creates a new category.")
    @ApiResponse(responseCode = "201", description = "Category created")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @PostMapping()
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO categoryDto) {
        CategoryDTO categorySaved = service.create(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categorySaved);
    }

    @Operation(summary = "Update a category", description = "Updates the category.\n\n- 404 if the category does not exist")
    @ApiResponse(responseCode = "200", description = "Category updated")
    @ApiResponse(responseCode = "400", ref = OpenApiConfig.BAD_REQUEST)
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> update(@PathVariable int categoryId, @Valid @RequestBody CategoryDTO categoryDto) {
        CategoryDTO categorySaved = service.update(categoryId, categoryDto);
        return ResponseEntity.ok(categorySaved);
    }

    @Operation(summary = "Delete a category",
            description = "Deletes the category.\n\n- 404 if the category does not exist\n" +
                    "- 409 if products still belong to it")
    @ApiResponse(responseCode = "204", description = "Category deleted")
    @ApiResponse(responseCode = "404", ref = OpenApiConfig.NOT_FOUND)
    @ApiResponse(responseCode = "409", ref = OpenApiConfig.CONFLICT)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable int categoryId) {
        service.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
