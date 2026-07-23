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

    @KafkaListener(topics = "${cdr.kafka.topic}")  //cdr-raw-topice mesaj gelirse bu metodu çalıştır.
    public void consume(String payload) {
        try {
            RawCdrMessage message = objectMapper.readValue(payload, RawCdrMessage.class); //kafkadan önce ham json metni gelir bu kısım jsonu java nesnesine çevirir
            cdrProcessingService.process(message);   //mesaj service katmanına gider
        } catch (JsonProcessingException | InvalidCdrMessageException exception) { //geçersiz json veya cdr gelirse mesaj loglanır ve atlanır.aynı hatalı mesajın sonsuza kadar tekrar denenmesi engellenir.
            // Gecersiz veri tekrar tekrar denenmez; loglanir ve consumer bir sonraki mesaja gecer.
            log.warn("Gecersiz CDR mesaji atlandi. reason={}, payload={}", exception.getMessage(), payload);
        }
    }
}
