package com.ecommerce.ecommerce.models;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FetchProductResponse {
    private String title;
    private String description;
    private Boolean inStock;
    private BigDecimal price;
    private String productUrl;
}
