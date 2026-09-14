package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import com.aiinterviewplatform.backend.repository.InterviewQuestionRepository;
import com.aiinterviewplatform.backend.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewPersistenceService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    public InterviewPersistenceService(
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository
    ) {
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
    }

    @Transactional
    public Interview saveInterviewWithQuestions(
            Interview interview,
            List<InterviewQuestion> questions
    ) {
        Interview savedInterview = interviewRepository.save(interview);

        interviewQuestionRepository.saveAll(questions);

        return savedInterview;
    }
}