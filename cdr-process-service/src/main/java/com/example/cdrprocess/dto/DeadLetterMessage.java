package com.example.cdrprocess.dto;

import java.time.Instant;

public record DeadLetterMessage(
        String originalPayload,
        String errorMessage,
        String exceptionType,
        Instant failedAt
) {
}
