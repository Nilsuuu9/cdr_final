package com.example.cdrreport.service;

import com.example.cdrreport.config.CdrCacheKeys;
import com.example.cdrreport.dto.CdrResponse;
import com.example.cdrreport.entity.Cdr;
import com.example.cdrreport.mapper.CdrReportMapper;
import com.example.cdrreport.repository.CdrRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CdrQueryServiceTest {

    @Test
    void getByCallerNumber_shouldReadFromDatabaseAndWriteToCacheOnCacheMiss() {
        CdrRepository repository = mock(CdrRepository.class);
        CdrReportMapper mapper = mock(CdrReportMapper.class);
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        CdrQueryService service = new CdrQueryService(repository, mapper, redisTemplate);
        String callerNumber = "5551112233";
        String cacheKey = CdrCacheKeys.byCaller(callerNumber);
        List<Cdr> entities = List.of(mock(Cdr.class));
        List<CdrResponse> responses = List.of(mock(CdrResponse.class));

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(cacheKey)).willReturn(null);
        given(repository.findByANumber(callerNumber)).willReturn(entities);
        given(mapper.toResponseList(entities)).willReturn(responses);

        assertThat(service.getByCallerNumber(callerNumber)).isSameAs(responses);

        verify(repository).findByANumber(callerNumber);
        verify(valueOperations).set(cacheKey, responses, Duration.ofMinutes(10));
    }

    @Test
    void getByCallerNumber_shouldReturnCachedResponsesWithoutDatabaseQueryOnCacheHit() {
        CdrRepository repository = mock(CdrRepository.class);
        CdrReportMapper mapper = mock(CdrReportMapper.class);
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        CdrQueryService service = new CdrQueryService(repository, mapper, redisTemplate);
        String cacheKey = CdrCacheKeys.byCaller("5551112233");
        List<CdrResponse> cachedResponses = List.of(mock(CdrResponse.class));

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(cacheKey)).willReturn(cachedResponses);

        assertThat(service.getByCallerNumber("5551112233")).isSameAs(cachedResponses);

        verify(repository, never()).findByANumber("5551112233");
        verify(mapper, never()).toResponseList(org.mockito.ArgumentMatchers.anyList());
    }
}
