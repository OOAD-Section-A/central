package com.jackfruit.scm.database.util;

import java.math.BigDecimal;
import java.util.Objects;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }

    public static void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public static void requirePositive(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    public static void requireRange(BigDecimal value, BigDecimal min, BigDecimal max, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }
}
