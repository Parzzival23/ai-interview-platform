package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.QuestionType;

import java.util.UUID;

public record InterviewQuestionResponse(
        UUID id,
        String questionText,
        Integer questionOrder,
        QuestionType questionType
) {
}