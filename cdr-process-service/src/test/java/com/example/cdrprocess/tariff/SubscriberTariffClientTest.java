package com.example.cdrprocess.tariff;

import com.example.tariff.grpc.SubscriberTariffServiceGrpc;
import com.example.tariff.grpc.TariffResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriberTariffClientTest {

    private Server server;
    private SubscriberTariffClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = ServerBuilder.forPort(0)
                .addService(new SubscriberTariffServiceGrpc.SubscriberTariffServiceImplBase() {
                    @Override
                    public void getTariff(
                            com.example.tariff.grpc.TariffRequest request,
                            StreamObserver<TariffResponse> responseObserver) {
                        responseObserver.onNext(TariffResponse.newBuilder()
                                .setTariffType("Gold")
                                .build());
                        responseObserver.onCompleted();
                    }
                })
                .build()
                .start();
        client = new SubscriberTariffClient("localhost", server.getPort());
    }

    @AfterEach
    void tearDown() {
        client.shutdown();
        server.shutdownNow();
    }

    @Test
    void getsTariffFromGrpcServer() {
        assertThat(client.getTariff("5551112230")).isEqualTo("Gold");
    }

    @Test
    void usesStandardWhenGrpcServerIsUnavailable() {
        client.shutdown();

        assertThat(client.getTariff("5551112230")).isEqualTo("Standard");
    }
}
