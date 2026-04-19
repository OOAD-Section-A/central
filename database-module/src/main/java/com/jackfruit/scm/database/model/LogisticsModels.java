package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class LogisticsModels {

    private LogisticsModels() {
    }

    public record LogisticsShipment(String shipmentId, String orderId, String originAddress,
                                    String destinationAddress, BigDecimal packageWeight,
                                    boolean dropShip, String shippingPriority, String shipmentStatus,
                                    String supplierId, Integer inventoryLevel, String routeId,
                                    String carrierId, String trackingId, boolean minCostConstraint,
                                    boolean minTimeConstraint, boolean avoidTollsConstraint,
                                    BigDecimal calculatedCost, LocalDateTime createdAt) {
    }

    public record LogisticsRoute(String routeId, String shipmentId, String gpsCoordinates,
                                 LocalDateTime currentEta, String timelineStage, String routeStatus,
                                 boolean requiresRerouting) {
    }

    public record ShipmentAlert(String alertId, String shipmentId, String alertMessage,
                                String alertSeverity, LocalDateTime createdAt) {
    }
}
