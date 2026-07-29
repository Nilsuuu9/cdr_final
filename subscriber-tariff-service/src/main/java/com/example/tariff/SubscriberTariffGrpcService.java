package com.example.tariff;

import com.example.tariff.grpc.SubscriberTariffServiceGrpc;
import com.example.tariff.grpc.TariffRequest;
import com.example.tariff.grpc.TariffResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class SubscriberTariffGrpcService extends SubscriberTariffServiceGrpc.SubscriberTariffServiceImplBase {

    static final String DEFAULT_TARIFF = "Standard";

    @Override
    public void getTariff(
            TariffRequest request,
            StreamObserver<TariffResponse> responseObserver) {
        String tariffType = resolveTariff(request.getPhoneNumber());
        responseObserver.onNext(TariffResponse.newBuilder()
                .setTariffType(tariffType)
                .build());
        responseObserver.onCompleted();
    }

    String resolveTariff(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return DEFAULT_TARIFF;
        }

        char lastDigit = phoneNumber.charAt(phoneNumber.length() - 1);
        return switch (lastDigit) {
            case '0', '1', '2' -> "Gold";
            case '3', '4', '5' -> "Premium";
            default -> DEFAULT_TARIFF;
        };
    }
}
