package com.aiinterviewplatform.backend.exception;

public class InterviewAlreadyStartedException extends RuntimeException {

    public InterviewAlreadyStartedException(String message) {
        super(message);
    }
}