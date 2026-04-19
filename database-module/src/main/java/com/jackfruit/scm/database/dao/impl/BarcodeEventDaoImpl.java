package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.BarcodeEventDao;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class BarcodeEventDaoImpl extends AbstractJdbcDao implements BarcodeEventDao {

    @Override
    public void save(BarcodeRfidEvent event) {
        executeUpdate(
                """
                INSERT INTO barcode_rfid_events
                (event_id, product_id, rfid_tag, product_name, category, description, transaction_id,
                 warehouse_id, event_timestamp, status, source)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, event.getEventId());
                    statement.setString(2, event.getProductId());
                    statement.setString(3, event.getRfidTag());
                    statement.setString(4, event.getProductName());
                    statement.setString(5, event.getCategory());
                    statement.setString(6, event.getDescription());
                    statement.setString(7, event.getTransactionId());
                    statement.setString(8, event.getWarehouseId());
                    statement.setTimestamp(9, Timestamp.valueOf(event.getEventTimestamp()));
                    statement.setString(10, event.getStatus());
                    statement.setString(11, event.getSource());
                });
    }

    @Override
    public void update(BarcodeRfidEvent event) {
        executeUpdate(
                """
                UPDATE barcode_rfid_events
                SET rfid_tag = ?, product_name = ?, category = ?, description = ?, transaction_id = ?,
                    warehouse_id = ?, event_timestamp = ?, status = ?, source = ?
                WHERE event_id = ?
                """,
                statement -> {
                    statement.setString(1, event.getRfidTag());
                    statement.setString(2, event.getProductName());
                    statement.setString(3, event.getCategory());
                    statement.setString(4, event.getDescription());
                    statement.setString(5, event.getTransactionId());
                    statement.setString(6, event.getWarehouseId());
                    statement.setTimestamp(7, Timestamp.valueOf(event.getEventTimestamp()));
                    statement.setString(8, event.getStatus());
                    statement.setString(9, event.getSource());
                    statement.setString(10, event.getEventId());
                });
    }

    @Override
    public Optional<BarcodeRfidEvent> findById(String eventId) {
        return queryForObject(
                "SELECT * FROM barcode_rfid_events WHERE event_id = ?",
                resultSet -> {
                    BarcodeRfidEvent event = new BarcodeRfidEvent(
                            resultSet.getString("event_id"),
                            resultSet.getString("product_id"),
                            resultSet.getString("rfid_tag"),
                            resultSet.getString("product_name"),
                            resultSet.getString("category"),
                            resultSet.getString("description"),
                            resultSet.getString("transaction_id"),
                            resultSet.getTimestamp("event_timestamp").toLocalDateTime(),
                            resultSet.getString("status"),
                            resultSet.getString("source"));
                    event.setWarehouseId(resultSet.getString("warehouse_id"));
                    return event;
                },
                statement -> statement.setString(1, eventId));
    }

    @Override
    public List<BarcodeRfidEvent> findAll() {
        return queryForList(
                "SELECT * FROM barcode_rfid_events",
                resultSet -> {
                    BarcodeRfidEvent event = new BarcodeRfidEvent(
                            resultSet.getString("event_id"),
                            resultSet.getString("product_id"),
                            resultSet.getString("rfid_tag"),
                            resultSet.getString("product_name"),
                            resultSet.getString("category"),
                            resultSet.getString("description"),
                            resultSet.getString("transaction_id"),
                            resultSet.getTimestamp("event_timestamp").toLocalDateTime(),
                            resultSet.getString("status"),
                            resultSet.getString("source"));
                    event.setWarehouseId(resultSet.getString("warehouse_id"));
                    return event;
                });
    }
}
