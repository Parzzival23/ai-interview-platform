package com.aiinterviewplatform.backend.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedEvaluationValidationTest {

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
    void validEvaluationShouldHaveNoViolations() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        "The answer correctly explains the core concept."
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullScoreShouldHaveViolation() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        null,
                        "The answer is reasonable."
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).anyMatch(
                violation -> violation.toString().contains("score")
        );
    }

    @Test
    void scoreBelowZeroShouldHaveViolation() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        -1.0,
                        "The answer is reasonable."
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).anyMatch(
                violation -> violation.toString().contains("score")
        );
    }

    @Test
    void scoreAboveTenShouldHaveViolation() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        11.0,
                        "The answer is reasonable."
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).anyMatch(
                violation -> violation.toString().contains("score")
        );
    }

    @Test
    void nullFeedbackShouldHaveViolation() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        null
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).anyMatch(
                violation -> violation.toString().contains("feedback")
        );
    }

    @Test
    void blankFeedbackShouldHaveViolation() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        ""
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).anyMatch(
                violation -> violation.toString().contains("feedback")
        );
    }

    @Test
    void whitespaceOnlyFeedbackShouldHaveViolation() {
        GeneratedEvaluation evaluation =
                new GeneratedEvaluation(
                        8.0,
                        "   "
                );

        Set<?> violations = validator.validate(evaluation);

        assertThat(violations).anyMatch(
                violation -> violation.toString().contains("feedback")
        );
    }
}