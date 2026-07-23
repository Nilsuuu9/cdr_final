package com.example.cdrprocess.service;

import com.example.cdrprocess.dto.RawCdrMessage;
import com.example.cdrprocess.entity.Cdr;
import com.example.cdrprocess.repository.CdrRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CdrProcessingServiceTest {
    @Test
    void calculatesChargeAndSavesNewMessage() {
        CdrRepository repository = mock(CdrRepository.class); //bu test gerçek mysql veya kafka çalıştırmaz.sahte repository oluşturur
        when(repository.existsByEventId("event-1")).thenReturn(false);
        when(repository.save(any(Cdr.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CdrProcessingService service = new CdrProcessingService(repository);

        LocalDateTime start = LocalDateTime.of(2026, 7, 22, 10, 0);
        RawCdrMessage message = new RawCdrMessage("event-1", start, start.plusSeconds(120), "286011234567890",
                "12345678901234", 1, 1, "5551112233", "5554445566", 3L, 120L, "MO", "ANSWERED");
        service.process(message);

        ArgumentCaptor<Cdr> cdrCaptor = ArgumentCaptor.forClass(Cdr.class);
        verify(repository).save(cdrCaptor.capture());
        assertThat(cdrCaptor.getValue().getChargeAmount()).isEqualByComparingTo(new BigDecimal("6.00"));
    }
}
