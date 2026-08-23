package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInterviewRequest(
        @NotBlank String topic,
        @NotNull Difficulty difficulty
) {}