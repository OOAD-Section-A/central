package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class InventoryModels {

    private InventoryModels() {
    }

    public record StockLevel(String stockLevelId, String productId, int currentStockQty,
                             int reservedStockQty, int availableStockQty, int reorderThreshold,
                             int reorderQuantity, int safetyStockLevel, String zoneAssignment,
                             String stockHealthStatus, LocalDateTime snapshotTimestamp,
                             LocalDateTime lastUpdated) {
    }

    public record Product(String productId, String productName, String sku, String category,
                          String subCategory, String supplierId, String unitOfMeasure, String zone,
                          Integer reorderThreshold, String productImageReference,
                          String storageConditions, Integer shelfLifeDays, LocalDateTime createdAt) {
    }

    public record ProductBatch(String batchId, String productId, String lotId, LocalDate manufacturingDate,
                               String supplierId, String batchStatus, String linkedSku,
                               Integer quantityReceived, LocalDateTime receiptDate,
                               LocalDate expiryDate, String lotStatus, Boolean perishabilityFlag,
                               LocalDateTime receivedDate) {
    }

    public record ExpiryTracking(String expiryId, String batchId, LocalDate expiryDate,
                                 int daysRemaining, String expiryStatus, boolean alertFlag,
                                 String expiryTriggerFlag, String lotStatus) {
    }

    public record StockAdjustment(String adjustmentId, String productId, String batchId,
                                  String adjustmentType, int quantityAdjusted, String reason,
                                  String adjustedBy, LocalDateTime adjustedAt, String skuReference,
                                  String performer, String reasonNote, boolean auditLockFlag,
                                  LocalDateTime adjustmentDate) {
    }

    public record ReorderManagement(String reorderId, String productId, int currentStock,
                                    int reorderThreshold, int reorderQuantity, String supplierId,
                                    String reorderStatus, LocalDateTime lastReorderDate,
                                    String supplierName, Integer suggestedReorderQty,
                                    LocalDateTime orderDate, String orderReference) {
    }

    public record StockReservation(String reservationId, String productId, String orderId,
                                   int reservedQty, String reservationStatus, LocalDateTime reservedAt,
                                   LocalDateTime expiryTime, String linkedSku,
                                   Integer reservedQuantity) {
    }

    public record StockFreeze(String freezeId, String productId, String batchId, boolean freezeStatus,
                              String freezeReason, String frozenBy, LocalDateTime frozenAt,
                              Boolean freezeStatusFlag, String reasonForFreeze,
                              String freezeAppliedBy, LocalDateTime freezeTimestamp) {
    }

    public record DeadStock(String deadStockId, String productId, LocalDateTime lastMovementDate,
                            int stagnantDays, int stagnantQuantity, String actionFlag,
                            String actionStatus) {
    }

    public record StockValuation(String valuationId, String productId, BigDecimal unitCost,
                                 int totalQuantity, BigDecimal totalValue, BigDecimal reservedValue,
                                 String valuationMethod, BigDecimal totalInventoryValue,
                                 BigDecimal reservedStockValue, BigDecimal deadStockValue,
                                 BigDecimal monthlyWriteoffValue, String stockValueByCategory,
                                 String monthlyValuationTrend) {
    }
}
