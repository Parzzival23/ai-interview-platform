package com.aiinterviewplatform.backend.controller;

import com.aiinterviewplatform.backend.dto.*;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.service.InterviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interviews")
@Validated
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewResponse createInterview(
            @Valid @RequestBody CreateInterviewRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return interviewService.createInterview(request, user);
    }

    @GetMapping("/{id}")
    public InterviewResponse getInterview(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return interviewService.getInterview(id, user);
    }

    @PostMapping("/{id}/start")
    public InterviewResponse startInterview(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return interviewService.startInterview(id, user);
    }

    @GetMapping("/{id}/questions")
    public List<InterviewQuestionResponse> getInterviewQuestions(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return interviewService.getInterviewQuestions(id, user);
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answer")
    public AnswerEvaluationResponse submitAnswer(
            @PathVariable UUID interviewId,
            @PathVariable UUID questionId,
            @Valid @RequestBody SubmitAnswerRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        GeneratedEvaluation evaluation = interviewService.submitAnswer(
                interviewId,
                questionId,
                request,
                user
        );

        return new AnswerEvaluationResponse(
                evaluation.getScore(),
                evaluation.getFeedback()
        );
    }

    @PostMapping("/{id}/complete")
    public InterviewResponse completeInterview(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return interviewService.completeInterview(id, user);
    }

    @GetMapping("/{id}/result")
    public InterviewResultResponse getInterviewResult(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return interviewService.getInterviewResult(id, user);
    }

    @GetMapping
    public InterviewHistoryPageResponse getInterviewHistory(
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size,

            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        Pageable pageable = PageRequest.of(page, size);

        return interviewService.getInterviewHistory(user, pageable);
    }
}