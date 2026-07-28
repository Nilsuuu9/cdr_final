package com.example.tariff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class SubscriberTariffApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(SubscriberTariffApplication.class, args);
        try {
            context.getBean(SubscriberTariffGrpcServer.class).awaitTermination();
            new CountDownLatch(1).await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
