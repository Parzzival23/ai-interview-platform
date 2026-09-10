package com.aiinterviewplatform.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvaluateAnswerRequest(
       @NotNull @Min(0) @Max(10) Double score,
       @NotBlank String feedback
) {
}