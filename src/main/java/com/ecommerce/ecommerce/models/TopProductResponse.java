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
public class TopProductResponse {
    private Long productId;
    private String productTitle;
    private String productUrl;
    private BigDecimal price;
    private long totalSold;
    private BigDecimal totalRevenue;
    private String categoryName;
}
