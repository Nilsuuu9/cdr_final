package com.example.cdrprocess.service;

import com.example.cdrprocess.dto.RawCdrMessage;
import com.example.cdrprocess.entity.Cdr;
import com.example.cdrprocess.exception.InvalidCdrMessageException;
import com.example.cdrprocess.repository.CdrRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class CdrProcessingService {

    private static final BigDecimal PRICE_PER_SECOND = new BigDecimal("0.05");

    private final CdrRepository cdrRepository;

    public CdrProcessingService(CdrRepository cdrRepository) {
        this.cdrRepository = cdrRepository;
    }

    @Transactional
    public void process(RawCdrMessage message) {
        validate(message);

        if (cdrRepository.existsByEventId(message.eventId())) {
            log.info("Ayni eventId ile gelen tekrar mesaj atlandi. eventId={}", message.eventId());
            return;
        }

        BigDecimal chargeAmount = calculateCharge(message.conversationDuration());
        Cdr cdr = new Cdr(
                message.eventId(), message.startTime(), message.endTime(), message.imsi(), message.imei(),
                message.cellId(), message.lacId(), message.aNumber(), message.bNumber(), message.setupDuration(),
                message.conversationDuration(), message.direction(), message.result(), chargeAmount
        );
        Cdr savedCdr = cdrRepository.save(cdr);
        log.info("CDR Kafka'dan okunup veritabanina yazildi. eventId={}, id={}, chargeAmount={}",
                message.eventId(), savedCdr.getId(), chargeAmount);
    }

    private BigDecimal calculateCharge(Long conversationDuration) {
        return PRICE_PER_SECOND
                .multiply(BigDecimal.valueOf(conversationDuration))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validate(RawCdrMessage message) {
        if (message == null || isBlank(message.eventId()) || message.startTime() == null || message.endTime() == null
                || isBlank(message.aNumber()) || isBlank(message.bNumber()) || message.conversationDuration() == null
                || message.conversationDuration() < 0 || message.setupDuration() == null || message.setupDuration() < 0) {
            throw new InvalidCdrMessageException("CDR mesaji zorunlu alanlari icermiyor veya gecersiz sureye sahip.");
        }

        if (message.endTime().isBefore(message.startTime())) {
            throw new InvalidCdrMessageException("CDR bitis zamani baslangic zamanindan once olamaz.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}


