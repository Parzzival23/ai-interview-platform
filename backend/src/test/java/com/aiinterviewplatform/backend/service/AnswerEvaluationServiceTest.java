package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.ai.GeminiAIClient;
import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import com.aiinterviewplatform.backend.entity.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerEvaluationServiceTest {

    @Mock
    private GeminiAIClient geminiAIClient;

    private AnswerEvaluationService answerEvaluationService;

    @BeforeEach
    void setUp() {
        answerEvaluationService =
                new AnswerEvaluationServiceImpl(geminiAIClient);
    }

    @Test
    void evaluateAnswer_shouldReturnGeneratedEvaluation() {
        InterviewQuestion question =
                new InterviewQuestion(
                        null,
                        null,
                        "Explain polymorphism in Java.",
                        1,
                        QuestionType.TECHNICAL
                );

        String answer =
                "Polymorphism allows the same interface to have different implementations.";

        GeneratedEvaluation expectedEvaluation =
                new GeneratedEvaluation(
                        8.0,
                        "The answer correctly explains the core concept."
                );

        when(geminiAIClient.evaluateAnswer(
                question.getQuestionText(),
                answer
        )).thenReturn(expectedEvaluation);

        GeneratedEvaluation result =
                answerEvaluationService.evaluateAnswer(
                        question,
                        answer
                );

        assertThat(result).isSameAs(expectedEvaluation);
        assertThat(result.getScore()).isEqualTo(8.0);
        assertThat(result.getFeedback())
                .isEqualTo("The answer correctly explains the core concept.");

        verify(geminiAIClient).evaluateAnswer(
                question.getQuestionText(),
                answer
        );
    }
}