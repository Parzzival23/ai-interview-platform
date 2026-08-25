package com.aiinterviewplatform.backend.exception;

public class InterviewNotFoundException extends RuntimeException {

    public InterviewNotFoundException(String message) {
        super(message);
    }
}