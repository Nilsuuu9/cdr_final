package com.example.cdrprocess.listener;

import com.example.cdrprocess.dto.RawCdrMessage;
import com.example.cdrprocess.exception.InvalidCdrMessageException;
import com.example.cdrprocess.service.CdrProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CdrKafkaListener {

    private final ObjectMapper objectMapper;
    private final CdrProcessingService cdrProcessingService;

    public CdrKafkaListener(ObjectMapper objectMapper, CdrProcessingService cdrProcessingService) {
        this.objectMapper = objectMapper;
        this.cdrProcessingService = cdrProcessingService;
    }

    @KafkaListener(topics = "${cdr.kafka.topic}")
    public void consume(String payload) {
        try {
            RawCdrMessage message = objectMapper.readValue(payload, RawCdrMessage.class);
            cdrProcessingService.process(message);
        } catch (JsonProcessingException | InvalidCdrMessageException exception) {
            // Skip invalid messages so one malformed payload does not block the consumer.
            log.warn("Skipped invalid CDR message. reason={}, payload={}", exception.getMessage(), payload);
        }
    }
}
