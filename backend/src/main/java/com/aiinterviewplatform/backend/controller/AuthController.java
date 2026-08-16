package com.aiinterviewplatform.backend.controller;

import com.aiinterviewplatform.backend.dto.LoginRequest;
import com.aiinterviewplatform.backend.dto.LoginResponse;
import com.aiinterviewplatform.backend.dto.RegisterRequest;
import com.aiinterviewplatform.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.aiinterviewplatform.backend.dto.RegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterResponse("User registered successfully"));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new LoginResponse(token));
    }
}