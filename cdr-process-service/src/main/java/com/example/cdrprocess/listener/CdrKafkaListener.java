package com.example.cdrprocess.listener;

import com.example.cdrprocess.dto.RawCdrMessage;
import com.example.cdrprocess.dto.DeadLetterMessage;
import com.example.cdrprocess.service.CdrProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CdrKafkaListener {

    private final ObjectMapper objectMapper;
    private final CdrProcessingService cdrProcessingService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String deadLetterTopic;

    public CdrKafkaListener(
            ObjectMapper objectMapper,
            CdrProcessingService cdrProcessingService,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${cdr.kafka.dlt-topic}") String deadLetterTopic) {
        this.objectMapper = objectMapper;
        this.cdrProcessingService = cdrProcessingService;
        this.kafkaTemplate = kafkaTemplate;
        this.deadLetterTopic = deadLetterTopic;
    }

    @KafkaListener(topics = "${cdr.kafka.topic}")
    public void consume(String payload) {
        try {
            RawCdrMessage message = objectMapper.readValue(payload, RawCdrMessage.class);
            cdrProcessingService.process(message);
        } catch (Exception exception) {
            sendToDeadLetterTopic(payload, exception);
        }
    }

    private void sendToDeadLetterTopic(String payload, Exception exception) {
        DeadLetterMessage deadLetterMessage = new DeadLetterMessage(
                payload,
                exception.getMessage(),
                exception.getClass().getName(),
                Instant.now()
        );

        try {
            String serializedMessage = objectMapper.writeValueAsString(deadLetterMessage);
            kafkaTemplate.send(deadLetterTopic, serializedMessage).get(10, TimeUnit.SECONDS);
            log.warn("Sent invalid CDR message to dead-letter topic. topic={}, reason={}",
                    deadLetterTopic, exception.getMessage());
        } catch (Exception deadLetterException) {
            log.error("Failed to send invalid CDR message to dead-letter topic. topic={}",
                    deadLetterTopic, deadLetterException);
        }
    }
}
