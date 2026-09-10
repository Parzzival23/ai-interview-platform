package com.aiinterviewplatform.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GeneratedQuestions {

    @NotNull
    @Size(min = 1)
    private List<@Valid GeneratedQuestion> questions;

    public GeneratedQuestions() {
    }

    public List<GeneratedQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<GeneratedQuestion> questions) {
        this.questions = questions;
    }
}