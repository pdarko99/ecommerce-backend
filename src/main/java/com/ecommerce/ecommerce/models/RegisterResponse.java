package com.ecommerce.ecommerce.models;


import lombok.Data;

@Data
public class RegisterResponse {
    private String token;
    private String userName;
    private String firstName;
}
