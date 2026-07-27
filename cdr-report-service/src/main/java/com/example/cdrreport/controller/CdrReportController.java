package com.example.cdrreport.controller;

import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.mapper.CdrReportMapper;
import com.example.cdrreport.service.CdrQueryService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/cdrs")
public class CdrReportController {
    private final CdrQueryService cdrQueryService;
    private final CdrReportMapper cdrReportMapper;

    public CdrReportController(CdrQueryService cdrQueryService, CdrReportMapper cdrReportMapper) {
        this.cdrQueryService = cdrQueryService;
        this.cdrReportMapper = cdrReportMapper;
    }

    @GetMapping
    public ResponseEntity<List<CdrResponse>> getAllCdrs() {
        return ResponseEntity.ok(cdrReportMapper.toResponseList(cdrQueryService.getAll()));
    }

    @GetMapping("/by-caller/{phoneNumber}")
    public ResponseEntity<List<CdrResponse>> getByCaller(
            @PathVariable @NotBlank(message = "Phone number must not be blank.") String phoneNumber) {
        return ResponseEntity.ok(cdrReportMapper.toResponseList(cdrQueryService.getByCallerNumber(phoneNumber)));
    }
}
