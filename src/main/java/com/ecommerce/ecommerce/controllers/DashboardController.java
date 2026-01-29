package com.ecommerce.ecommerce.controllers;

import com.ecommerce.ecommerce.models.*;
import com.ecommerce.ecommerce.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ProductResponse<DashboardResponse>> getDashboard() {
        ProductResponse<DashboardResponse> response = dashboardService.getDashboard();
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overview")
    public ResponseEntity<ProductResponse<DashboardResponse.OverviewStats>> getOverview() {
        ProductResponse<DashboardResponse.OverviewStats> response = dashboardService.getOverviewStats();
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-products")
    public ResponseEntity<ProductResponse<List<TopProductResponse>>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        ProductResponse<List<TopProductResponse>> response = dashboardService.getTopSellingProducts(limit);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category-stats")
    public ResponseEntity<ProductResponse<List<CategoryStatsResponse>>> getCategoryStats() {
        ProductResponse<List<CategoryStatsResponse>> response = dashboardService.getCategoryStatistics();
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ProductResponse<List<LowStockProductResponse>>> getLowStock() {
        ProductResponse<List<LowStockProductResponse>> response = dashboardService.getLowStockAlerts();
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<ProductResponse<List<RecentOrderResponse>>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        ProductResponse<List<RecentOrderResponse>> response = dashboardService.getRecentOrdersList(limit);
        if ("error".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
