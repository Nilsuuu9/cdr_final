package com.example.cdrreport.service;

import com.example.cdrreport.config.CdrCacheKeys;
import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.mapper.CdrReportMapper;
import com.example.cdrreport.repository.CdrRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CdrQueryService {
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final CdrRepository cdrRepository;
    private final CdrReportMapper cdrReportMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public CdrQueryService(
            CdrRepository cdrRepository,
            CdrReportMapper cdrReportMapper,
            RedisTemplate<String, Object> redisTemplate) {
        this.cdrRepository = cdrRepository;
        this.cdrReportMapper = cdrReportMapper;
        this.redisTemplate = redisTemplate;
    }

    public List<CdrResponse> getAll() {
        return cdrReportMapper.toResponseList(cdrRepository.findAll());
    }

    public List<CdrResponse> getByCallerNumber(String callerNumber) {
        String cacheKey = CdrCacheKeys.byCaller(callerNumber);
        List<CdrResponse> cachedResponses;
        try {
            cachedResponses = readCachedResponses(cacheKey);
        } catch (RuntimeException exception) {
            log.error("Failed to read caller responses from Redis. key={}", cacheKey, exception);
            throw exception;
        }
        if (cachedResponses != null) {
            return cachedResponses;
        }

        List<CdrResponse> responses = cdrReportMapper.toResponseList(
                cdrRepository.findByANumber(callerNumber));
        try {
            redisTemplate.opsForValue().set(cacheKey, responses, CACHE_TTL);
        } catch (RuntimeException exception) {
            log.error("Failed to write caller responses to Redis. key={}", cacheKey, exception);
            throw exception;
        }
        return responses;
    }

    @SuppressWarnings("unchecked")
    private List<CdrResponse> readCachedResponses(String cacheKey) {
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue instanceof List<?> cachedList) {
            return (List<CdrResponse>) cachedList;
        }
        return null;
    }
}
