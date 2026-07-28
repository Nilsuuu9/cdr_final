package com.example.cdrprocess.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RawCdrMessageValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsMissingRequiredFieldsAndNegativeDurations() {
        RawCdrMessage message = new RawCdrMessage(
                "event-1", LocalDateTime.now(), LocalDateTime.now(), null, null,
                null, null, "", "", -1L, -1L, "", "");

        assertThat(validator.validate(message))
                .extracting(violation -> violation.getMessage())
                .contains(
                        "A-party number must not be blank.",
                        "B-party number must not be blank.",
                        "Conversation duration must be zero or greater.",
                        "Setup duration must be zero or greater.",
                        "Direction must not be blank.",
                        "Result must not be blank.");
    }

    @Test
    void rejectsEndTimeBeforeStartTime() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 28, 10, 0);
        RawCdrMessage message = new RawCdrMessage(
                "event-2", start, start.minusSeconds(1), null, null,
                null, null, "5551112233", "5554445566", 0L, 0L, "MO", "ANSWERED");

        assertThat(validator.validate(message))
                .extracting(violation -> violation.getMessage())
                .contains("End time must not be before start time.");
    }
}
