package com.aiinterviewplatform.backend.exception;

public class InterviewNotCompletedException extends RuntimeException {

    public InterviewNotCompletedException(String message) {
        super(message);
    }
}