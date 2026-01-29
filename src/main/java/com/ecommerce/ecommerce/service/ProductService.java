package com.ecommerce.ecommerce.service;


import com.ecommerce.ecommerce.models.CreateProductRequest;
import com.ecommerce.ecommerce.models.FetchProductResponse;
import com.ecommerce.ecommerce.models.ProductResponse;
import com.ecommerce.ecommerce.models.UpdateProductRequest;
import com.ecommerce.ecommerce.repositories.CategoryRepository;
import com.ecommerce.ecommerce.repositories.ProductRepository;
import com.ecommerce.ecommerce.schemas.Category;
import com.ecommerce.ecommerce.schemas.Products;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public ProductResponse<List<FetchProductResponse>> fetchProducts(int page, int limit, String searchQuery) {
        try {
            Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

            Page<Products> productsPage;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                productsPage = productRepository.searchProducts(searchQuery, pageable);
            } else {
                productsPage = productRepository.findAll(pageable);
            }

            List<FetchProductResponse> productList = productsPage.getContent().stream()
                    .map(this::mapToFetchProductResponse)
                    .toList();

            return ProductResponse.<List<FetchProductResponse>>builder()
                    .status("success")
                    .message("Products fetched successfully")
                    .data(productList)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching products", e);
            return ProductResponse.<List<FetchProductResponse>>builder()
                    .status("error")
                    .message("Failed to fetch products: " + e.getMessage())
                    .build();
        }
    }

    public ProductResponse<FetchProductResponse> getProductById(Long productId) {
        try {
            Optional<Products> productOpt = productRepository.findById(productId);

            if (productOpt.isEmpty()) {
                return ProductResponse.<FetchProductResponse>builder()
                        .status("error")
                        .message("Product not found")
                        .build();
            }

            return ProductResponse.<FetchProductResponse>builder()
                    .status("success")
                    .message("Product fetched successfully")
                    .data(mapToFetchProductResponse(productOpt.get()))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching product by ID: {}", productId, e);
            return ProductResponse.<FetchProductResponse>builder()
                    .status("error")
                    .message("Failed to fetch product: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<FetchProductResponse> createProduct(CreateProductRequest payload) {
        try {
            String productUrl = null;
            if (payload.getProductImage() != null && !payload.getProductImage().isEmpty()) {
                productUrl = fileStorageService.storeImage(payload.getProductImage());
            } else {
                productUrl = "";
            }

            Products product = new Products();
            product.setTitle(payload.getTitle());
            product.setDescription(payload.getDescription());
            product.setQuantity(payload.getQuantity());
            product.setPrice(payload.getAmount());
            product.setProductUrl(productUrl != null ? productUrl : "");
            product.setCategoryId(payload.getCategoryId());

            Products savedProduct = productRepository.save(product);
            log.info("Product created successfully with ID: {}", savedProduct.getId());

            return ProductResponse.<FetchProductResponse>builder()
                    .status("success")
                    .message("Product created successfully")
                    .data(mapToFetchProductResponse(savedProduct))
                    .build();
        } catch (Exception e) {
            log.error("Error creating product", e);
            return ProductResponse.<FetchProductResponse>builder()
                    .status("error")
                    .message("Failed to create product: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<FetchProductResponse> updateProduct(UpdateProductRequest payload) {
        try {
            Optional<Products> existingProductOpt = productRepository.findById(payload.getProductId());

            if (existingProductOpt.isEmpty()) {
                return ProductResponse.<FetchProductResponse>builder()
                        .status("error")
                        .message("Product not found")
                        .build();
            }

            Products existingProduct = existingProductOpt.get();

            if (payload.getTitle() != null) {
                existingProduct.setTitle(payload.getTitle());
            }
            if (payload.getDescription() != null) {
                existingProduct.setDescription(payload.getDescription());
            }
            if (payload.getQuantity() > 0) {
                existingProduct.setQuantity(payload.getQuantity());
            }
            if (payload.getAmount() != null) {
                existingProduct.setPrice(payload.getAmount());
            }
            if (payload.getProductImage() != null && !payload.getProductImage().isEmpty()) {
                if (existingProduct.getProductUrl() != null && !existingProduct.getProductUrl().isEmpty()) {
                    fileStorageService.deleteImage(existingProduct.getProductUrl());
                }
                String newProductUrl = fileStorageService.storeImage(payload.getProductImage());
                existingProduct.setProductUrl(newProductUrl);
            }
            if (payload.getCategoryId() != null) {
                existingProduct.setCategoryId(payload.getCategoryId());
            }

            Products updatedProduct = productRepository.save(existingProduct);
            log.info("Product updated successfully with ID: {}", updatedProduct.getId());

            return ProductResponse.<FetchProductResponse>builder()
                    .status("success")
                    .message("Product updated successfully")
                    .data(mapToFetchProductResponse(updatedProduct))
                    .build();
        } catch (Exception e) {
            log.error("Error updating product", e);
            return ProductResponse.<FetchProductResponse>builder()
                    .status("error")
                    .message("Failed to update product: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<Void> deleteProduct(Long productId) {
        try {
            Optional<Products> productOpt = productRepository.findById(productId);

            if (productOpt.isEmpty()) {
                return ProductResponse.<Void>builder()
                        .status("error")
                        .message("Product not found")
                        .build();
            }

            Products product = productOpt.get();
            fileStorageService.deleteImage(product.getProductUrl());
            productRepository.delete(product);

            log.info("Product deleted successfully with ID: {}", productId);

            return ProductResponse.<Void>builder()
                    .status("success")
                    .message("Product deleted successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error deleting product", e);
            return ProductResponse.<Void>builder()
                    .status("error")
                    .message("Failed to delete product: " + e.getMessage())
                    .build();
        }
    }

    private FetchProductResponse mapToFetchProductResponse(Products product) {
        String categoryName = null;
        if (product.getCategoryId() != null) {
            categoryName = categoryRepository.findById(product.getCategoryId())
                    .map(Category::getName)
                    .orElse(null);
        }

        return FetchProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .inStock(product.getQuantity() > 0)
                .price(product.getPrice())
                .productUrl(product.getProductUrl())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .build();
    }
}
