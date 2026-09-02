package com.aiinterviewplatform.backend.dto;

public record EvaluateAnswerRequest(
        Double score,
        String feedback
) {
}