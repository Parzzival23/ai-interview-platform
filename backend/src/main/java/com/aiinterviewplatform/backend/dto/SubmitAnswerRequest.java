package com.aiinterviewplatform.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(

        @NotNull
        Long timeTaken,

        @NotBlank
        String answerText

) {
}