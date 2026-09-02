package com.aiinterviewplatform.backend.exception;

public class InterviewNotInProgressException extends RuntimeException {

    public InterviewNotInProgressException(String message) {
        super(message);
    }
}