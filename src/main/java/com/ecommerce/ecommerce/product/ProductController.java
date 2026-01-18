package com.ecommerce.ecommerce.product;

import com.ecommerce.ecommerce.models.FetchProductRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/product")
public class ProductController {

    @GetMapping("/fetch")
    public FetchProductRequest fetchProducts(FetchProductRequest payload){
        return null;
    }

}
