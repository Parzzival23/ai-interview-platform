package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.ai.GeminiAIClient;
import com.aiinterviewplatform.backend.dto.GeneratedQuestion;
import com.aiinterviewplatform.backend.dto.GeneratedQuestions;
import com.aiinterviewplatform.backend.entity.Interview;
import com.aiinterviewplatform.backend.entity.InterviewQuestion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class QuestionGenerationServiceImpl
        implements QuestionGenerationService {

    private final GeminiAIClient geminiAIClient;

    public QuestionGenerationServiceImpl(
            GeminiAIClient geminiAIClient
    ) {
        this.geminiAIClient = geminiAIClient;
    }

    @Override
    public List<InterviewQuestion> generateQuestions(
            Interview interview,
            int numberOfQuestions
    ) {

        GeneratedQuestions generatedQuestions =
                geminiAIClient.generateQuestions(
                        interview.getTopic(),
                        interview.getDifficulty(),
                        numberOfQuestions
                );

        List<GeneratedQuestion> questions =
                generatedQuestions.getQuestions();

        return IntStream.range(0, questions.size())
                .mapToObj(index -> {

                    GeneratedQuestion generatedQuestion =
                            questions.get(index);

                    return new InterviewQuestion(
                            null,
                            interview,
                            generatedQuestion.getQuestionText(),
                            index + 1,
                            generatedQuestion.getQuestionType()
                    );
                })
                .toList();
    }
}