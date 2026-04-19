package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public final class UiModels {

    private UiModels() {
    }

    public record UiUser(Integer userId, String username, String passwordHash, Integer twoFactorToken,
                         String jwtSessionToken, String userRole, String authorizedMenuItems,
                         long sessionExpiryTime, int loginAttemptCount, boolean accountLocked,
                         String redirectPanelUrl, LocalDateTime lastLoginTimestamp,
                         String userEmail, String displayName, String themePreference,
                         String languagePreference, String sessionStatus,
                         String notificationPreferences, String systemConfigValues,
                         LocalDateTime createdAt) {
    }

    public record UiSession(Integer sessionId, Integer userId, String jwtSessionToken,
                            String redirectPanelUrl, long sessionExpiryTime,
                            String sessionStatus, LocalDateTime createdAt) {
    }

    public record UiNotification(Integer notificationId, Integer userId, String notificationType,
                                 String notificationMessage, boolean read, LocalDateTime createdAt) {
    }

    public record UiAuditLog(Integer auditId, LocalDateTime auditTimestamp, String auditActionUser,
                             String auditActionDescription, String auditModuleName) {
    }

    public record UiPanelState(String panelId, Integer userId, Integer notificationCount,
                               String currentPanelState, String breadcrumbTrail,
                               String sidebarMenuItems, String activeUserRole) {
    }

    public record UiDashboardState(Integer dashboardStateId, Integer userId, String dateRangeFilter,
                                   Integer warehouseId, String kpiDataFeed,
                                   String chartDatasetArray, Integer totalOrders,
                                   Double totalRevenue, Integer lowStockCount,
                                   Integer shipmentsToday, String exportFormat) {
    }

    public record UiInventoryPanel(Integer inventoryPanelId, Integer userId, String productList,
                                   String productSku, String productName, String productCategory,
                                   Integer currentStockLevel, Integer reorderThreshold,
                                   String stockStatus, String barcodeRfidValue,
                                   String warehouseZoneData, String stockTransferRequest) {
    }

    public record UiOrderManagement(Integer orderManagementId, Integer userId, Integer orderId,
                                    String orderStatus, String customerName, String customerEmail,
                                    String deliveryAddress, String orderProductSkus,
                                    Integer orderQuantity, Double orderValue,
                                    String discountCodeApplied, String orderDate,
                                    String returnRefundRequest, byte[] invoicePdf,
                                    String packingSlipData) {
    }

    public record UiTransportLogistics(Integer transportUiId, Integer userId, Integer shipmentId,
                                       String shipmentStatus, Double gpsLatitude,
                                       Double gpsLongitude, String vehicleId,
                                       String estimatedArrivalTime, String carrierName,
                                       String routePlan, String carrierApiResponse,
                                       String deliveryOrigin, String deliveryDestination,
                                       String dropShipConfig) {
    }

    public record UiPricingCommission(Integer pricingUiId, Integer userId,
                                      String productPriceTierList, Double pricePerUnit,
                                      Integer minOrderQuantity, String tierLevel,
                                      String discountCode, String discountType,
                                      Double discountValue, String discountValidFrom,
                                      String discountValidTo, Double minOrderValueForDiscount,
                                      String agentId, Double commissionRate,
                                      Double commissionAmount, String payoutStatus) {
    }

    public record UiForecastReports(Integer forecastUiId, Integer userId, String historicalSalesData,
                                    String forecastModelOutput, Integer forecastMonths,
                                    String seasonalParameters, String productCategoryFilter,
                                    String reorderSuggestions, String demandHeatmapData,
                                    String reportExportFormat) {
    }

    public record UiExceptionHandling(Integer exceptionUiId, Integer userId, String notificationList,
                                      Integer notificationId, String notificationType,
                                      String notificationMessage, boolean notificationRead,
                                      Integer exceptionErrorCode, String exceptionModuleName,
                                      String exceptionStackTrace, String alertConfigRules,
                                      String auditLogEntries, String auditTimestamp,
                                      String auditActionUser, String auditActionDescription,
                                      String auditModuleName) {
    }
}
