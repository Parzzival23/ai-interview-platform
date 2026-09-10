package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GeneratedQuestion {

    @NotBlank
    private String questionText;

    @NotNull
    private QuestionType questionType;

    public GeneratedQuestion() {
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
}