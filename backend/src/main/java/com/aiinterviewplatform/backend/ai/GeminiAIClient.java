package com.aiinterviewplatform.backend.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiAIClient {

    private final Client client;

    public GeminiAIClient(
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String testStructuredOutput() {

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

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        """
                        You are an expert technical interviewer.

                        Generate exactly 2 technical interview questions about Java
                        at medium difficulty.

                        Do not provide answers.
                        Do not provide explanations.
                        Return only the requested structured response.
                        """,
                        config
                );

        return response.text();
    }
}