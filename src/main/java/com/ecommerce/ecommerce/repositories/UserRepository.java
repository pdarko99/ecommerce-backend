package com.ecommerce.ecommerce.repositories;

import com.ecommerce.ecommerce.schemas.EcommerceUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<EcommerceUsers, Long> {
    Optional<EcommerceUsers> findByEmail(String email);
    boolean existsByEmail(String email);

}
