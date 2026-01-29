package com.ecommerce.ecommerce.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedProductResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productDescription;
    private BigDecimal productPrice;
    private String productUrl;
    private int quantity;
    private LocalDateTime purchasedAt;
}
