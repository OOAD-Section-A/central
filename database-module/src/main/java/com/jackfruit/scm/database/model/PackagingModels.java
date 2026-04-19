package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PackagingModels {

    private PackagingModels() {
    }

    public record PackagingJob(String packageId, String orderId, int quantity, BigDecimal totalAmount,
                               BigDecimal discounts, String packagingStatus, String packedBy,
                               LocalDateTime createdAt) {
    }

    public record RepairRequest(String requestId, String orderId, String productId, String defectDetails,
                                String requestStatus, LocalDateTime requestedAt) {
    }

    public record ReceiptRecord(String receiptRecordId, String orderId, String packageId,
                                BigDecimal receivedAmount, String receiptStatus,
                                LocalDateTime recordedAt) {
    }

    public record ContractSkuPrice(Long id, String contractId, String skuId, BigDecimal negotiatedPrice) {
    }

    public record PackagingDiscountPolicy(String policyId, String policyName, String stackingRule,
                                          int priorityLevel, BigDecimal maxDiscountCapPct,
                                          int perishabilityDays, BigDecimal clearanceDiscountPct,
                                          boolean active) {
    }

    public record BundlePromotion(String promoId, String promoName, BigDecimal discountPct,
                                  LocalDate startDate, LocalDate endDate, boolean expired) {
    }

    public record PackagingPromotion(String promoId, String promoName, String couponCode,
                                     String discountType, BigDecimal discountValue,
                                     LocalDateTime startDate, LocalDateTime endDate,
                                     String eligibleSkuIds, BigDecimal minCartValue,
                                     int maxUses, int currentUseCount, boolean expired) {
    }

    public record PromotionEligibleSku(Long id, String promoId, String skuId) {
    }
}
