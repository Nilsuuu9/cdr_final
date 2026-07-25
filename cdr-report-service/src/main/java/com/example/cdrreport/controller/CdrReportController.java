package com.example.cdrreport.controller;

import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.entity.Cdr;
import com.example.cdrreport.service.CdrQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cdrs")
public class CdrReportController {
    private final CdrQueryService cdrQueryService;

    public CdrReportController(CdrQueryService cdrQueryService) {
        this.cdrQueryService = cdrQueryService;
    }

    @GetMapping
    public ResponseEntity<List<CdrResponse>> getAllCdrs() {
        return ResponseEntity.ok(cdrQueryService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/by-caller/{phoneNumber}")
    public ResponseEntity<List<CdrResponse>> getByCaller(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(cdrQueryService.getByCallerNumber(phoneNumber).stream().map(this::toResponse).toList());
    }

    private CdrResponse toResponse(Cdr cdr) {
        return new CdrResponse(cdr.getId(), cdr.getEventId(), cdr.getStartTime(), cdr.getEndTime(),
                cdr.getANumber(), cdr.getBNumber(), cdr.getSetupDuration(), cdr.getConversationDuration(),
                cdr.getDirection(), cdr.getResult(), cdr.getChargeAmount());
    }
}
