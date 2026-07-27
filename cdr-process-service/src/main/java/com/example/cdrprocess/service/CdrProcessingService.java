package com.example.cdrprocess.service;

import com.example.cdrprocess.dto.RawCdrMessage;
import com.example.cdrprocess.entity.Cdr;
import com.example.cdrprocess.config.CdrCacheKeys;
import com.example.cdrprocess.exception.InvalidCdrMessageException;
import com.example.cdrprocess.repository.CdrRepository;
import com.example.cdrprocess.tariff.SubscriberTariffClient;
import com.example.cdrprocess.tariff.TariffChargingService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CdrProcessingService {

    private final CdrRepository cdrRepository;
    private final Validator validator;
    private final StringRedisTemplate redisTemplate;
    private final SubscriberTariffClient subscriberTariffClient;
    private final TariffChargingService tariffChargingService;

    public CdrProcessingService(
            CdrRepository cdrRepository,
            Validator validator,
            StringRedisTemplate redisTemplate,
            SubscriberTariffClient subscriberTariffClient,
            TariffChargingService tariffChargingService) {
        this.cdrRepository = cdrRepository;
        this.validator = validator;
        this.redisTemplate = redisTemplate;
        this.subscriberTariffClient = subscriberTariffClient;
        this.tariffChargingService = tariffChargingService;
    }

    @Transactional
    public void process(@Valid RawCdrMessage message) {
        validate(message);

        if (cdrRepository.existsByEventId(message.eventId())) {
            log.info("Skipped duplicate message with the same eventId. eventId={}", message.eventId());
            return;
        }

        String tariffType = subscriberTariffClient.getTariff(message.aNumber());
        BigDecimal chargeAmount = tariffChargingService.calculateCharge(
                tariffType, message.conversationDuration());
        Cdr cdr = new Cdr(
                message.eventId(), message.startTime(), message.endTime(), message.imsi(), message.imei(),
                message.cellId(), message.lacId(), message.aNumber(), message.bNumber(), message.setupDuration(),
                message.conversationDuration(), message.direction(), message.result(), chargeAmount
        );
        Cdr savedCdr = cdrRepository.save(cdr);
        invalidateCallerCache(message.aNumber());
        log.info("CDR read from Kafka and persisted to the database. eventId={}, id={}, tariffType={}, chargeAmount={}",
                message.eventId(), savedCdr.getId(), tariffType, chargeAmount);
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

    private void invalidateCallerCache(String callerNumber) {
        String cacheKey = CdrCacheKeys.byCaller(callerNumber);
        try {
            boolean deleted = redisTemplate.delete(cacheKey);
            log.info("Invalidated caller cache. key={}, deleted={}", cacheKey, deleted);
        } catch (DataAccessException exception) {
            log.warn("Could not invalidate caller cache. key={}", cacheKey, exception);
        }
    }
}


