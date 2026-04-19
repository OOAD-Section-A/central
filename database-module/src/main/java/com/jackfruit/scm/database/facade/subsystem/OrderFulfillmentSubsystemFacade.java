package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.OrderFulfillmentModels.FulfillmentOrder;
import com.jackfruit.scm.database.model.OrderFulfillmentModels.PackingDetail;
import java.sql.Date;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class OrderFulfillmentSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public OrderFulfillmentSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createFulfillmentOrder(FulfillmentOrder fulfillmentOrder) {
        jdbcOperations.update(
                """
                INSERT INTO fulfillment_orders
                (fulfillment_id, order_id, customer_id, product_id, quantity, order_status, order_date, total_amount,
                 customer_name, shipping_address, contact_number, payment_id, payment_status, payment_method,
                 product_stock_available, reserved_quantity, warehouse_id, storage_location_rack_id, picking_status,
                 packing_status, shipment_id, courier_partner, tracking_id, shipping_status, estimated_delivery_date,
                 fulfillment_status, assigned_staff_id, reservation_timestamp, delivery_instructions,
                 failed_delivery_attempts, cancellation_status, cancellation_reason, cancellation_timestamp,
                 assigned_warehouse, priority_level, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, fulfillmentOrder.fulfillmentId());
                    statement.setString(2, fulfillmentOrder.orderId());
                    statement.setString(3, fulfillmentOrder.customerId());
                    statement.setString(4, fulfillmentOrder.productId());
                    statement.setObject(5, fulfillmentOrder.quantity());
                    statement.setString(6, fulfillmentOrder.orderStatus());
                    statement.setTimestamp(7, fulfillmentOrder.orderDate() == null ? null : Timestamp.valueOf(fulfillmentOrder.orderDate()));
                    statement.setBigDecimal(8, fulfillmentOrder.totalAmount());
                    statement.setString(9, fulfillmentOrder.customerName());
                    statement.setString(10, fulfillmentOrder.shippingAddress());
                    statement.setString(11, fulfillmentOrder.contactNumber());
                    statement.setString(12, fulfillmentOrder.paymentId());
                    statement.setString(13, fulfillmentOrder.paymentStatus());
                    statement.setString(14, fulfillmentOrder.paymentMethod());
                    statement.setObject(15, fulfillmentOrder.productStockAvailable());
                    statement.setObject(16, fulfillmentOrder.reservedQuantity());
                    statement.setString(17, fulfillmentOrder.warehouseId());
                    statement.setString(18, fulfillmentOrder.storageLocationRackId());
                    statement.setString(19, fulfillmentOrder.pickingStatus());
                    statement.setString(20, fulfillmentOrder.packingStatus());
                    statement.setString(21, fulfillmentOrder.shipmentId());
                    statement.setString(22, fulfillmentOrder.courierPartner());
                    statement.setString(23, fulfillmentOrder.trackingId());
                    statement.setString(24, fulfillmentOrder.shippingStatus());
                    statement.setDate(25, fulfillmentOrder.estimatedDeliveryDate() == null ? null : Date.valueOf(fulfillmentOrder.estimatedDeliveryDate()));
                    statement.setString(26, fulfillmentOrder.fulfillmentStatus());
                    statement.setString(27, fulfillmentOrder.assignedStaffId());
                    statement.setTimestamp(28, fulfillmentOrder.reservationTimestamp() == null ? null : Timestamp.valueOf(fulfillmentOrder.reservationTimestamp()));
                    statement.setString(29, fulfillmentOrder.deliveryInstructions());
                    statement.setObject(30, fulfillmentOrder.failedDeliveryAttempts());
                    statement.setString(31, fulfillmentOrder.cancellationStatus());
                    statement.setString(32, fulfillmentOrder.cancellationReason());
                    statement.setTimestamp(33, fulfillmentOrder.cancellationTimestamp() == null ? null : Timestamp.valueOf(fulfillmentOrder.cancellationTimestamp()));
                    statement.setString(34, fulfillmentOrder.assignedWarehouse());
                    statement.setString(35, fulfillmentOrder.priorityLevel());
                    statement.setTimestamp(36, Timestamp.valueOf(fulfillmentOrder.createdAt()));
                });
    }

    public List<FulfillmentOrder> listFulfillmentOrders() {
        return jdbcOperations.query(
                "SELECT * FROM fulfillment_orders",
                resultSet -> new FulfillmentOrder(
                        resultSet.getString("fulfillment_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("product_id"),
                        (Integer) resultSet.getObject("quantity"),
                        resultSet.getString("order_status"),
                        resultSet.getTimestamp("order_date") == null ? null : resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("shipping_address"),
                        resultSet.getString("contact_number"),
                        resultSet.getString("payment_id"),
                        resultSet.getString("payment_status"),
                        resultSet.getString("payment_method"),
                        (Integer) resultSet.getObject("product_stock_available"),
                        (Integer) resultSet.getObject("reserved_quantity"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getString("storage_location_rack_id"),
                        resultSet.getString("picking_status"),
                        resultSet.getString("packing_status"),
                        resultSet.getString("shipment_id"),
                        resultSet.getString("courier_partner"),
                        resultSet.getString("tracking_id"),
                        resultSet.getString("shipping_status"),
                        resultSet.getDate("estimated_delivery_date") == null ? null : resultSet.getDate("estimated_delivery_date").toLocalDate(),
                        resultSet.getString("fulfillment_status"),
                        resultSet.getString("assigned_staff_id"),
                        resultSet.getTimestamp("reservation_timestamp") == null ? null : resultSet.getTimestamp("reservation_timestamp").toLocalDateTime(),
                        resultSet.getString("delivery_instructions"),
                        (Integer) resultSet.getObject("failed_delivery_attempts"),
                        resultSet.getString("cancellation_status"),
                        resultSet.getString("cancellation_reason"),
                        resultSet.getTimestamp("cancellation_timestamp") == null ? null : resultSet.getTimestamp("cancellation_timestamp").toLocalDateTime(),
                        resultSet.getString("assigned_warehouse"),
                        resultSet.getString("priority_level"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createPackingDetail(PackingDetail packingDetail) {
        jdbcOperations.update(
                "INSERT INTO packing_details (packing_id, fulfillment_id, package_type, packed_by, packed_at, package_weight) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, packingDetail.packingId());
                    statement.setString(2, packingDetail.fulfillmentId());
                    statement.setString(3, packingDetail.packageType());
                    statement.setString(4, packingDetail.packedBy());
                    statement.setTimestamp(5, Timestamp.valueOf(packingDetail.packedAt()));
                    statement.setBigDecimal(6, packingDetail.packageWeight());
                });
    }
}
