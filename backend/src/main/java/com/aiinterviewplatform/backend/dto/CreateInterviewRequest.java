package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInterviewRequest(
        @NotBlank String topic,

        @NotNull Difficulty difficulty,

        @NotNull
        @Min(1)
        @Max(20)
        Integer numberOfQuestions
) {}