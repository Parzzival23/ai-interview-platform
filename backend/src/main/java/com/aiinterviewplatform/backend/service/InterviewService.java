package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.*;
import com.aiinterviewplatform.backend.entity.*;
import com.aiinterviewplatform.backend.exception.*;
import com.aiinterviewplatform.backend.repository.AnswerRepository;
import com.aiinterviewplatform.backend.repository.InterviewQuestionRepository;
import com.aiinterviewplatform.backend.repository.InterviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final AnswerRepository answerRepository;

    public InterviewService(
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            AnswerRepository answerRepository) {

        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.answerRepository = answerRepository;
    }

    public InterviewResponse createInterview(
            CreateInterviewRequest request,
            User user
    ) {

        Interview interview = new Interview(
                user,
                request.topic(),
                request.difficulty()
        );

        Interview savedInterview = interviewRepository.save(interview);

        return new InterviewResponse(
                savedInterview.getId(),
                savedInterview.getTopic(),
                savedInterview.getDifficulty(),
                savedInterview.getStatus(),
                savedInterview.getCreatedAt(),
                savedInterview.getStartedAt(),
                savedInterview.getCompletedAt()
        );
    }

    public InterviewResponse getInterview(UUID interviewId, User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        return new InterviewResponse(
                interview.getId(),
                interview.getTopic(),
                interview.getDifficulty(),
                interview.getStatus(),
                interview.getCreatedAt(),
                interview.getStartedAt(),
                interview.getCompletedAt()
        );
    }

    public InterviewResponse startInterview(UUID interviewId, User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        if (interview.getStatus() != InterviewStatus.NOT_STARTED) {
            throw new InterviewAlreadyStartedException(
                    "Interview has already been started"
            );
        }

        interview.start();

        Interview savedInterview = interviewRepository.save(interview);

        return new InterviewResponse(
                savedInterview.getId(),
                savedInterview.getTopic(),
                savedInterview.getDifficulty(),
                savedInterview.getStatus(),
                savedInterview.getCreatedAt(),
                savedInterview.getStartedAt(),
                savedInterview.getCompletedAt()
        );
    }

    public List<InterviewQuestionResponse> getInterviewQuestions(
            UUID interviewId,
            User user) {

        interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        List<InterviewQuestion> questions =
                interviewQuestionRepository
                        .findByInterviewIdOrderByQuestionOrder(interviewId);

        return questions.stream()
                .map(question -> new InterviewQuestionResponse(
                        question.getId(),
                        question.getQuestionText(),
                        question.getQuestionOrder(),
                        question.getQuestionType()
                ))
                .toList();
    }

    public void submitAnswer(
            UUID interviewId,
            UUID questionId,
            SubmitAnswerRequest request,
            User user) {

        // 1. Verify that the interview belongs to the current user
        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        // 2. Interview must be in progress
        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new IllegalStateException("Interview is not in progress");
        }

        // 3. Find the question
        InterviewQuestion question = interviewQuestionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException("Question not found"));

        // 4. Make sure the question belongs to this interview
        if (!question.getInterview().getId().equals(interview.getId())) {
            throw new InterviewNotFoundException("Question not found");
        }

        // 5. Prevent answering the same question twice
        if (answerRepository.findByQuestion(question).isPresent()) {
            throw new AnswerAlreadySubmittedException(
                    "Question already answered"
            );
        }

        // 6. Create and save the answer
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswerText(request.answerText());
        answer.setTimeTaken(request.timeTaken());

        answerRepository.save(answer);
    }

    @Transactional
    public void evaluateAnswer(
            UUID interviewId,
            UUID questionId,
            EvaluateAnswerRequest request,
            User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        InterviewQuestion question = interviewQuestionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new QuestionNotFoundException("Question not found"));

        if (!question.getInterview().getId().equals(interview.getId())) {
            throw new InterviewNotFoundException("Question not found");
        }

        Answer answer = answerRepository
                .findByQuestion(question)
                .orElseThrow(() ->
                        new AnswerNotFoundException("Answer not found"));

        answer.setScore(request.score());
        answer.setFeedback(request.feedback());

        answerRepository.save(answer);
    }

    public InterviewResponse completeInterview(UUID interviewId, User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new InterviewNotInProgressException(
                    "Interview is not in progress"
            );
        }

        interview.complete();

        Interview savedInterview = interviewRepository.save(interview);

        return new InterviewResponse(
                savedInterview.getId(),
                savedInterview.getTopic(),
                savedInterview.getDifficulty(),
                savedInterview.getStatus(),
                savedInterview.getCreatedAt(),
                savedInterview.getStartedAt(),
                savedInterview.getCompletedAt()
        );
    }

    public InterviewResultResponse getInterviewResult(
            UUID interviewId,
            User user) {

        Interview interview = interviewRepository
                .findByIdAndUser(interviewId, user)
                .orElseThrow(() ->
                        new InterviewNotFoundException("Interview not found"));

        if (interview.getStatus() != InterviewStatus.COMPLETED) {
            throw new InterviewNotCompletedException(
                    "Interview has not been completed"
            );
        }

        List<InterviewQuestion> questions =
                interviewQuestionRepository
                        .findByInterviewIdOrderByQuestionOrder(interviewId);

        List<InterviewResultQuestionResponse> questionResults =
                questions.stream()
                        .map(question -> {

                            Answer answer = answerRepository
                                    .findByQuestion(question)
                                    .orElse(null);

                            return new InterviewResultQuestionResponse(
                                    question.getId(),
                                    question.getQuestionText(),
                                    answer != null
                                            ? answer.getAnswerText()
                                            : null,
                                    answer != null
                                            ? answer.getTimeTaken()
                                            : null,
                                    answer != null
                                            ? answer.getScore()
                                            : null,
                                    answer != null
                                            ? answer.getFeedback()
                                            : null
                            );
                        })
                        .toList();

        int totalQuestions = questions.size();

        int answeredQuestions = (int) questionResults.stream()
                .filter(result -> result.answerText() != null)
                .count();

        double totalScore = questionResults.stream()
                .filter(result -> result.score() != null)
                .mapToDouble(InterviewResultQuestionResponse::score)
                .sum();

        long evaluatedQuestions = questionResults.stream()
                .filter(result -> result.score() != null)
                .count();

        double averageScore = evaluatedQuestions > 0
                ? totalScore / evaluatedQuestions
                : 0.0;

        return new InterviewResultResponse(
                interview.getId(),
                interview.getTopic(),
                interview.getDifficulty(),
                interview.getStatus(),
                totalQuestions,
                answeredQuestions,
                totalScore,
                averageScore,
                questionResults
        );
    }

    public InterviewHistoryPageResponse getInterviewHistory(
            User user,
            Pageable pageable) {

        Page<Interview> interviewPage =
                interviewRepository.findByUserOrderByCreatedAtDesc(
                        user,
                        pageable
                );

        List<InterviewHistoryResponse> interviews =
                interviewPage.getContent()
                        .stream()
                        .map(interview -> new InterviewHistoryResponse(
                                interview.getId(),
                                interview.getTopic(),
                                interview.getDifficulty(),
                                interview.getStatus(),
                                interview.getCreatedAt(),
                                interview.getStartedAt(),
                                interview.getCompletedAt()
                        ))
                        .toList();

        return new InterviewHistoryPageResponse(
                interviews,
                interviewPage.getNumber(),
                interviewPage.getSize(),
                interviewPage.getTotalElements(),
                interviewPage.getTotalPages()
        );
    }
}