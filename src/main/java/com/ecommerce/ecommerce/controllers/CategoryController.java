package com.ecommerce.ecommerce.controllers;

import com.ecommerce.ecommerce.models.CategoryRequest;
import com.ecommerce.ecommerce.models.CategoryResponse;
import com.ecommerce.ecommerce.models.ProductResponse;
import com.ecommerce.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ProductResponse<List<CategoryResponse>>> getAllCategories() {
        ProductResponse<List<CategoryResponse>> response = categoryService.getAllCategories();
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        ProductResponse<CategoryResponse> response = categoryService.getCategoryById(id);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        ProductResponse<CategoryResponse> response = categoryService.createCategory(request);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        ProductResponse<CategoryResponse> response = categoryService.updateCategory(id, request);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse<Void>> deleteCategory(@PathVariable Long id) {
        ProductResponse<Void> response = categoryService.deleteCategory(id);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
