package com.example.cdrprocess.dto;

import java.time.LocalDateTime;

/** Kafka'dan gelen ham CDR JSON sozlesmesi. */
public record RawCdrMessage(
        String eventId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String imsi,
        String imei,
        Integer cellId,
        Integer lacId,
        String aNumber,
        String bNumber,
        Long setupDuration,
        Long conversationDuration,
        String direction,
        String result
) {
}
