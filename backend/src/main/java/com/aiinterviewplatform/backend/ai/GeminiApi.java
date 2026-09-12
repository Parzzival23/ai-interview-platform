package com.aiinterviewplatform.backend.ai;

import com.google.genai.types.GenerateContentConfig;

public interface GeminiApi {

    String generateContent(
            String prompt,
            GenerateContentConfig config
    );
}