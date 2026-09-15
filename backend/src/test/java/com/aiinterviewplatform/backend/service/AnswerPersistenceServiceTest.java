package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.entity.Answer;
import com.aiinterviewplatform.backend.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerPersistenceServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    private AnswerPersistenceService answerPersistenceService;

    @BeforeEach
    void setUp() {
        answerPersistenceService =
                new AnswerPersistenceServiceImpl(answerRepository);
    }

    @Test
    void saveEvaluation_shouldUpdateAndPersistEvaluation() {
        Answer answer = new Answer();

        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        "The answer correctly explains the core concept."
                );

        when(answerRepository.save(answer))
                .thenReturn(answer);

        Answer result =
                answerPersistenceService.saveEvaluation(
                        answer,
                        evaluation
                );

        assertThat(result).isSameAs(answer);
        assertThat(answer.getScore()).isEqualTo(8.0);
        assertThat(answer.getFeedback())
                .isEqualTo("The answer correctly explains the core concept.");

        verify(answerRepository).save(answer);
    }
}