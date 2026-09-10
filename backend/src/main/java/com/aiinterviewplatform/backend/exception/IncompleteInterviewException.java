package com.aiinterviewplatform.backend.exception;

public class IncompleteInterviewException extends RuntimeException {

    public IncompleteInterviewException(String message) {
        super(message);
    }
}