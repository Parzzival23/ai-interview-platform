package com.aiinterviewplatform.backend.controller;

import com.aiinterviewplatform.backend.dto.RegisterRequest;
import com.aiinterviewplatform.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public void register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);
    }
}