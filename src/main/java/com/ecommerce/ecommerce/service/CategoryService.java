package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.models.CategoryRequest;
import com.ecommerce.ecommerce.models.CategoryResponse;
import com.ecommerce.ecommerce.models.ProductResponse;
import com.ecommerce.ecommerce.repositories.CategoryRepository;
import com.ecommerce.ecommerce.repositories.ProductRepository;
import com.ecommerce.ecommerce.schemas.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductResponse<List<CategoryResponse>> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .toList();

            return ProductResponse.<List<CategoryResponse>>builder()
                    .status("success")
                    .message("Categories fetched successfully")
                    .data(categories)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching categories", e);
            return ProductResponse.<List<CategoryResponse>>builder()
                    .status("error")
                    .message("Failed to fetch categories: " + e.getMessage())
                    .build();
        }
    }

    public ProductResponse<CategoryResponse> getCategoryById(Long id) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(id);

            if (categoryOpt.isEmpty()) {
                return ProductResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Category not found")
                        .build();
            }

            return ProductResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("Category fetched successfully")
                    .data(mapToResponse(categoryOpt.get()))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching category by ID: {}", id, e);
            return ProductResponse.<CategoryResponse>builder()
                    .status("error")
                    .message("Failed to fetch category: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<CategoryResponse> createCategory(CategoryRequest request) {
        try {
            if (categoryRepository.existsByName(request.getName())) {
                return ProductResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Category with this name already exists")
                        .build();
            }

            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());

            Category saved = categoryRepository.save(category);
            log.info("Category created successfully: {}", saved.getName());

            return ProductResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("Category created successfully")
                    .data(mapToResponse(saved))
                    .build();
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ProductResponse.<CategoryResponse>builder()
                    .status("error")
                    .message("Failed to create category: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<CategoryResponse> updateCategory(Long id, CategoryRequest request) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(id);

            if (categoryOpt.isEmpty()) {
                return ProductResponse.<CategoryResponse>builder()
                        .status("error")
                        .message("Category not found")
                        .build();
            }

            Category category = categoryOpt.get();

            if (request.getName() != null && !request.getName().equals(category.getName())) {
                if (categoryRepository.existsByName(request.getName())) {
                    return ProductResponse.<CategoryResponse>builder()
                            .status("error")
                            .message("Category with this name already exists")
                            .build();
                }
                category.setName(request.getName());
            }

            if (request.getDescription() != null) {
                category.setDescription(request.getDescription());
            }

            Category updated = categoryRepository.save(category);
            log.info("Category updated successfully: {}", updated.getName());

            return ProductResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("Category updated successfully")
                    .data(mapToResponse(updated))
                    .build();
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ProductResponse.<CategoryResponse>builder()
                    .status("error")
                    .message("Failed to update category: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<Void> deleteCategory(Long id) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(id);

            if (categoryOpt.isEmpty()) {
                return ProductResponse.<Void>builder()
                        .status("error")
                        .message("Category not found")
                        .build();
            }

            long productCount = productRepository.countByCategoryId(id);
            if (productCount > 0) {
                return ProductResponse.<Void>builder()
                        .status("error")
                        .message("Cannot delete category with " + productCount + " products. Reassign products first.")
                        .build();
            }

            categoryRepository.delete(categoryOpt.get());
            log.info("Category deleted successfully: {}", id);

            return ProductResponse.<Void>builder()
                    .status("success")
                    .message("Category deleted successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return ProductResponse.<Void>builder()
                    .status("error")
                    .message("Failed to delete category: " + e.getMessage())
                    .build();
        }
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .productCount(productRepository.countByCategoryId(category.getId()))
                .createdAt(category.getCreatedAt())
                .build();
    }
}
