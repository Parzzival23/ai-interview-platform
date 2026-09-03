package com.aiinterviewplatform.backend.dto;

import java.util.UUID;

public record InterviewResultQuestionResponse(
        UUID questionId,
        String questionText,
        String answerText,
        Long timeTaken,
        Double score,
        String feedback
) {
}