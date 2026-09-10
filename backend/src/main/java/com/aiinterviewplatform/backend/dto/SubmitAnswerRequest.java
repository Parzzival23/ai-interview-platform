package com.aiinterviewplatform.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubmitAnswerRequest(

        @NotNull
        @Positive
        Long timeTaken,

        @NotBlank
        String answerText

) {
}