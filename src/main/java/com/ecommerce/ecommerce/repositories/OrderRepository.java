package com.ecommerce.ecommerce.repositories;

import com.ecommerce.ecommerce.schemas.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Orders> findByStatus(String status);

    @Query("SELECT COUNT(o) FROM Orders o WHERE o.createdAt >= :startDate")
    long countOrdersSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Orders o WHERE o.status = 'COMPLETED'")
    java.math.BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Orders o WHERE o.status = 'COMPLETED' AND o.createdAt >= :startDate")
    java.math.BigDecimal getRevenueSince(@Param("startDate") LocalDateTime startDate);
}
