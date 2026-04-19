package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.OrderDao;
import com.jackfruit.scm.database.model.Order;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class OrderDaoImpl extends AbstractJdbcDao implements OrderDao {

    @Override
    public void save(Order order) {
        executeUpdate(
                """
                INSERT INTO orders
                (order_id, customer_id, order_status, order_date, total_amount, payment_status, sales_channel)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, order.getOrderId());
                    statement.setString(2, order.getCustomerId());
                    statement.setString(3, order.getOrderStatus());
                    statement.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
                    statement.setBigDecimal(5, order.getTotalAmount());
                    statement.setString(6, order.getPaymentStatus());
                    statement.setString(7, order.getSalesChannel());
                });
    }

    @Override
    public void update(Order order) {
        executeUpdate(
                """
                UPDATE orders
                SET order_status = ?, total_amount = ?, payment_status = ?, sales_channel = ?
                WHERE order_id = ?
                """,
                statement -> {
                    statement.setString(1, order.getOrderStatus());
                    statement.setBigDecimal(2, order.getTotalAmount());
                    statement.setString(3, order.getPaymentStatus());
                    statement.setString(4, order.getSalesChannel());
                    statement.setString(5, order.getOrderId());
                });
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return queryForObject(
                "SELECT * FROM orders WHERE order_id = ?",
                resultSet -> new Order(
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("order_status"),
                        resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getString("payment_status"),
                        resultSet.getString("sales_channel")),
                statement -> statement.setString(1, orderId));
    }

    @Override
    public List<Order> findAll() {
        return queryForList(
                "SELECT * FROM orders",
                resultSet -> new Order(
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("order_status"),
                        resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getString("payment_status"),
                        resultSet.getString("sales_channel")));
    }
}
