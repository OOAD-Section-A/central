package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ReturnsModels {

    private ReturnsModels() {
    }

    public record ProductReturn(String returnRequestId, String orderId, String customerId,
                                String productDetails, String defectDetails, String customerFeedback,
                                String transportDetails, LocalDateTime warrantyValidUntil,
                                String returnStatus, LocalDateTime createdAt) {
    }

    public record ReturnGrowthStatistic(String growthStatId, String returnRequestId, String metricPeriod,
                                        BigDecimal returnRate, BigDecimal resolutionRate,
                                        LocalDateTime recordedAt) {
    }
}
