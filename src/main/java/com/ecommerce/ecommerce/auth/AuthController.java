package com.ecommerce.ecommerce.auth;


import com.ecommerce.ecommerce.models.LoginRequest;
import com.ecommerce.ecommerce.models.LoginResponse;
import com.ecommerce.ecommerce.models.RegisterRequest;
import com.ecommerce.ecommerce.models.RegisterResponse;
import com.ecommerce.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest payload) {
        return ResponseEntity.ok(authService.loginUser(payload));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest payload){
        return new ResponseEntity<>(authService.registerUser(payload), HttpStatus.CREATED);
    }
    @PostMapping("/change-password")
    public RegisterResponse changedPassword(@Valid @RequestBody String payload){
        authService.changePassword(payload);
        return null;
    }

}
