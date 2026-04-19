package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.OrderItemDao;
import com.jackfruit.scm.database.model.OrderItem;
import java.util.List;
import java.util.Optional;

public class OrderItemDaoImpl extends AbstractJdbcDao implements OrderItemDao {

    @Override
    public void save(OrderItem item) {
        executeUpdate(
                """
                INSERT INTO order_items
                (order_item_id, order_id, product_id, ordered_quantity, unit_price, line_total)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, item.getOrderItemId());
                    statement.setString(2, item.getOrderId());
                    statement.setString(3, item.getProductId());
                    statement.setInt(4, item.getOrderedQuantity());
                    statement.setBigDecimal(5, item.getUnitPrice());
                    statement.setBigDecimal(6, item.getLineTotal());
                });
    }

    @Override
    public void update(OrderItem item) {
        executeUpdate(
                """
                UPDATE order_items
                SET ordered_quantity = ?, unit_price = ?, line_total = ?
                WHERE order_item_id = ?
                """,
                statement -> {
                    statement.setInt(1, item.getOrderedQuantity());
                    statement.setBigDecimal(2, item.getUnitPrice());
                    statement.setBigDecimal(3, item.getLineTotal());
                    statement.setString(4, item.getOrderItemId());
                });
    }

    @Override
    public Optional<OrderItem> findById(String orderItemId) {
        return queryForObject(
                "SELECT * FROM order_items WHERE order_item_id = ?",
                resultSet -> new OrderItem(
                        resultSet.getString("order_item_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("product_id"),
                        resultSet.getInt("ordered_quantity"),
                        resultSet.getBigDecimal("unit_price"),
                        resultSet.getBigDecimal("line_total")),
                statement -> statement.setString(1, orderItemId));
    }

    @Override
    public List<OrderItem> findAll() {
        return queryForList(
                "SELECT * FROM order_items",
                resultSet -> new OrderItem(
                        resultSet.getString("order_item_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("product_id"),
                        resultSet.getInt("ordered_quantity"),
                        resultSet.getBigDecimal("unit_price"),
                        resultSet.getBigDecimal("line_total")));
    }

    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        return queryForList(
                "SELECT * FROM order_items WHERE order_id = ?",
                resultSet -> new OrderItem(
                        resultSet.getString("order_item_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("product_id"),
                        resultSet.getInt("ordered_quantity"),
                        resultSet.getBigDecimal("unit_price"),
                        resultSet.getBigDecimal("line_total")),
                statement -> statement.setString(1, orderId));
    }
}
