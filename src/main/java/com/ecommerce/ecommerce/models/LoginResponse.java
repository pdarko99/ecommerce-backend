package com.ecommerce.ecommerce.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginResponse {
    private String token;
    private String userName;
    private String firstName;
}
