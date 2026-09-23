package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.GeneratedEvaluation;
import com.aiinterviewplatform.backend.dto.SubmitAnswerRequest;
import com.aiinterviewplatform.backend.entity.Answer;
import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import com.aiinterviewplatform.backend.entity.InterviewStatus;

import com.aiinterviewplatform.backend.entity.User;

import com.aiinterviewplatform.backend.exception.AIResponseParsingException;
import com.aiinterviewplatform.backend.exception.AnswerAlreadySubmittedException;
import com.aiinterviewplatform.backend.repository.AnswerRepository;
import com.aiinterviewplatform.backend.repository.InterviewQuestionRepository;
import com.aiinterviewplatform.backend.repository.InterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewQuestionRepository interviewQuestionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionGenerationService questionGenerationService;

    @Mock
    private InterviewPersistenceService interviewPersistenceService;

    @Mock
    private AnswerPersistenceService answerPersistenceService;

    @Mock
    private AnswerEvaluationService answerEvaluationService;

    private InterviewService interviewService;

    @BeforeEach
    void setUp() {
        interviewService = new InterviewService(
                interviewRepository,
                interviewQuestionRepository,
                answerRepository,
                questionGenerationService,
                interviewPersistenceService,
                answerPersistenceService,
                answerEvaluationService
        );
    }

    @Test
    void submitAnswer_shouldSaveAnswerThenEvaluateAndPersistEvaluation() {

        // Arrange
        UUID interviewId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        User user = new User();
        Interview interview = mock(Interview.class);
        InterviewQuestion question = mock(InterviewQuestion.class);

        when(interviewRepository.findByIdAndUser(interviewId, user))
                .thenReturn(Optional.of(interview));

        when(interview.getStatus())
                .thenReturn(InterviewStatus.IN_PROGRESS);

        when(interview.getId())
                .thenReturn(interviewId);

        when(interviewQuestionRepository.findById(questionId))
                .thenReturn(Optional.of(question));

        when(question.getInterview())
                .thenReturn(interview);

        when(answerRepository.findByQuestion(question))
                .thenReturn(Optional.empty());

        SubmitAnswerRequest request =
                new SubmitAnswerRequest(
                        30L,
                        "Polymorphism allows an object to take multiple forms."
                );

        Answer savedAnswer = new Answer();
        savedAnswer.setQuestion(question);
        savedAnswer.setAnswerText(request.answerText());
        savedAnswer.setTimeTaken(request.timeTaken());

        when(answerPersistenceService.saveAnswer(any(Answer.class)))
                .thenReturn(savedAnswer);

        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        "The answer correctly explains the core concept."
                );

        when(answerEvaluationService.evaluateAnswer(
                question,
                request.answerText()
        )).thenReturn(evaluation);

        // Act
        GeneratedEvaluation result = interviewService.submitAnswer(
                interviewId,
                questionId,
                request,
                user
        );

        // Assert

        // Returned evaluation must match the AI-generated evaluation
        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(8.0);
        assertThat(result.getFeedback())
                .isEqualTo("The answer correctly explains the core concept.");

        // Answer must be persisted
        verify(answerPersistenceService)
                .saveAnswer(any(Answer.class));

        // AI evaluation must be called
        verify(answerEvaluationService)
                .evaluateAnswer(
                        question,
                        request.answerText()
                );

        // Evaluation must be persisted
        verify(answerPersistenceService)
                .saveEvaluation(
                        savedAnswer,
                        evaluation
                );
    }

    @Test
    void submitAnswer_shouldKeepAnswerWhenEvaluationFails() {

        // Arrange
        UUID interviewId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        User user = new User();
        Interview interview = mock(Interview.class);
        InterviewQuestion question = mock(InterviewQuestion.class);

        when(interviewRepository.findByIdAndUser(interviewId, user))
                .thenReturn(Optional.of(interview));

        when(interview.getStatus())
                .thenReturn(InterviewStatus.IN_PROGRESS);

        when(interview.getId())
                .thenReturn(interviewId);

        when(interviewQuestionRepository.findById(questionId))
                .thenReturn(Optional.of(question));

        when(question.getInterview())
                .thenReturn(interview);

        when(answerRepository.findByQuestion(question))
                .thenReturn(Optional.empty());

        SubmitAnswerRequest request =
                new SubmitAnswerRequest(
                        30L,
                        "Polymorphism allows an object to take multiple forms."
                );

        Answer savedAnswer = new Answer();
        savedAnswer.setQuestion(question);
        savedAnswer.setAnswerText(request.answerText());
        savedAnswer.setTimeTaken(request.timeTaken());

        when(answerPersistenceService.saveAnswer(any(Answer.class)))
                .thenReturn(savedAnswer);

        when(answerEvaluationService.evaluateAnswer(
                question,
                request.answerText()
        )).thenThrow(
                new AIResponseParsingException(
                        "Failed to process AI-generated evaluation"
                )
        );

        // Act & Assert
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                interviewService.submitAnswer(
                        interviewId,
                        questionId,
                        request,
                        user
                )
        ).isInstanceOf(AIResponseParsingException.class);

        // Answer was saved before AI evaluation
        verify(answerPersistenceService)
                .saveAnswer(any(Answer.class));

        // Evaluation was attempted
        verify(answerEvaluationService)
                .evaluateAnswer(
                        question,
                        request.answerText()
                );

        // Evaluation must NOT be persisted because AI failed
        verify(answerPersistenceService, never())
                .saveEvaluation(any(Answer.class), any(GeneratedEvaluation.class));
    }

    @Test
    void submitAnswer_existingEvaluatedAnswer_shouldReject() {

        UUID interviewId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        User user = new User();
        Interview interview = mock(Interview.class);
        InterviewQuestion question = mock(InterviewQuestion.class);

        when(interviewRepository.findByIdAndUser(interviewId, user))
                .thenReturn(Optional.of(interview));
        when(interview.getStatus())
                .thenReturn(InterviewStatus.IN_PROGRESS);
        when(interview.getId())
                .thenReturn(interviewId);
        when(interviewQuestionRepository.findById(questionId))
                .thenReturn(Optional.of(question));
        when(question.getInterview())
                .thenReturn(interview);

        Answer existingAnswer = new Answer();
        existingAnswer.setQuestion(question);
        existingAnswer.setAnswerText("Previous answer text.");
        existingAnswer.setTimeTaken(45L);
        existingAnswer.setScore(7.0);
        existingAnswer.setFeedback("Good answer.");

        when(answerRepository.findByQuestion(question))
                .thenReturn(Optional.of(existingAnswer));

        SubmitAnswerRequest request =
                new SubmitAnswerRequest(
                        30L,
                        "New answer text."
                );

        assertThatThrownBy(() ->
                interviewService.submitAnswer(
                        interviewId, questionId, request, user
                )
        ).isInstanceOf(AnswerAlreadySubmittedException.class);

        verify(answerPersistenceService, never())
                .saveAnswer(any(Answer.class));
        verify(answerEvaluationService, never())
                .evaluateAnswer(any(), anyString());
    }

    @Test
    void submitAnswer_existingUnevaluatedAnswer_shouldRetryEvaluation() {

        UUID interviewId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        User user = new User();
        Interview interview = mock(Interview.class);
        InterviewQuestion question = mock(InterviewQuestion.class);

        when(interviewRepository.findByIdAndUser(interviewId, user))
                .thenReturn(Optional.of(interview));
        when(interview.getStatus())
                .thenReturn(InterviewStatus.IN_PROGRESS);
        when(interview.getId())
                .thenReturn(interviewId);
        when(interviewQuestionRepository.findById(questionId))
                .thenReturn(Optional.of(question));
        when(question.getInterview())
                .thenReturn(interview);

        Answer existingAnswer = new Answer();
        existingAnswer.setQuestion(question);
        existingAnswer.setAnswerText("Original answer text.");
        existingAnswer.setTimeTaken(45L);
        // score and feedback are null (unevaluated)

        when(answerRepository.findByQuestion(question))
                .thenReturn(Optional.of(existingAnswer));

        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        "The answer correctly explains the core concept."
                );

        when(answerEvaluationService.evaluateAnswer(
                question, "Original answer text."
        )).thenReturn(evaluation);

        SubmitAnswerRequest request =
                new SubmitAnswerRequest(
                        30L,
                        "This text should be ignored."
                );

        GeneratedEvaluation result = interviewService.submitAnswer(
                interviewId, questionId, request, user
        );

        // Evaluation returned
        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(8.0);
        assertThat(result.getFeedback())
                .isEqualTo("The answer correctly explains the core concept.");

        // No new answer was created
        verify(answerPersistenceService, never())
                .saveAnswer(any(Answer.class));

        // Evaluation was persisted on the existing answer
        verify(answerPersistenceService)
                .saveEvaluation(existingAnswer, evaluation);

        // Original answer data preserved
        assertThat(existingAnswer.getAnswerText())
                .isEqualTo("Original answer text.");
        assertThat(existingAnswer.getTimeTaken())
                .isEqualTo(45L);
    }

    @Test
    void submitAnswer_retryEvaluationFails_shouldNotPersistEvaluation() {

        UUID interviewId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        User user = new User();
        Interview interview = mock(Interview.class);
        InterviewQuestion question = mock(InterviewQuestion.class);

        when(interviewRepository.findByIdAndUser(interviewId, user))
                .thenReturn(Optional.of(interview));
        when(interview.getStatus())
                .thenReturn(InterviewStatus.IN_PROGRESS);
        when(interview.getId())
                .thenReturn(interviewId);
        when(interviewQuestionRepository.findById(questionId))
                .thenReturn(Optional.of(question));
        when(question.getInterview())
                .thenReturn(interview);

        Answer existingAnswer = new Answer();
        existingAnswer.setQuestion(question);
        existingAnswer.setAnswerText("Original answer text.");
        existingAnswer.setTimeTaken(45L);

        when(answerRepository.findByQuestion(question))
                .thenReturn(Optional.of(existingAnswer));

        when(answerEvaluationService.evaluateAnswer(
                question, "Original answer text."
        )).thenThrow(new AIResponseParsingException(
                "Failed to process AI-generated evaluation"
        ));

        SubmitAnswerRequest request =
                new SubmitAnswerRequest(
                        30L,
                        "This text should be ignored."
                );

        assertThatThrownBy(() ->
                interviewService.submitAnswer(
                        interviewId, questionId, request, user
                )
        ).isInstanceOf(AIResponseParsingException.class);

        // No new answer was created
        verify(answerPersistenceService, never())
                .saveAnswer(any(Answer.class));

        // Evaluation was NOT persisted
        verify(answerPersistenceService, never())
                .saveEvaluation(
                        any(Answer.class),
                        any(GeneratedEvaluation.class)
                );

        // Answer remains unevaluated
        assertThat(existingAnswer.getScore()).isNull();
        assertThat(existingAnswer.getFeedback()).isNull();
    }
}