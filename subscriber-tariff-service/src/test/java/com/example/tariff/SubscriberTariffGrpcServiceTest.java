package com.example.tariff;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriberTariffGrpcServiceTest {

    private final SubscriberTariffGrpcService service = new SubscriberTariffGrpcService();

    @Test
    void returnsGoldForNumbersEndingInZeroToTwo() {
        assertThat(service.resolveTariff("5551112230")).isEqualTo("Gold");
    }

    @Test
    void returnsPremiumForNumbersEndingInThreeToFive() {
        assertThat(service.resolveTariff("5551112233")).isEqualTo("Premium");
    }

    @Test
    void returnsStandardForUnknownNumbers() {
        assertThat(service.resolveTariff("5551112239")).isEqualTo("Standard");
    }

    @Test
    void returnsStandardForBlankNumbers() {
        assertThat(service.resolveTariff(" ")).isEqualTo("Standard");
    }
}
