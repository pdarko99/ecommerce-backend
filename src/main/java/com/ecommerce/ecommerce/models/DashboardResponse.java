package com.ecommerce.ecommerce.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private OverviewStats overview;
    private List<TopProductResponse> topProducts;
    private List<CategoryStatsResponse> categoryStats;
    private List<LowStockProductResponse> lowStockProducts;
    private List<RecentOrderResponse> recentOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewStats {
        private BigDecimal totalRevenue;
        private BigDecimal todayRevenue;
        private BigDecimal weekRevenue;
        private BigDecimal monthRevenue;

        private long totalOrders;
        private long todayOrders;
        private long weekOrders;
        private long monthOrders;

        private long totalUsers;
        private long newUsersToday;
        private long newUsersWeek;
        private long newUsersMonth;

        private long totalProducts;
        private long lowStockCount;
        private long outOfStockCount;

        private long totalItemsSold;
    }
}
