package com.example.cdrprocess.config;

public final class CdrCacheKeys {

    public static final String BY_CALLER_PREFIX = "cdrs:by-caller:";

    private CdrCacheKeys() {
    }

    public static String byCaller(String phoneNumber) {
        return BY_CALLER_PREFIX + phoneNumber;
    }
}
