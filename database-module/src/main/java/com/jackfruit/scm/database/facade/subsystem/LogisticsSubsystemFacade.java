package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.LogisticsModels.LogisticsRoute;
import com.jackfruit.scm.database.model.LogisticsModels.LogisticsShipment;
import com.jackfruit.scm.database.model.LogisticsModels.ShipmentAlert;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class LogisticsSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public LogisticsSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createShipment(LogisticsShipment shipment) {
        jdbcOperations.update(
                """
                INSERT INTO shipments
                (shipment_id, order_id, origin_address, destination_address, package_weight, is_drop_ship,
                 shipping_priority, shipment_status, supplier_id, inventory_level, route_id, carrier_id, tracking_id,
                 min_cost_constraint, min_time_constraint, avoid_tolls_constraint, calculated_cost, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, shipment.shipmentId());
                    statement.setString(2, shipment.orderId());
                    statement.setString(3, shipment.originAddress());
                    statement.setString(4, shipment.destinationAddress());
                    statement.setBigDecimal(5, shipment.packageWeight());
                    statement.setBoolean(6, shipment.dropShip());
                    statement.setString(7, shipment.shippingPriority());
                    statement.setString(8, shipment.shipmentStatus());
                    statement.setString(9, shipment.supplierId());
                    if (shipment.inventoryLevel() == null) {
                        statement.setNull(10, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(10, shipment.inventoryLevel());
                    }
                    statement.setString(11, shipment.routeId());
                    statement.setString(12, shipment.carrierId());
                    statement.setString(13, shipment.trackingId());
                    statement.setBoolean(14, shipment.minCostConstraint());
                    statement.setBoolean(15, shipment.minTimeConstraint());
                    statement.setBoolean(16, shipment.avoidTollsConstraint());
                    statement.setBigDecimal(17, shipment.calculatedCost());
                    statement.setTimestamp(18, Timestamp.valueOf(shipment.createdAt()));
                });
    }

    public List<LogisticsShipment> listShipments() {
        return jdbcOperations.query(
                "SELECT * FROM shipments",
                resultSet -> new LogisticsShipment(
                        resultSet.getString("shipment_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("origin_address"),
                        resultSet.getString("destination_address"),
                        resultSet.getBigDecimal("package_weight"),
                        resultSet.getBoolean("is_drop_ship"),
                        resultSet.getString("shipping_priority"),
                        resultSet.getString("shipment_status"),
                        resultSet.getString("supplier_id"),
                        (Integer) resultSet.getObject("inventory_level"),
                        resultSet.getString("route_id"),
                        resultSet.getString("carrier_id"),
                        resultSet.getString("tracking_id"),
                        resultSet.getBoolean("min_cost_constraint"),
                        resultSet.getBoolean("min_time_constraint"),
                        resultSet.getBoolean("avoid_tolls_constraint"),
                        resultSet.getBigDecimal("calculated_cost"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createRoute(LogisticsRoute route) {
        jdbcOperations.update(
                "INSERT INTO logistics_routes (route_id, shipment_id, gps_coordinates, current_eta, timeline_stage, route_status, requires_rerouting) VALUES (?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, route.routeId());
                    statement.setString(2, route.shipmentId());
                    statement.setString(3, route.gpsCoordinates());
                    statement.setTimestamp(4, route.currentEta() == null ? null : Timestamp.valueOf(route.currentEta()));
                    statement.setString(5, route.timelineStage());
                    statement.setString(6, route.routeStatus());
                    statement.setBoolean(7, route.requiresRerouting());
                });
    }

    public void createShipmentAlert(ShipmentAlert alert) {
        jdbcOperations.update(
                "INSERT INTO shipment_alerts (alert_id, shipment_id, alert_message, alert_severity, created_at) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, alert.alertId());
                    statement.setString(2, alert.shipmentId());
                    statement.setString(3, alert.alertMessage());
                    statement.setString(4, alert.alertSeverity());
                    statement.setTimestamp(5, Timestamp.valueOf(alert.createdAt()));
                });
    }
}
