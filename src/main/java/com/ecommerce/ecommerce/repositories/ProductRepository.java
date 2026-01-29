package com.ecommerce.ecommerce.repositories;

import com.ecommerce.ecommerce.schemas.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

    @Query("SELECT p FROM Products p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<Products> searchProducts(@Param("searchQuery") String searchQuery, Pageable pageable);

    @Query("SELECT p FROM Products p WHERE p.quantity < :threshold")
    java.util.List<Products> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT p FROM Products p WHERE p.quantity = 0")
    java.util.List<Products> findOutOfStockProducts();

    @Query("SELECT p FROM Products p WHERE p.categoryId = :categoryId")
    java.util.List<Products> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(p) FROM Products p WHERE p.categoryId = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
