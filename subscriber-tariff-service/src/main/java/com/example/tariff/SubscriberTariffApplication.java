package com.example.tariff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SubscriberTariffApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(SubscriberTariffApplication.class, args);
        try {
            context.getBean(SubscriberTariffGrpcServer.class).awaitTermination();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
