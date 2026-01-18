package com.ecommerce.ecommerce.models;


import lombok.Data;

@Data
public class FetchProductRequest {
    private int page;
    private int limit;
    private String searchQuery;
}
