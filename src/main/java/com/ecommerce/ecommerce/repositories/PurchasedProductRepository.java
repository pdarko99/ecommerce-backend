package com.ecommerce.ecommerce.repositories;

import com.ecommerce.ecommerce.schemas.PurchasedProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchasedProductRepository extends JpaRepository<PurchasedProducts, Long> {
    List<PurchasedProducts> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<PurchasedProducts> findByOrderId(Long orderId);

    @Query("SELECT pp.productId, SUM(pp.quantity) as totalQty FROM PurchasedProducts pp GROUP BY pp.productId ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT pp.productId, SUM(pp.quantity) as totalQty FROM PurchasedProducts pp WHERE pp.createdAt >= :startDate GROUP BY pp.productId ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProductsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(pp.quantity), 0) FROM PurchasedProducts pp")
    long getTotalItemsSold();

    @Query("SELECT COALESCE(SUM(pp.quantity), 0) FROM PurchasedProducts pp WHERE pp.createdAt >= :startDate")
    long getItemsSoldSince(@Param("startDate") LocalDateTime startDate);
}
