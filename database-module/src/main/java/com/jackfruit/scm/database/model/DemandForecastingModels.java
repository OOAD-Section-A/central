package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class DemandForecastingModels {

    private DemandForecastingModels() {
    }

    public record SalesRecord(String saleId, String productId, String storeId, LocalDate saleDate,
                              int quantitySold, BigDecimal unitPrice, BigDecimal revenue,
                              String region) {
    }

    public record HolidayCalendar(String holidayId, LocalDate holidayDate, String holidayName,
                                  String holidayType, String regionApplicable) {
    }

    public record PromotionalCalendar(String promoCalendarId, String promoId, String promoName,
                                      LocalDate promoStartDate, LocalDate promoEndDate,
                                      BigDecimal discountPercentage, String promoType,
                                      String applicableProducts) {
    }

    public record ProductLifecycleStage(String lifecycleId, String productId, String currentStage,
                                        LocalDate stageStartDate, String previousStage,
                                        LocalDate transitionDate) {
    }

    public record ProductMetadata(String productId, String productName, String category,
                                  String subCategory, String seasonalityType) {
    }

    public record InventorySupply(String productId, Integer currentStock, Integer reorderPoint,
                                  Integer leadTimeDays, String supplierId) {
    }

    public record ForecastPerformanceMetric(String evalId, String forecastId, LocalDate forecastDate,
                                            int predictedQty, Integer actualQty, BigDecimal mape,
                                            BigDecimal rmse, String modelUsed) {
    }
}
