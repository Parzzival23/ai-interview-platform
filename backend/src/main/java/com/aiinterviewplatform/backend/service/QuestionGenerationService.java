package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;

import java.util.List;

public interface QuestionGenerationService {

    List<InterviewQuestion> generateQuestions(
            Interview interview,
            int numberOfQuestions
    );
}