package com.example.cdrprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka  //@KafkaListener içeren sınıfları bul, Kafka consumer olarak başlat ve çalışır durumda tut.Bu anotasyon olmazsa Java uygulaması açılır ama Kafka listener çalışmaz.
@SpringBootApplication
public class CdrProcessApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdrProcessApplication.class, args);
    }
}



// springboot başlama uygulamasıdır.
