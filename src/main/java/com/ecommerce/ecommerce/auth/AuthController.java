package com.ecommerce.ecommerce.auth;


import com.ecommerce.ecommerce.models.LoginRequest;
import com.ecommerce.ecommerce.models.LoginResponse;
import com.ecommerce.ecommerce.models.RegisterRequest;
import com.ecommerce.ecommerce.models.RegisterResponse;
import com.ecommerce.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public LoginResponse loginUser(LoginRequest payload) {
        authService.loginUser(payload);
        return null; // temporary
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(RegisterRequest payload){
        authService.registerUser(payload);
        return null;
    }
    @PostMapping("/change-password")
    public RegisterResponse changedPassword(String payload){
        authService.changePassword(payload);
        return null;
    }

}
