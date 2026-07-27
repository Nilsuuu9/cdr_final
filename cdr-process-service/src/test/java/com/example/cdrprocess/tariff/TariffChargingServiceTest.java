package com.example.cdrprocess.tariff;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TariffChargingServiceTest {

    private final TariffChargingService service = new TariffChargingService();

    @Test
    void calculatesGoldCharge() {
        assertThat(service.calculateCharge("Gold", 120)).isEqualByComparingTo(new BigDecimal("3.60"));
    }

    @Test
    void calculatesStandardCharge() {
        assertThat(service.calculateCharge("Standard", 120)).isEqualByComparingTo(new BigDecimal("6.00"));
    }

    @Test
    void calculatesPremiumCharge() {
        assertThat(service.calculateCharge("Premium", 120)).isEqualByComparingTo(new BigDecimal("2.40"));
    }

    @Test
    void usesStandardRateForUnknownTariff() {
        assertThat(service.calculateCharge("Unknown", 120)).isEqualByComparingTo(new BigDecimal("6.00"));
    }
}
