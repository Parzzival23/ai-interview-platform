package com.aiinterviewplatform.backend.dto;

import com.aiinterviewplatform.backend.entity.QuestionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedQuestionsValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void validGeneratedQuestions_hasNoViolations() {
        GeneratedQuestion question = new GeneratedQuestion();
        question.setQuestionText("Explain Java interfaces.");
        question.setQuestionType(QuestionType.TECHNICAL);

        GeneratedQuestions questions = new GeneratedQuestions();
        questions.setQuestions(List.of(question));

        Set<ConstraintViolation<GeneratedQuestions>> violations =
                validator.validate(questions);

        assertThat(violations).isEmpty();
    }

    @Test
    void emptyQuestionsList_hasSizeViolation() {
        GeneratedQuestions questions = new GeneratedQuestions();
        questions.setQuestions(List.of());

        Set<ConstraintViolation<GeneratedQuestions>> violations =
                validator.validate(questions);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().equals("questions")
                                && violation.getMessage().contains("size"));
    }

    @Test
    void blankQuestionText_hasNotBlankViolation() {
        GeneratedQuestion question = new GeneratedQuestion();
        question.setQuestionText("");
        question.setQuestionType(QuestionType.TECHNICAL);

        GeneratedQuestions questions = new GeneratedQuestions();
        questions.setQuestions(List.of(question));

        Set<ConstraintViolation<GeneratedQuestions>> violations =
                validator.validate(questions);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().contains("questionText"));
    }

    @Test
    void nullQuestionType_hasNotNullViolation() {
        GeneratedQuestion question = new GeneratedQuestion();
        question.setQuestionText("Explain Java interfaces.");
        question.setQuestionType(null);

        GeneratedQuestions questions = new GeneratedQuestions();
        questions.setQuestions(List.of(question));

        Set<ConstraintViolation<GeneratedQuestions>> violations =
                validator.validate(questions);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().contains("questionType"));
    }

    @Test
    void nullQuestionsList_hasNotNullViolation() {
        GeneratedQuestions questions = new GeneratedQuestions();
        questions.setQuestions(null);

        Set<ConstraintViolation<GeneratedQuestions>> violations =
                validator.validate(questions);

        assertThat(violations)
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().equals("questions"));
    }
}