package com.ecommerce.ecommerce.controllers;

import com.ecommerce.ecommerce.models.BulkPurchaseRequest;
import com.ecommerce.ecommerce.models.ProductResponse;
import com.ecommerce.ecommerce.models.PurchaseProductRequest;
import com.ecommerce.ecommerce.models.PurchasedProductResponse;
import com.ecommerce.ecommerce.service.PurchasedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchased-products")
@RequiredArgsConstructor
public class PurchasedProductController {

    private final PurchasedProductService purchasedProductService;

    @GetMapping
    public ResponseEntity<ProductResponse<List<PurchasedProductResponse>>> getPurchasedProducts(
            @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        ProductResponse<List<PurchasedProductResponse>> response = purchasedProductService.getPurchasedProducts(token);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse<PurchasedProductResponse>> purchaseProduct(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PurchaseProductRequest request) {
        String token = extractToken(authHeader);
        ProductResponse<PurchasedProductResponse> response = purchasedProductService.purchaseProduct(token, request);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ProductResponse<List<PurchasedProductResponse>>> purchaseProducts(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BulkPurchaseRequest request) {
        String token = extractToken(authHeader);
        ProductResponse<List<PurchasedProductResponse>> response = purchasedProductService.purchaseProducts(token, request);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
