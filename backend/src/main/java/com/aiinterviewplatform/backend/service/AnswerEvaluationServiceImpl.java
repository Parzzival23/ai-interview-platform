package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.ai.GeminiAIClient;
import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import org.springframework.stereotype.Service;

@Service
public class AnswerEvaluationServiceImpl
        implements AnswerEvaluationService {

    private final GeminiAIClient geminiAIClient;

    public AnswerEvaluationServiceImpl(
            GeminiAIClient geminiAIClient
    ) {
        this.geminiAIClient = geminiAIClient;
    }

    @Override
    public GeneratedEvaluation evaluateAnswer(
            InterviewQuestion question,
            String answerText
    ) {
        return geminiAIClient.evaluateAnswer(
                question.getQuestionText(),
                answerText
        );
    }
}