package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.entity.Answer;
import com.aiinterviewplatform.backend.repository.AnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerPersistenceServiceImpl
        implements AnswerPersistenceService {

    private final AnswerRepository answerRepository;

    public AnswerPersistenceServiceImpl(
            AnswerRepository answerRepository
    ) {
        this.answerRepository = answerRepository;
    }

    @Override
    @Transactional
    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public Answer saveEvaluation(
            Answer answer,
            GeneratedEvaluation evaluation
    ) {
        answer.setScore(evaluation.getScore());
        answer.setFeedback(evaluation.getFeedback());

        return answerRepository.save(answer);
    }
}