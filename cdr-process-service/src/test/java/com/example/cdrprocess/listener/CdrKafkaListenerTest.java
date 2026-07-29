package com.example.cdrprocess.listener;

import com.example.cdrprocess.service.CdrProcessingService;
import com.example.cdrprocess.dto.RawCdrMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class CdrKafkaListenerTest {

    @Test
    void consume_shouldSendInvalidJsonToDeadLetterTopic() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CdrProcessingService processingService = mock(CdrProcessingService.class);
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        given(kafkaTemplate.send(eq("cdr-raw-topic-dlt"), anyString()))
                .willReturn(CompletableFuture.completedFuture(null));
        CdrKafkaListener listener = new CdrKafkaListener(
                objectMapper,
                processingService,
                kafkaTemplate,
                "cdr-raw-topic-dlt"
        );

        listener.consume("{ invalid-json }");

        var payloadCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("cdr-raw-topic-dlt"), payloadCaptor.capture());
        JsonNode deadLetterMessage = objectMapper.readTree(payloadCaptor.getValue());
        assertThat(deadLetterMessage.get("originalPayload").asText()).isEqualTo("{ invalid-json }");
        assertThat(deadLetterMessage.get("errorMessage").asText()).isNotBlank();
        assertThat(deadLetterMessage.get("exceptionType").asText()).contains("Json");
        assertThat(deadLetterMessage.get("failedAt").asText()).isNotBlank();
    }

    @Test
    void consume_shouldSendProcessingFailureToDeadLetterTopic() throws Exception {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        CdrProcessingService processingService = mock(CdrProcessingService.class);
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        RawCdrMessage message = new RawCdrMessage(
                "event-1", java.time.LocalDateTime.now(), java.time.LocalDateTime.now(),
                null, null, null, null, "5551112233", "5554445566", 0L, 1L, "MO", "ANSWERED");
        given(objectMapper.readValue("valid-json", RawCdrMessage.class)).willReturn(message);
        given(objectMapper.writeValueAsString(any())).willReturn("{\"deadLetter\":true}");
        given(kafkaTemplate.send(eq("cdr-raw-topic-dlt"), anyString()))
                .willReturn(CompletableFuture.completedFuture(null));
        org.mockito.Mockito.doThrow(new IllegalStateException("Processing failed"))
                .when(processingService).process(message);

        CdrKafkaListener listener = new CdrKafkaListener(
                objectMapper, processingService, kafkaTemplate, "cdr-raw-topic-dlt");

        listener.consume("valid-json");

        verify(kafkaTemplate).send(eq("cdr-raw-topic-dlt"), anyString());
    }

    @Test
    void consume_shouldProcessValidMessageWithoutSendingToDeadLetterTopic() throws Exception {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        CdrProcessingService processingService = mock(CdrProcessingService.class);
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        RawCdrMessage message = new RawCdrMessage(
                "event-2", java.time.LocalDateTime.now(), java.time.LocalDateTime.now(),
                null, null, null, null, "5551112233", "5554445566", 0L, 1L, "MO", "ANSWERED");
        given(objectMapper.readValue("valid-json", RawCdrMessage.class)).willReturn(message);

        CdrKafkaListener listener = new CdrKafkaListener(
                objectMapper, processingService, kafkaTemplate, "cdr-raw-topic-dlt");

        listener.consume("valid-json");

        verify(processingService).process(message);
        verifyNoInteractions(kafkaTemplate);
    }
}
