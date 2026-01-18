package com.ecommerce.ecommerce.models;


import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private Boolean isAdmin;
}
