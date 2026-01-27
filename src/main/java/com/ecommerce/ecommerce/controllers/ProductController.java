package com.ecommerce.ecommerce.controllers;

import com.ecommerce.ecommerce.models.CreateProductRequest;
import com.ecommerce.ecommerce.models.FetchProductResponse;
import com.ecommerce.ecommerce.models.ProductResponse;
import com.ecommerce.ecommerce.models.UpdateProductRequest;
import com.ecommerce.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductResponse<List<FetchProductResponse>>> fetchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String searchQuery) {
        ProductResponse<List<FetchProductResponse>> response = productService.fetchProducts(page, limit, searchQuery);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse<FetchProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse<FetchProductResponse> response = productService.getProductById(id);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse<FetchProductResponse>> createProduct(@ModelAttribute CreateProductRequest payload) {
        ProductResponse<FetchProductResponse> response = productService.createProduct(payload);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse<FetchProductResponse>> updateProduct(@ModelAttribute UpdateProductRequest payload) {
        ProductResponse<FetchProductResponse> response = productService.updateProduct(payload);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse<Void>> deleteProduct(@PathVariable Long id) {
        ProductResponse<Void> response = productService.deleteProduct(id);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
