package com.example.cdrprocess.service;

import com.example.cdrprocess.dto.RawCdrMessage;
import com.example.cdrprocess.entity.Cdr;
import com.example.cdrprocess.exception.InvalidCdrMessageException;
import com.example.cdrprocess.repository.CdrRepository;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CdrProcessingService {

    private static final BigDecimal PRICE_PER_SECOND = new BigDecimal("0.05");

    private final CdrRepository cdrRepository;
    private final Validator validator;

    public CdrProcessingService(CdrRepository cdrRepository, Validator validator) {
        this.cdrRepository = cdrRepository;
        this.validator = validator;
    }

    @Transactional
    public void process(@Valid RawCdrMessage message) {
        validate(message);

        if (cdrRepository.existsByEventId(message.eventId())) {
            log.info("Skipped duplicate message with the same eventId. eventId={}", message.eventId());
            return;
        }

        BigDecimal chargeAmount = calculateCharge(message.conversationDuration());
        Cdr cdr = new Cdr(
                message.eventId(), message.startTime(), message.endTime(), message.imsi(), message.imei(),
                message.cellId(), message.lacId(), message.aNumber(), message.bNumber(), message.setupDuration(),
                message.conversationDuration(), message.direction(), message.result(), chargeAmount
        );
        Cdr savedCdr = cdrRepository.save(cdr);
        log.info("CDR read from Kafka and persisted to the database. eventId={}, id={}, chargeAmount={}",
                message.eventId(), savedCdr.getId(), chargeAmount);
    }

    private BigDecimal calculateCharge(Long conversationDuration) {
        return PRICE_PER_SECOND
                .multiply(BigDecimal.valueOf(conversationDuration))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validate(RawCdrMessage message) {
        if (message == null) {
            throw new InvalidCdrMessageException("The CDR message must not be null.");
        }

        Set<ConstraintViolation<RawCdrMessage>> violations = validator.validate(message);
        if (!violations.isEmpty()) {
            String validationMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(" "));
            throw new InvalidCdrMessageException(validationMessage);
        }
    }
}


