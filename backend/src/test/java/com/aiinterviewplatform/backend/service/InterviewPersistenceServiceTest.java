package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.entity.Difficulty;
import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.repository.InterviewQuestionRepository;
import com.aiinterviewplatform.backend.repository.InterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewPersistenceServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewQuestionRepository interviewQuestionRepository;

    private InterviewPersistenceService interviewPersistenceService;

    @BeforeEach
    void setUp() {
        interviewPersistenceService =
                new InterviewPersistenceService(
                        interviewRepository,
                        interviewQuestionRepository
                );
    }

    @Test
    void saveInterviewWithQuestions_savesInterviewAndQuestions() {

        User user = new User();

        Interview interview = new Interview(
                user,
                "Java",
                Difficulty.MEDIUM,
                2
        );

        List<InterviewQuestion> questions = List.of(
                new InterviewQuestion(
                        null,
                        interview,
                        "What is dependency injection?",
                        1,
                        com.aiinterviewplatform.backend.entity.QuestionType.TECHNICAL
                ),
                new InterviewQuestion(
                        null,
                        interview,
                        "What is the JVM?",
                        2,
                        com.aiinterviewplatform.backend.entity.QuestionType.TECHNICAL
                )
        );

        when(interviewRepository.save(interview))
                .thenReturn(interview);

        Interview result =
                interviewPersistenceService.saveInterviewWithQuestions(
                        interview,
                        questions
                );

        assertThat(result).isSameAs(interview);

        verify(interviewRepository).save(interview);

        verify(interviewQuestionRepository)
                .saveAll(questions);
    }
}