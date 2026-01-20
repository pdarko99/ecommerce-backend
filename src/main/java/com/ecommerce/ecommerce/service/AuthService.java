package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.models.LoginRequest;
import com.ecommerce.ecommerce.models.RegisterRequest;
import com.ecommerce.ecommerce.schemas.EcommerceUsers;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String loginUser(LoginRequest payload){
        return null;
    }

    public String registerUser(RegisterRequest payload){
        EcommerceUsers newUser = new EcommerceUsers();

        newUser.setEmail(payload.getEmail());
        return null;
    }

    public String changePassword(String payload){
        return null;
    }
}
