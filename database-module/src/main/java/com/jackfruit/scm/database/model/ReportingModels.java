package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ReportingModels {

    private ReportingModels() {
    }

    public record InventoryStockReportRow(String productId, String binId, String zoneId,
                                          String warehouseId, int currentStockLevel,
                                          LocalDateTime lastUpdated) {
    }

    public record PriceDiscountReportRow(String productId, BigDecimal productPrice, String regionCode,
                                         String channel, String currencyCode, String status) {
    }

    public record ExceptionReportRow(String exceptionId, String exceptionType, String severityLevel,
                                     LocalDateTime timestamp, String requestedBy,
                                     String justificationText) {
    }

    public record CustomerTierCacheRow(String customerId, String tier, LocalDateTime evaluatedAt) {
    }

    public record DashboardReportRow(String orderId, LocalDateTime orderDate, LocalDateTime deliveryDate,
                                     String orderStatus, Double fulfillmentTime, Integer orderQuantity,
                                     String productId, String productName, Integer currentStockLevel,
                                     Integer reorderLevel, Boolean stockOutFlag,
                                     Double inventoryTurnoverRate, String supplierId, String supplierName,
                                     Double supplierPerformanceScore, Double leadTime,
                                     Double onTimeSupplyRate, String shipmentId,
                                     LocalDateTime dispatchDate, String deliveryStatus,
                                     Double transitTime, Boolean delayFlag, String deliveryLocation,
                                     String warehouseId, Integer storageCapacity,
                                     Double utilizationRate, Integer inboundQuantity,
                                     Integer outboundQuantity, BigDecimal productPrice,
                                     BigDecimal discountApplied, Integer salesVolume,
                                     BigDecimal revenue, Integer demandForecast,
                                     String forecastPeriod, Integer predictedInventoryNeeds,
                                     String exceptionId, String exceptionType,
                                     String severityLevel, LocalDateTime timestamp) {
    }
}
