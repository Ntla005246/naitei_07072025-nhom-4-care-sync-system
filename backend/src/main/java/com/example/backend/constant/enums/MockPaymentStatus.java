package com.example.backend.constant.enums;

public enum MockPaymentStatus {
    SUCCESS("SUCCESS"), FAILED("FAILED");

    private final String value;

    MockPaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
