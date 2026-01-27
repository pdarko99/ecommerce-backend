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
public class FetchProductResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean inStock;
    private int quantity;
    private BigDecimal price;
    private String productUrl;
}
