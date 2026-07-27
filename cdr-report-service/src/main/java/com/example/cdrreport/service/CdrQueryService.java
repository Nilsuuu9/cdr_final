package com.example.cdrreport.service;

import com.example.cdrreport.config.CdrCacheKeys;
import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.mapper.CdrReportMapper;
import com.example.cdrreport.repository.CdrRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional(readOnly = true)
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
        List<CdrResponse> cachedResponses = readCachedResponses(cacheKey);
        if (cachedResponses != null) {
            return cachedResponses;
        }

        List<CdrResponse> responses = cdrReportMapper.toResponseList(
                cdrRepository.findByANumber(callerNumber));
        redisTemplate.opsForValue().set(cacheKey, responses, CACHE_TTL);
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
