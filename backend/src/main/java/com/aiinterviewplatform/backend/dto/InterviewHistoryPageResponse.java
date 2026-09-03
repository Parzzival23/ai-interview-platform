package com.aiinterviewplatform.backend.dto;

import java.util.List;

public record InterviewHistoryPageResponse(
        List<InterviewHistoryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}