package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.ai.GeminiAIClient;
import com.aiinterviewplatform.backend.dto.GeneratedQuestion;
import com.aiinterviewplatform.backend.dto.GeneratedQuestions;
import com.aiinterviewplatform.backend.entity.Difficulty;
import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import com.aiinterviewplatform.backend.entity.QuestionType;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.service.QuestionGenerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionGenerationServiceTest {

    @Mock
    private GeminiAIClient geminiAIClient;

    private QuestionGenerationServiceImpl questionGenerationService;

    @BeforeEach
    void setUp() {
        questionGenerationService =
                new QuestionGenerationServiceImpl(geminiAIClient);
    }

    @Test
    void generateQuestions_mapsGeneratedQuestionsToInterviewQuestions() {

        User user = new User();

        Interview interview = new Interview(
                user,
                "Java",
                Difficulty.MEDIUM,
                2
        );

        GeneratedQuestion question1 = new GeneratedQuestion();
        question1.setQuestionText("What is dependency injection?");
        question1.setQuestionType(QuestionType.TECHNICAL);

        GeneratedQuestion question2 = new GeneratedQuestion();
        question2.setQuestionText("What is the JVM?");
        question2.setQuestionType(QuestionType.TECHNICAL);

        GeneratedQuestions generatedQuestions =
                new GeneratedQuestions();

        generatedQuestions.setQuestions(
                List.of(question1, question2)
        );

        when(geminiAIClient.generateQuestions(
                "Java",
                Difficulty.MEDIUM,
                2
        )).thenReturn(generatedQuestions);

        List<InterviewQuestion> result =
                questionGenerationService.generateQuestions(
                        interview,
                        2
                );

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getInterview())
                .isSameAs(interview);
        assertThat(result.get(0).getQuestionText())
                .isEqualTo("What is dependency injection?");
        assertThat(result.get(0).getQuestionOrder())
                .isEqualTo(1);
        assertThat(result.get(0).getQuestionType())
                .isEqualTo(QuestionType.TECHNICAL);

        assertThat(result.get(1).getInterview())
                .isSameAs(interview);
        assertThat(result.get(1).getQuestionText())
                .isEqualTo("What is the JVM?");
        assertThat(result.get(1).getQuestionOrder())
                .isEqualTo(2);
        assertThat(result.get(1).getQuestionType())
                .isEqualTo(QuestionType.TECHNICAL);

        verify(geminiAIClient).generateQuestions(
                "Java",
                Difficulty.MEDIUM,
                2
        );
    }
}