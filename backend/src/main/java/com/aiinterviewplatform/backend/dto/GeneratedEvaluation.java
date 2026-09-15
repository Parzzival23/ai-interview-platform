package com.aiinterviewplatform.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GeneratedEvaluation {

    @NotNull
    @Min(0)
    @Max(10)
    private Double score;

    @NotBlank
    private String feedback;

    public GeneratedEvaluation() {
    }

    public GeneratedEvaluation(Double score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}