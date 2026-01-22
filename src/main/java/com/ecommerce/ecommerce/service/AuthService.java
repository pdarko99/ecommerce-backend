package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.exception.EmailAlreadyExistsException;
import com.ecommerce.ecommerce.exception.InvalidCredentialsException;
import com.ecommerce.ecommerce.exception.PasswordMismatchException;
import com.ecommerce.ecommerce.models.LoginRequest;
import com.ecommerce.ecommerce.models.LoginResponse;
import com.ecommerce.ecommerce.models.RegisterRequest;
import com.ecommerce.ecommerce.models.RegisterResponse;
import com.ecommerce.ecommerce.repositories.UserRepository;
import com.ecommerce.ecommerce.schemas.EcommerceUsers;
import com.ecommerce.ecommerce.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.expiration}")
    private Long expiration;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginResponse loginUser(LoginRequest payload) {
        log.info("Login attempt for email: {}", payload.getEmail());

        EcommerceUsers user = userRepository.findByEmail(payload.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(payload.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for email: {}", payload.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .token(token)
                .expiresAt(expiration)
                .build();
    }

    @Transactional
    public RegisterResponse registerUser(RegisterRequest payload) {
        log.info("Registration attempt for email: {}", payload.getEmail());

        if (!payload.getPassword().equals(payload.getConfirmPassword())) {
            throw new PasswordMismatchException("Password and confirmation do not match");
        }

        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }

        EcommerceUsers newUser = new EcommerceUsers();
        BeanUtils.copyProperties(payload, newUser, "password", "confirmPassword");
        newUser.setPassword(passwordEncoder.encode(payload.getPassword()));
        newUser.setIsAdmin(false);

        EcommerceUsers savedUser = userRepository.save(newUser);
        log.info("User registered successfully: {}", savedUser.getEmail());

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .token(token)
                .expiresAt(expiration)
                .build();
    }

    public String changePassword(String payload) {
        return null;
    }
}
