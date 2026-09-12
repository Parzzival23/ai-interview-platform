package com.aiinterviewplatform.backend.ai;

import com.aiinterviewplatform.backend.dto.GeneratedQuestions;
import com.aiinterviewplatform.backend.entity.Difficulty;
import com.aiinterviewplatform.backend.entity.QuestionType;
import com.aiinterviewplatform.backend.exception.AIResponseParsingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.types.GenerateContentConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@Component
public class GeminiAIClient {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final GeminiApi geminiApi;

    public GeminiAIClient(
            ObjectMapper objectMapper,
            Validator validator,
            GeminiApi geminiApi
    ) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.geminiApi = geminiApi;
    }

    public GeneratedQuestions generateQuestions(
            String topic,
            Difficulty difficulty,
            int numberOfQuestions
    ) {

        ImmutableMap<String, Object> schema = ImmutableMap.of(
                "type", "object",
                "properties", ImmutableMap.of(
                        "questions", ImmutableMap.of(
                                "type", "array",
                                "items", ImmutableMap.of(
                                        "type", "object",
                                        "properties", ImmutableMap.of(
                                                "questionText", ImmutableMap.of(
                                                        "type", "string"
                                                ),
                                                "questionType", ImmutableMap.of(
                                                        "type", "string",
                                                        "enum", ImmutableList.of(
                                                                "TECHNICAL",
                                                                "BEHAVIORAL",
                                                                "CODING",
                                                                "SYSTEM_DESIGN"
                                                        )
                                                )
                                        ),
                                        "required", ImmutableList.of(
                                                "questionText",
                                                "questionType"
                                        )
                                )
                        )
                ),
                "required", ImmutableList.of("questions")
        );

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .candidateCount(1)
                        .responseJsonSchema(schema)
                        .build();

        String prompt = """
                You are an expert technical interviewer.

                Generate exactly %d technical interview questions about %s
                at %s difficulty.

                Do not provide answers.
                Do not provide explanations.
                Return only the requested structured response.
                """.formatted(
                numberOfQuestions,
                topic,
                difficulty
        );

        String responseText =
                geminiApi.generateContent(
                        prompt,
                        config
                );

        try {
            GeneratedQuestions generatedQuestions =
                    objectMapper.readValue(
                            responseText,
                            GeneratedQuestions.class
                    );

            // Structural validation
            Set<ConstraintViolation<GeneratedQuestions>> violations =
                    validator.validate(generatedQuestions);

            if (!violations.isEmpty()) {
                throw new AIResponseParsingException(
                        "AI-generated questions failed structural validation"
                );
            }

            // Business validation
            validateBusinessRules(
                    generatedQuestions,
                    numberOfQuestions
            );

            return generatedQuestions;

        } catch (JacksonException e) {
            throw new AIResponseParsingException(
                    "Failed to parse AI-generated questions",
                    e
            );
        }
    }

    private void validateBusinessRules(
            GeneratedQuestions generatedQuestions,
            int expectedQuestionCount
    ) {

        // Rule 1: Exact question count
        if (generatedQuestions.getQuestions().size()
                != expectedQuestionCount) {

            throw new AIResponseParsingException(
                    "AI generated an incorrect number of questions"
            );
        }

        // Rule 2: Every question must be technical
        boolean hasNonTechnicalQuestion =
                generatedQuestions.getQuestions().stream()
                        .anyMatch(question ->
                                question.getQuestionType()
                                        != QuestionType.TECHNICAL
                        );

        if (hasNonTechnicalQuestion) {
            throw new AIResponseParsingException(
                    "AI generated a non-technical question"
            );
        }

        // Rule 3: No duplicate questions
        long uniqueQuestionCount =
                generatedQuestions.getQuestions().stream()
                        .map(question ->
                                question.getQuestionText().trim()
                        )
                        .map(String::toLowerCase)
                        .distinct()
                        .count();

        if (uniqueQuestionCount != expectedQuestionCount) {
            throw new AIResponseParsingException(
                    "AI generated duplicate questions"
            );
        }
    }
}