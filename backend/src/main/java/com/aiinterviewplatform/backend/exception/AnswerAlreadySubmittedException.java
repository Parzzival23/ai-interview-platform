package com.aiinterviewplatform.backend.exception;

public class AnswerAlreadySubmittedException extends RuntimeException {

    public AnswerAlreadySubmittedException(String message) {
        super(message);
    }
}