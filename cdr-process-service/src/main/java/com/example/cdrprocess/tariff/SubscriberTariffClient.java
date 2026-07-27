package com.example.cdrprocess.tariff;

import com.example.tariff.grpc.SubscriberTariffServiceGrpc;
import com.example.tariff.grpc.TariffRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Slf4j
@Component
public class SubscriberTariffClient {

    private static final String DEFAULT_TARIFF = "Standard";

    private final ManagedChannel channel;
    private final SubscriberTariffServiceGrpc.SubscriberTariffServiceBlockingStub stub;

    public SubscriberTariffClient(
            @Value("${grpc.tariff-service.host}") String host,
            @Value("${grpc.tariff-service.port}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = SubscriberTariffServiceGrpc.newBlockingStub(channel);
    }

    public String getTariff(String phoneNumber) {
        try {
            String tariffType = stub.getTariff(TariffRequest.newBuilder()
                    .setPhoneNumber(phoneNumber == null ? "" : phoneNumber)
                    .build())
                    .getTariffType();
            return tariffType == null || tariffType.isBlank() ? DEFAULT_TARIFF : tariffType;
        } catch (StatusRuntimeException exception) {
            log.warn("Tariff service is unavailable. Using the default tariff.", exception);
            return DEFAULT_TARIFF;
        }
    }

    @PreDestroy
    void shutdown() {
        channel.shutdown();
    }
}
