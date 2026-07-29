package com.example.cdrprocess.tariff;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class TariffChargingService {

    private static final String DEFAULT_TARIFF = "Standard";
    private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.05");
    private static final Map<String, BigDecimal> RATES = Map.of(
            "Gold", new BigDecimal("0.03"),
            "Standard", DEFAULT_RATE,
            "Premium", new BigDecimal("0.02")
    );

    public BigDecimal calculateCharge(String tariffType, long conversationDuration) {
        BigDecimal rate = RATES.getOrDefault(tariffType, RATES.get(DEFAULT_TARIFF));
        return rate.multiply(BigDecimal.valueOf(conversationDuration))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
