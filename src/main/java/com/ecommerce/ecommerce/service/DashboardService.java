package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.models.*;
import com.ecommerce.ecommerce.repositories.*;
import com.ecommerce.ecommerce.schemas.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PurchasedProductRepository purchasedProductRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static final int LOW_STOCK_THRESHOLD = 10;

    public ProductResponse<DashboardResponse> getDashboard() {
        try {
            DashboardResponse dashboard = DashboardResponse.builder()
                    .overview(buildOverviewStats())
                    .topProducts(getTopProducts(10))
                    .categoryStats(getCategoryStats())
                    .lowStockProducts(getLowStockProducts())
                    .recentOrders(getRecentOrders(10))
                    .build();

            return ProductResponse.<DashboardResponse>builder()
                    .status("success")
                    .message("Dashboard data fetched successfully")
                    .data(dashboard)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching dashboard data", e);
            return ProductResponse.<DashboardResponse>builder()
                    .status("error")
                    .message("Failed to fetch dashboard: " + e.getMessage())
                    .build();
        }
    }

    public ProductResponse<DashboardResponse.OverviewStats> getOverviewStats() {
        try {
            DashboardResponse.OverviewStats stats = buildOverviewStats();
            return ProductResponse.<DashboardResponse.OverviewStats>builder()
                    .status("success")
                    .message("Overview stats fetched successfully")
                    .data(stats)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching overview stats", e);
            return ProductResponse.<DashboardResponse.OverviewStats>builder()
                    .status("error")
                    .message("Failed to fetch overview stats: " + e.getMessage())
                    .build();
        }
    }

    private DashboardResponse.OverviewStats buildOverviewStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.with(LocalTime.MIN);
        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfMonth = now.minusDays(30);

        List<Products> lowStock = productRepository.findLowStockProducts(LOW_STOCK_THRESHOLD);
        List<Products> outOfStock = productRepository.findOutOfStockProducts();

        return DashboardResponse.OverviewStats.builder()
                .totalRevenue(orderRepository.getTotalRevenue())
                .todayRevenue(orderRepository.getRevenueSince(startOfToday))
                .weekRevenue(orderRepository.getRevenueSince(startOfWeek))
                .monthRevenue(orderRepository.getRevenueSince(startOfMonth))
                .totalOrders(orderRepository.count())
                .todayOrders(orderRepository.countOrdersSince(startOfToday))
                .weekOrders(orderRepository.countOrdersSince(startOfWeek))
                .monthOrders(orderRepository.countOrdersSince(startOfMonth))
                .totalUsers(userRepository.count())
                .newUsersToday(userRepository.countNewUsersSince(startOfToday))
                .newUsersWeek(userRepository.countNewUsersSince(startOfWeek))
                .newUsersMonth(userRepository.countNewUsersSince(startOfMonth))
                .totalProducts(productRepository.count())
                .lowStockCount(lowStock.size())
                .outOfStockCount(outOfStock.size())
                .totalItemsSold(purchasedProductRepository.getTotalItemsSold())
                .build();
    }

    public ProductResponse<List<TopProductResponse>> getTopSellingProducts(int limit) {
        try {
            List<TopProductResponse> topProducts = getTopProducts(limit);
            return ProductResponse.<List<TopProductResponse>>builder()
                    .status("success")
                    .message("Top products fetched successfully")
                    .data(topProducts)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching top products", e);
            return ProductResponse.<List<TopProductResponse>>builder()
                    .status("error")
                    .message("Failed to fetch top products: " + e.getMessage())
                    .build();
        }
    }

    private List<TopProductResponse> getTopProducts(int limit) {
        List<Object[]> topSelling = purchasedProductRepository.findTopSellingProducts();
        List<TopProductResponse> result = new ArrayList<>();

        int count = 0;
        for (Object[] row : topSelling) {
            if (count >= limit) break;

            Long productId = (Long) row[0];
            Long totalSold = (Long) row[1];

            Optional<Products> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Products product = productOpt.get();
                String categoryName = getCategoryName(product.getCategoryId());

                result.add(TopProductResponse.builder()
                        .productId(productId)
                        .productTitle(product.getTitle())
                        .productUrl(product.getProductUrl())
                        .price(product.getPrice())
                        .totalSold(totalSold)
                        .totalRevenue(product.getPrice().multiply(BigDecimal.valueOf(totalSold)))
                        .categoryName(categoryName)
                        .build());
                count++;
            }
        }

        return result;
    }

    public ProductResponse<List<CategoryStatsResponse>> getCategoryStatistics() {
        try {
            List<CategoryStatsResponse> stats = getCategoryStats();
            return ProductResponse.<List<CategoryStatsResponse>>builder()
                    .status("success")
                    .message("Category stats fetched successfully")
                    .data(stats)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching category stats", e);
            return ProductResponse.<List<CategoryStatsResponse>>builder()
                    .status("error")
                    .message("Failed to fetch category stats: " + e.getMessage())
                    .build();
        }
    }

    private List<CategoryStatsResponse> getCategoryStats() {
        List<Category> categories = categoryRepository.findAll();
        List<Object[]> topSelling = purchasedProductRepository.findTopSellingProducts();

        Map<Long, Long> productSales = topSelling.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a
                ));

        List<CategoryStatsResponse> result = new ArrayList<>();

        for (Category category : categories) {
            List<Products> categoryProducts = productRepository.findByCategoryId(category.getId());

            long totalSold = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Products product : categoryProducts) {
                Long sold = productSales.getOrDefault(product.getId(), 0L);
                totalSold += sold;
                totalRevenue = totalRevenue.add(product.getPrice().multiply(BigDecimal.valueOf(sold)));
            }

            result.add(CategoryStatsResponse.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getName())
                    .productCount(categoryProducts.size())
                    .totalSold(totalSold)
                    .totalRevenue(totalRevenue)
                    .build());
        }

        result.sort((a, b) -> Long.compare(b.getTotalSold(), a.getTotalSold()));

        return result;
    }

    public ProductResponse<List<LowStockProductResponse>> getLowStockAlerts() {
        try {
            List<LowStockProductResponse> lowStock = getLowStockProducts();
            return ProductResponse.<List<LowStockProductResponse>>builder()
                    .status("success")
                    .message("Low stock alerts fetched successfully")
                    .data(lowStock)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching low stock alerts", e);
            return ProductResponse.<List<LowStockProductResponse>>builder()
                    .status("error")
                    .message("Failed to fetch low stock alerts: " + e.getMessage())
                    .build();
        }
    }

    private List<LowStockProductResponse> getLowStockProducts() {
        List<Products> lowStockProducts = productRepository.findLowStockProducts(LOW_STOCK_THRESHOLD);

        return lowStockProducts.stream()
                .map(product -> LowStockProductResponse.builder()
                        .productId(product.getId())
                        .productTitle(product.getTitle())
                        .productUrl(product.getProductUrl())
                        .currentStock(product.getQuantity())
                        .price(product.getPrice())
                        .categoryName(getCategoryName(product.getCategoryId()))
                        .build())
                .sorted((a, b) -> Integer.compare(a.getCurrentStock(), b.getCurrentStock()))
                .collect(Collectors.toList());
    }

    public ProductResponse<List<RecentOrderResponse>> getRecentOrdersList(int limit) {
        try {
            List<RecentOrderResponse> orders = getRecentOrders(limit);
            return ProductResponse.<List<RecentOrderResponse>>builder()
                    .status("success")
                    .message("Recent orders fetched successfully")
                    .data(orders)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching recent orders", e);
            return ProductResponse.<List<RecentOrderResponse>>builder()
                    .status("error")
                    .message("Failed to fetch recent orders: " + e.getMessage())
                    .build();
        }
    }

    private List<RecentOrderResponse> getRecentOrders(int limit) {
        List<Orders> orders = orderRepository.findAll(
                PageRequest.of(0, limit, Sort.by("createdAt").descending())
        ).getContent();

        List<RecentOrderResponse> result = new ArrayList<>();

        for (Orders order : orders) {
            Optional<EcommerceUsers> userOpt = userRepository.findById(order.getUserId());
            List<PurchasedProducts> items = purchasedProductRepository.findByOrderId(order.getId());

            List<RecentOrderResponse.OrderItemResponse> orderItems = items.stream()
                    .map(item -> {
                        Optional<Products> productOpt = productRepository.findById(item.getProductId());
                        return RecentOrderResponse.OrderItemResponse.builder()
                                .productId(item.getProductId())
                                .productTitle(productOpt.map(Products::getTitle).orElse("Unknown"))
                                .quantity(item.getQuantity())
                                .priceAtPurchase(item.getPriceAtPurchase())
                                .build();
                    })
                    .collect(Collectors.toList());

            result.add(RecentOrderResponse.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .userEmail(userOpt.map(EcommerceUsers::getEmail).orElse("Unknown"))
                    .userName(userOpt.map(u -> u.getFirstName() + " " + u.getLastName()).orElse("Unknown"))
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .itemCount(items.size())
                    .createdAt(order.getCreatedAt())
                    .items(orderItems)
                    .build());
        }

        return result;
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "Uncategorized";
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse("Uncategorized");
    }
}
