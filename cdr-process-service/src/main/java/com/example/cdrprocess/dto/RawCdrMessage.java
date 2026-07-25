package com.example.cdrprocess.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record RawCdrMessage(
        @NotBlank(message = "Event ID must not be blank.")
        String eventId,
        @NotNull(message = "Start time is required.")
        LocalDateTime startTime,
        @NotNull(message = "End time is required.")
        LocalDateTime endTime,
        String imsi,
        String imei,
        Integer cellId,
        Integer lacId,
        @NotBlank(message = "A-party number must not be blank.")
        String aNumber,
        @NotBlank(message = "B-party number must not be blank.")
        String bNumber,
        @NotNull(message = "Setup duration is required.")
        @PositiveOrZero(message = "Setup duration must be zero or greater.")
        Long setupDuration,
        @NotNull(message = "Conversation duration is required.")
        @PositiveOrZero(message = "Conversation duration must be zero or greater.")
        Long conversationDuration,
        @NotBlank(message = "Direction must not be blank.")
        String direction,
        @NotBlank(message = "Result must not be blank.")
        String result
) {

    @AssertTrue(message = "End time must not be before start time.")
    public boolean isTimeRangeValid() {
        return startTime == null || endTime == null || !endTime.isBefore(startTime);
    }
}
