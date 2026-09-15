package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;

public interface AnswerEvaluationService {

    GeneratedEvaluation evaluateAnswer(
            InterviewQuestion question,
            String answerText
    );
}