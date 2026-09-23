package com.aiinterviewplatform.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleAIResponseParsing_returns500WithGenericMessage() {
        ResponseEntity<String> response =
                handler.handleAIResponseParsing(
                        new AIResponseParsingException(
                                "AI generated an incorrect number of questions"
                        )
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .isEqualTo("Failed to process AI response");
    }

    @Test
    void handleAIResponseParsing_doesNotExposeInternalDetails() {
        ResponseEntity<String> response =
                handler.handleAIResponseParsing(
                        new AIResponseParsingException(
                                "Internal parsing error with stack details"
                        )
                );

        assertThat(response.getBody())
                .doesNotContain("Internal parsing error");
    }

    @Test
    void handleAIProvider_returns503() {
        ResponseEntity<String> response =
                handler.handleAIProvider(
                        new AIProviderException(
                                "Gemini connection timeout",
                                new RuntimeException("socket closed")
                        )
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody())
                .isEqualTo("AI service is temporarily unavailable");
    }

    @Test
    void handleAIProvider_doesNotExposeProviderDetails() {
        ResponseEntity<String> response =
                handler.handleAIProvider(
                        new AIProviderException(
                                "API key invalid: sk-abc123",
                                new RuntimeException()
                        )
                );

        assertThat(response.getBody())
                .doesNotContain("API key")
                .doesNotContain("sk-abc123")
                .doesNotContain("Gemini");
    }
}
