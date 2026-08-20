package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role
) {
}