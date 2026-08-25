package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.CreateInterviewRequest;
import com.aiinterviewplatform.backend.dto.InterviewQuestionResponse;
import com.aiinterviewplatform.backend.dto.InterviewResponse;
import com.aiinterviewplatform.backend.entity.*;
import com.aiinterviewplatform.backend.exception.InterviewAlreadyStartedException;
import com.aiinterviewplatform.backend.exception.InterviewNotFoundException;
import com.aiinterviewplatform.backend.repository.InterviewQuestionRepository;
import com.aiinterviewplatform.backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    public InterviewService(
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository) {

        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
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

    public InterviewResponse getInterview(UUID interviewId, User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));

        return new InterviewResponse(
                interview.getId(),
                interview.getTopic(),
                interview.getDifficulty(),
                interview.getStatus(),
                interview.getCreatedAt(),
                interview.getStartedAt(),
                interview.getCompletedAt()
        );
    }
    public InterviewResponse startInterview(UUID interviewId, User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        if (interview.getStatus() != InterviewStatus.NOT_STARTED) {
            throw new InterviewAlreadyStartedException(
                    "Interview has already been started"
            );
        }

        interview.start();

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
    public List<InterviewQuestionResponse> getInterviewQuestions(
            UUID interviewId,
            User user) {

        interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        List<InterviewQuestion> questions =
                interviewQuestionRepository
                        .findByInterviewIdOrderByQuestionOrder(interviewId);

        return questions.stream()
                .map(question -> new InterviewQuestionResponse(
                        question.getId(),
                        question.getQuestionText(),
                        question.getQuestionOrder(),
                        question.getQuestionType()
                ))
                .toList();
    }
}