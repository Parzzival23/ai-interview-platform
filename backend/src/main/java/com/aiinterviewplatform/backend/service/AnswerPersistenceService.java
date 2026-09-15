package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.entity.Answer;

public interface AnswerPersistenceService {

    Answer saveAnswer(Answer answer);

    Answer saveEvaluation(
            Answer answer,
            GeneratedEvaluation evaluation
    );
}