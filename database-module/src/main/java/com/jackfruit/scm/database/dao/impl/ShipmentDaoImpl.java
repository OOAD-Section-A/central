package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.ShipmentDao;
import com.jackfruit.scm.database.model.Shipment;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ShipmentDaoImpl extends AbstractJdbcDao implements ShipmentDao {

    @Override
    public void save(Shipment shipment) {
        executeUpdate(
                """
                INSERT INTO delivery_orders
                (delivery_id, order_id, customer_id, delivery_address, delivery_status, delivery_type,
                 delivery_date, delivery_cost, assigned_agent, warehouse_id, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> bindShipment(statement, shipment));
    }

    @Override
    public void update(Shipment shipment) {
        executeUpdate(
                """
                UPDATE delivery_orders
                SET delivery_status = ?, delivery_address = ?, delivery_date = ?, delivery_type = ?,
                    assigned_agent = ?, updated_at = ?, delivery_cost = ?
                WHERE delivery_id = ?
                """,
                statement -> {
                    statement.setString(1, shipment.getDeliveryStatus());
                    statement.setString(2, shipment.getDeliveryAddress());
                    statement.setTimestamp(3, shipment.getDeliveryDate() == null ? null : Timestamp.valueOf(shipment.getDeliveryDate()));
                    statement.setString(4, shipment.getDeliveryType());
                    statement.setString(5, shipment.getAssignedAgent());
                    statement.setTimestamp(6, Timestamp.valueOf(shipment.getUpdatedAt()));
                    statement.setBigDecimal(7, shipment.getDeliveryCost());
                    statement.setString(8, shipment.getDeliveryId());
                });
    }

    @Override
    public Optional<Shipment> findById(String shipmentId) {
        return queryForObject(
                "SELECT * FROM delivery_orders WHERE delivery_id = ?",
                resultSet -> new Shipment(
                        resultSet.getString("delivery_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("delivery_address"),
                        resultSet.getString("delivery_status"),
                        resultSet.getTimestamp("delivery_date") == null ? null : resultSet.getTimestamp("delivery_date").toLocalDateTime(),
                        resultSet.getString("delivery_type"),
                        resultSet.getBigDecimal("delivery_cost"),
                        resultSet.getString("assigned_agent"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("updated_at") == null ? null : resultSet.getTimestamp("updated_at").toLocalDateTime()),
                statement -> statement.setString(1, shipmentId));
    }

    @Override
    public List<Shipment> findAll() {
        return queryForList(
                "SELECT * FROM delivery_orders",
                resultSet -> new Shipment(
                        resultSet.getString("delivery_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("delivery_address"),
                        resultSet.getString("delivery_status"),
                        resultSet.getTimestamp("delivery_date") == null ? null : resultSet.getTimestamp("delivery_date").toLocalDateTime(),
                        resultSet.getString("delivery_type"),
                        resultSet.getBigDecimal("delivery_cost"),
                        resultSet.getString("assigned_agent"),
                        resultSet.getString("warehouse_id"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("updated_at") == null ? null : resultSet.getTimestamp("updated_at").toLocalDateTime()));
    }

    private void bindShipment(PreparedStatement statement, Shipment shipment) throws SQLException {
        statement.setString(1, shipment.getDeliveryId());
        statement.setString(2, shipment.getOrderId());
        statement.setString(3, shipment.getCustomerId());
        statement.setString(4, shipment.getDeliveryAddress());
        statement.setString(5, shipment.getDeliveryStatus());
        statement.setString(6, shipment.getDeliveryType());
        statement.setTimestamp(7, shipment.getDeliveryDate() == null ? null : Timestamp.valueOf(shipment.getDeliveryDate()));
        statement.setBigDecimal(8, shipment.getDeliveryCost());
        statement.setString(9, shipment.getAssignedAgent());
        statement.setString(10, shipment.getWarehouseId());
        statement.setTimestamp(11, Timestamp.valueOf(shipment.getCreatedAt()));
        statement.setTimestamp(12, shipment.getUpdatedAt() == null ? null : Timestamp.valueOf(shipment.getUpdatedAt()));
    }
}
