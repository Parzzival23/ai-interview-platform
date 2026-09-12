package com.aiinterviewplatform.backend.ai;

import com.aiinterviewplatform.backend.dto.GeneratedQuestions;
import com.aiinterviewplatform.backend.entity.Difficulty;
import com.aiinterviewplatform.backend.entity.QuestionType;
import com.google.genai.types.GenerateContentConfig;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;
import com.aiinterviewplatform.backend.exception.AIResponseParsingException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GeminiAIClientTest {

    @Mock
    private GeminiApi geminiApi;

    private ObjectMapper objectMapper;
    private Validator validator;
    private GeminiAIClient geminiAIClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();

        geminiAIClient = new GeminiAIClient(
                objectMapper,
                validator,
                geminiApi
        );
    }

    @Test
    void validResponse_returnsGeneratedQuestions() {

        String responseJson = """
                {
                  "questions": [
                    {
                      "questionText": "What is the difference between HashMap and ConcurrentHashMap?",
                      "questionType": "TECHNICAL"
                    },
                    {
                      "questionText": "Explain the Java Memory Model.",
                      "questionType": "TECHNICAL"
                    }
                  ]
                }
                """;

        // Tell the mock what to return instead of calling Gemini.
        org.mockito.Mockito.when(
                geminiApi.generateContent(
                        anyString(),
                        any(GenerateContentConfig.class)
                )
        ).thenReturn(responseJson);

        GeneratedQuestions result =
                geminiAIClient.generateQuestions(
                        "Java",
                        Difficulty.MEDIUM,
                        2
                );

        assertThat(result).isNotNull();
        assertThat(result.getQuestions()).hasSize(2);

        assertThat(result.getQuestions().get(0).getQuestionText())
                .isNotBlank();

        assertThat(result.getQuestions().get(1).getQuestionText())
                .isNotBlank();

        assertThat(result.getQuestions().get(0).getQuestionType())
                .isEqualTo(QuestionType.TECHNICAL);

        assertThat(result.getQuestions().get(1).getQuestionType())
                .isEqualTo(QuestionType.TECHNICAL);

        verify(geminiApi).generateContent(
                anyString(),
                any(GenerateContentConfig.class)
        );
    }
    @Test
    void wrongQuestionCount_throwsException() {

        String responseJson = """
            {
              "questions": [
                {
                  "questionText": "What is the difference between HashMap and ConcurrentHashMap?",
                  "questionType": "TECHNICAL"
                }
              ]
            }
            """;

        org.mockito.Mockito.when(
                geminiApi.generateContent(
                        anyString(),
                        any(GenerateContentConfig.class)
                )
        ).thenReturn(responseJson);

        assertThatThrownBy(() ->
                geminiAIClient.generateQuestions(
                        "Java",
                        Difficulty.MEDIUM,
                        2
                )
        ).isInstanceOf(AIResponseParsingException.class);
    }
    @Test
    void nonTechnicalQuestion_throwsException() {

        String responseJson = """
            {
              "questions": [
                {
                  "questionText": "Tell me about a time you solved a difficult problem.",
                  "questionType": "BEHAVIORAL"
                },
                {
                  "questionText": "Explain the Java Memory Model.",
                  "questionType": "TECHNICAL"
                }
              ]
            }
            """;

        org.mockito.Mockito.when(
                geminiApi.generateContent(
                        anyString(),
                        any(GenerateContentConfig.class)
                )
        ).thenReturn(responseJson);

        assertThatThrownBy(() ->
                geminiAIClient.generateQuestions(
                        "Java",
                        Difficulty.MEDIUM,
                        2
                )
        ).isInstanceOf(AIResponseParsingException.class);
    }
    @Test
    void duplicateQuestions_throwsException() {

        String responseJson = """
            {
              "questions": [
                {
                  "questionText": "Explain the Java Memory Model.",
                  "questionType": "TECHNICAL"
                },
                {
                  "questionText": "Explain the Java Memory Model.",
                  "questionType": "TECHNICAL"
                }
              ]
            }
            """;

        org.mockito.Mockito.when(
                geminiApi.generateContent(
                        anyString(),
                        any(GenerateContentConfig.class)
                )
        ).thenReturn(responseJson);

        assertThatThrownBy(() ->
                geminiAIClient.generateQuestions(
                        "Java",
                        Difficulty.MEDIUM,
                        2
                )
        ).isInstanceOf(AIResponseParsingException.class);
    }
    @Test
    void malformedJson_throwsException() {

        String responseJson = """
            {
              "questions": [
                {
                  "questionText": "What is Java?",
                  "questionType": "TECHNICAL"
                }
            """;

        org.mockito.Mockito.when(
                geminiApi.generateContent(
                        anyString(),
                        any(GenerateContentConfig.class)
                )
        ).thenReturn(responseJson);

        assertThatThrownBy(() ->
                geminiAIClient.generateQuestions(
                        "Java",
                        Difficulty.MEDIUM,
                        2
                )
        ).isInstanceOf(AIResponseParsingException.class);
    }
}