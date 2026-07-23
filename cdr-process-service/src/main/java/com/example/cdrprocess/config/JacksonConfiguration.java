package com.example.cdrprocess.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    // Web servisi olmadigi icin ObjectMapper bean'i burada acikca tanimlanir.
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
 //Bu dosya JSON çevirici nesnesini Spring’e tanımlar.
//JavaTimeModule, JSON’daki tarih metnini şu Java türüne çevirebilmek için gerekir:LocalDateTime