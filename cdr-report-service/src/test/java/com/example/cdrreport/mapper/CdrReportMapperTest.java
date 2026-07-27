package com.example.cdrreport.mapper;

import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.entity.Cdr;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CdrReportMapperTest {

    private final CdrReportMapper mapper = new CdrReportMapper();

    @Test
    void toResponse_shouldMapAllCdrFields() {
        Cdr cdr = mock(Cdr.class);
        LocalDateTime startTime = LocalDateTime.of(2026, 7, 27, 10, 0);
        LocalDateTime endTime = startTime.plusMinutes(2);

        given(cdr.getId()).willReturn(1L);
        given(cdr.getEventId()).willReturn("event-1");
        given(cdr.getStartTime()).willReturn(startTime);
        given(cdr.getEndTime()).willReturn(endTime);
        given(cdr.getANumber()).willReturn("5551112233");
        given(cdr.getBNumber()).willReturn("5554445566");
        given(cdr.getSetupDuration()).willReturn(3L);
        given(cdr.getConversationDuration()).willReturn(120L);
        given(cdr.getDirection()).willReturn("MO");
        given(cdr.getResult()).willReturn("ANSWERED");
        given(cdr.getChargeAmount()).willReturn(new BigDecimal("6.00"));

        CdrResponse response = mapper.toResponse(cdr);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.eventId()).isEqualTo("event-1");
        assertThat(response.startTime()).isEqualTo(startTime);
        assertThat(response.endTime()).isEqualTo(endTime);
        assertThat(response.aNumber()).isEqualTo("5551112233");
        assertThat(response.bNumber()).isEqualTo("5554445566");
        assertThat(response.setupDuration()).isEqualTo(3L);
        assertThat(response.conversationDuration()).isEqualTo(120L);
        assertThat(response.direction()).isEqualTo("MO");
        assertThat(response.result()).isEqualTo("ANSWERED");
        assertThat(response.chargeAmount()).isEqualByComparingTo("6.00");
    }
}
