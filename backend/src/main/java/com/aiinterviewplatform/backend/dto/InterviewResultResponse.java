package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.Difficulty;
import com.aiinterviewplatform.backend.entity.InterviewStatus;

import java.util.List;
import java.util.UUID;

public record InterviewResultResponse(
        UUID interviewId,
        String topic,
        Difficulty difficulty,
        InterviewStatus status,
        int totalQuestions,
        int answeredQuestions,
        double totalScore,
        double averageScore,
        List<InterviewResultQuestionResponse> questions
) {
}