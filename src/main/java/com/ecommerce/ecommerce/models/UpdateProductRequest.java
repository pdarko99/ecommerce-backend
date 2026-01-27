package com.ecommerce.ecommerce.models;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    private Long productId;

    private String title;

    private String description;

    private int quantity;

    private BigDecimal amount;

    private MultipartFile productImage;
}
