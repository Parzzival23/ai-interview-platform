package com.aiinterviewplatform.backend.ai;

import com.aiinterviewplatform.backend.exception.AIProviderException;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiApiImpl implements GeminiApi {

    private final Client client;

    public GeminiApiImpl(
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Override
    public String generateContent(
            String prompt,
            GenerateContentConfig config
    ) {
        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            config
                    );

            return response.text();
        } catch (Exception e) {
            throw new AIProviderException(
                    "Gemini API call failed",
                    e
            );
        }
    }
}