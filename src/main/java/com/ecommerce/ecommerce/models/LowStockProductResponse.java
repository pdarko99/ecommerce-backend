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
public class LowStockProductResponse {
    private Long productId;
    private String productTitle;
    private String productUrl;
    private int currentStock;
    private BigDecimal price;
    private String categoryName;
}
