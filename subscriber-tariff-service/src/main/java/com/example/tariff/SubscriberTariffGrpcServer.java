package com.example.tariff;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SubscriberTariffGrpcServer {

    private final Server server;

    public SubscriberTariffGrpcServer(
            SubscriberTariffGrpcService tariffService,
            @Value("${grpc.server.port:9090}") int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(tariffService)
                .build();
    }

    @PostConstruct
    public void start() throws IOException {
        server.start();
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    @PreDestroy
    public void stop() {
        server.shutdown();
    }
}
