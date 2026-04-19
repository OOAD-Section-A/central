package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public final class DeliveryMonitoringModels {

    private DeliveryMonitoringModels() {
    }

    public record DeliveryTrackingRoute(String routePlanId, String deliveryId, String orderId,
                                        String customerId, String pickupAddress, String dropoffAddress,
                                        String itemDescription, Double itemWeightKg,
                                        LocalDateTime committedDeliveryWindowStart,
                                        LocalDateTime committedDeliveryWindowEnd,
                                        LocalDateTime orderCreatedAt, LocalDateTime dispatchedAt,
                                        String warehouseId, Double warehouseLatitude,
                                        Double warehouseLongitude, String riderId,
                                        LocalDateTime assignedAt, String customerName,
                                        String customerEmail, String customerPhone,
                                        String preferredNotificationChannel, String vehicleId,
                                        String plateNumber, String vehicleType, Double maxPayloadKg,
                                        Double temperatureMinC, Double temperatureMaxC,
                                        Boolean hazardous, String carrierId, String trackingApiUrl,
                                        String waypoints, LocalDateTime plannedDeparture,
                                        LocalDateTime plannedArrival, LocalDateTime currentEta,
                                        String routeStatus) {
    }

    public record DeliveryTrackingWaypoint(String waypointId, String routePlanId,
                                           int waypointSequence, String waypointLocation) {
    }

    public record DeliveryTrackingEvent(String trackingEventId, String deliveryId, String riderId,
                                        String vehicleId, String timelineStage, String gpsCoordinates,
                                        LocalDateTime eventTimestamp, String alertMessage,
                                        boolean requiresRerouting) {
    }
}
