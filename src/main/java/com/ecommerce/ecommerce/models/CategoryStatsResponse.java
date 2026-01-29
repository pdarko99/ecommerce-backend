package com.ecommerce.ecommerce.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsResponse {
    private Long categoryId;
    private String categoryName;
    private long productCount;
    private long totalSold;
    private BigDecimal totalRevenue;
}
