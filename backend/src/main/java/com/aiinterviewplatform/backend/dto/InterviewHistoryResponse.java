package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.Difficulty;
import com.aiinterviewplatform.backend.entity.InterviewStatus;

import java.time.Instant;
import java.util.UUID;

public record InterviewHistoryResponse(
        UUID id,
        String topic,
        Difficulty difficulty,
        InterviewStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {}