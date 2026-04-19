package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderFulfillmentModels {

    private OrderFulfillmentModels() {
    }

    public record FulfillmentOrder(String fulfillmentId, String orderId, String customerId,
                                   String productId, Integer quantity, String orderStatus,
                                   LocalDateTime orderDate, BigDecimal totalAmount,
                                   String customerName, String shippingAddress,
                                   String contactNumber, String paymentId,
                                   String paymentStatus, String paymentMethod,
                                   Integer productStockAvailable, Integer reservedQuantity,
                                   String warehouseId, String storageLocationRackId,
                                   String pickingStatus, String packingStatus,
                                   String shipmentId, String courierPartner,
                                   String trackingId, String shippingStatus,
                                   LocalDate estimatedDeliveryDate,
                                   String fulfillmentStatus, String assignedStaffId,
                                   LocalDateTime reservationTimestamp,
                                   String deliveryInstructions, Integer failedDeliveryAttempts,
                                   String cancellationStatus, String cancellationReason,
                                   LocalDateTime cancellationTimestamp,
                                   String assignedWarehouse, String priorityLevel,
                                   LocalDateTime createdAt) {
    }

    public record PackingDetail(String packingId, String fulfillmentId, String packageType,
                                String packedBy, LocalDateTime packedAt, BigDecimal packageWeight) {
    }
}
