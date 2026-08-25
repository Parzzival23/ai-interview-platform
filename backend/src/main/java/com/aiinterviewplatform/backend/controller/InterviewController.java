package com.aiinterviewplatform.backend.controller;

import com.aiinterviewplatform.backend.dto.CreateInterviewRequest;
import com.aiinterviewplatform.backend.dto.InterviewQuestionResponse;
import com.aiinterviewplatform.backend.dto.InterviewResponse;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interviews")
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
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return interviewService.startInterview(id, user);
    }
    @GetMapping("/{id}/questions")
    public List<InterviewQuestionResponse> getInterviewQuestions(
            @PathVariable UUID id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return interviewService.getInterviewQuestions(id, user);
    }
}
