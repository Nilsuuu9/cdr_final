package com.example.cdrreport.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CdrResponse(
        Long id, String eventId, LocalDateTime startTime, LocalDateTime endTime,
        String aNumber, String bNumber, Long setupDuration, Long conversationDuration,
        String direction, String result, BigDecimal chargeAmount
) {
}
