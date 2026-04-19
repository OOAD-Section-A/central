package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingEvent;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingRoute;
import com.jackfruit.scm.database.model.DeliveryMonitoringModels.DeliveryTrackingWaypoint;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DeliveryMonitoringSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public DeliveryMonitoringSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createTrackingRoute(DeliveryTrackingRoute route) {
        jdbcOperations.update(
                """
                INSERT INTO delivery_tracking_routes
                (route_plan_id, delivery_id, order_id, customer_id, pickup_address, dropoff_address, item_description,
                 item_weight_kg, committed_delivery_window_start, committed_delivery_window_end, order_created_at,
                 dispatched_at, warehouse_id, warehouse_latitude, warehouse_longitude, rider_id, assigned_at,
                 customer_name, customer_email, customer_phone, preferred_notification_channel, vehicle_id,
                 plate_number, vehicle_type, max_payload_kg, temperature_min_c, temperature_max_c, is_hazardous,
                 carrier_id, tracking_api_url, waypoints, planned_departure, planned_arrival, current_eta, route_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, route.routePlanId());
                    statement.setString(2, route.deliveryId());
                    statement.setString(3, route.orderId());
                    statement.setString(4, route.customerId());
                    statement.setString(5, route.pickupAddress());
                    statement.setString(6, route.dropoffAddress());
                    statement.setString(7, route.itemDescription());
                    statement.setObject(8, route.itemWeightKg());
                    statement.setTimestamp(9, route.committedDeliveryWindowStart() == null ? null : Timestamp.valueOf(route.committedDeliveryWindowStart()));
                    statement.setTimestamp(10, route.committedDeliveryWindowEnd() == null ? null : Timestamp.valueOf(route.committedDeliveryWindowEnd()));
                    statement.setTimestamp(11, route.orderCreatedAt() == null ? null : Timestamp.valueOf(route.orderCreatedAt()));
                    statement.setTimestamp(12, route.dispatchedAt() == null ? null : Timestamp.valueOf(route.dispatchedAt()));
                    statement.setString(13, route.warehouseId());
                    statement.setObject(14, route.warehouseLatitude());
                    statement.setObject(15, route.warehouseLongitude());
                    statement.setString(16, route.riderId());
                    statement.setTimestamp(17, route.assignedAt() == null ? null : Timestamp.valueOf(route.assignedAt()));
                    statement.setString(18, route.customerName());
                    statement.setString(19, route.customerEmail());
                    statement.setString(20, route.customerPhone());
                    statement.setString(21, route.preferredNotificationChannel());
                    statement.setString(22, route.vehicleId());
                    statement.setString(23, route.plateNumber());
                    statement.setString(24, route.vehicleType());
                    statement.setObject(25, route.maxPayloadKg());
                    statement.setObject(26, route.temperatureMinC());
                    statement.setObject(27, route.temperatureMaxC());
                    statement.setObject(28, route.hazardous());
                    statement.setString(29, route.carrierId());
                    statement.setString(30, route.trackingApiUrl());
                    statement.setString(31, route.waypoints());
                    statement.setTimestamp(32, route.plannedDeparture() == null ? null : Timestamp.valueOf(route.plannedDeparture()));
                    statement.setTimestamp(33, route.plannedArrival() == null ? null : Timestamp.valueOf(route.plannedArrival()));
                    statement.setTimestamp(34, route.currentEta() == null ? null : Timestamp.valueOf(route.currentEta()));
                    statement.setString(35, route.routeStatus());
                });
    }

    public List<DeliveryTrackingRoute> listTrackingRoutes() {
        return jdbcOperations.query(
                "SELECT * FROM delivery_tracking_routes",
                resultSet -> new DeliveryTrackingRoute(
                        resultSet.getString("route_plan_id"),
                        resultSet.getString("delivery_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("pickup_address"),
                        resultSet.getString("dropoff_address"),
                        resultSet.getString("item_description"),
                        nullableDouble(resultSet, "item_weight_kg"),
                        resultSet.getTimestamp("committed_delivery_window_start") == null ? null : resultSet.getTimestamp("committed_delivery_window_start").toLocalDateTime(),
                        resultSet.getTimestamp("committed_delivery_window_end") == null ? null : resultSet.getTimestamp("committed_delivery_window_end").toLocalDateTime(),
                        resultSet.getTimestamp("order_created_at") == null ? null : resultSet.getTimestamp("order_created_at").toLocalDateTime(),
                        resultSet.getTimestamp("dispatched_at") == null ? null : resultSet.getTimestamp("dispatched_at").toLocalDateTime(),
                        resultSet.getString("warehouse_id"),
                        nullableDouble(resultSet, "warehouse_latitude"),
                        nullableDouble(resultSet, "warehouse_longitude"),
                        resultSet.getString("rider_id"),
                        resultSet.getTimestamp("assigned_at") == null ? null : resultSet.getTimestamp("assigned_at").toLocalDateTime(),
                        resultSet.getString("customer_name"),
                        resultSet.getString("customer_email"),
                        resultSet.getString("customer_phone"),
                        resultSet.getString("preferred_notification_channel"),
                        resultSet.getString("vehicle_id"),
                        resultSet.getString("plate_number"),
                        resultSet.getString("vehicle_type"),
                        nullableDouble(resultSet, "max_payload_kg"),
                        nullableDouble(resultSet, "temperature_min_c"),
                        nullableDouble(resultSet, "temperature_max_c"),
                        (Boolean) resultSet.getObject("is_hazardous"),
                        resultSet.getString("carrier_id"),
                        resultSet.getString("tracking_api_url"),
                        resultSet.getString("waypoints"),
                        resultSet.getTimestamp("planned_departure") == null ? null : resultSet.getTimestamp("planned_departure").toLocalDateTime(),
                        resultSet.getTimestamp("planned_arrival") == null ? null : resultSet.getTimestamp("planned_arrival").toLocalDateTime(),
                        resultSet.getTimestamp("current_eta") == null ? null : resultSet.getTimestamp("current_eta").toLocalDateTime(),
                        resultSet.getString("route_status")));
    }

    public void createTrackingWaypoint(DeliveryTrackingWaypoint waypoint) {
        jdbcOperations.update(
                "INSERT INTO delivery_tracking_waypoints (waypoint_id, route_plan_id, waypoint_sequence, waypoint_location) VALUES (?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, waypoint.waypointId());
                    statement.setString(2, waypoint.routePlanId());
                    statement.setInt(3, waypoint.waypointSequence());
                    statement.setString(4, waypoint.waypointLocation());
                });
    }

    public void createTrackingEvent(DeliveryTrackingEvent event) {
        jdbcOperations.update(
                """
                INSERT INTO delivery_tracking_events
                (tracking_event_id, delivery_id, rider_id, vehicle_id, timeline_stage, gps_coordinates, event_timestamp, alert_message, requires_rerouting)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, event.trackingEventId());
                    statement.setString(2, event.deliveryId());
                    statement.setString(3, event.riderId());
                    statement.setString(4, event.vehicleId());
                    statement.setString(5, event.timelineStage());
                    statement.setString(6, event.gpsCoordinates());
                    statement.setTimestamp(7, Timestamp.valueOf(event.eventTimestamp()));
                    statement.setString(8, event.alertMessage());
                    statement.setBoolean(9, event.requiresRerouting());
                });
    }

    private static Double nullableDouble(ResultSet resultSet, String columnName) throws SQLException {
        double value = resultSet.getDouble(columnName);
        return resultSet.wasNull() ? null : value;
    }
}
