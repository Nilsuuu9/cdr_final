package com.example.cdrreport.mapper;

import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.entity.Cdr;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CdrReportMapper {

    public CdrResponse toResponse(Cdr cdr) {
        return new CdrResponse(
                cdr.getId(),
                cdr.getEventId(),
                cdr.getStartTime(),
                cdr.getEndTime(),
                cdr.getANumber(),
                cdr.getBNumber(),
                cdr.getSetupDuration(),
                cdr.getConversationDuration(),
                cdr.getDirection(),
                cdr.getResult(),
                cdr.getChargeAmount()
        );
    }

    public List<CdrResponse> toResponseList(List<Cdr> cdrs) {
        return cdrs.stream()
                .map(this::toResponse)
                .toList();
    }
}
