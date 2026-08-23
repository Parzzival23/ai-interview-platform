package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.CreateInterviewRequest;
import com.aiinterviewplatform.backend.dto.InterviewResponse;
import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;

    public InterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    public InterviewResponse createInterview(
            CreateInterviewRequest request,
            User user
    ) {

        Interview interview = new Interview(
                user,
                request.topic(),
                request.difficulty()
        );

        Interview savedInterview = interviewRepository.save(interview);

        return new InterviewResponse(
                savedInterview.getId(),
                savedInterview.getTopic(),
                savedInterview.getDifficulty(),
                savedInterview.getStatus(),
                savedInterview.getCreatedAt(),
                savedInterview.getStartedAt(),
                savedInterview.getCompletedAt()
        );
    }
}
