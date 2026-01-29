package com.ecommerce.ecommerce.repositories;

import com.ecommerce.ecommerce.schemas.EcommerceUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<EcommerceUsers, Long> {
    Optional<EcommerceUsers> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM EcommerceUsers u WHERE u.createdAt >= :startDate")
    long countNewUsersSince(@Param("startDate") LocalDateTime startDate);
}
