package com.example.cdrprocess.listener;

import com.example.cdrprocess.service.CdrProcessingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CdrKafkaListenerTest {

    @Test
    void consume_shouldSendInvalidJsonToDeadLetterTopic() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
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
}
