package com.ecommerce.ecommerce.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long userId;
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private Long expiresAt;
}
