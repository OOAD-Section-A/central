package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.OrderDao;
import com.jackfruit.scm.database.dao.OrderItemDao;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.OrderItem;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public OrderService(OrderDao orderDao, OrderItemDao orderItemDao) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }

    public void createOrder(Order order) {
        validateOrder(order);
        orderDao.save(order);
    }

    public void updateOrder(Order order) {
        validateOrder(order);
        orderDao.update(order);
    }

    public Optional<Order> getOrder(String orderId) {
        ValidationUtils.requireText(orderId, "orderId");
        return orderDao.findById(orderId);
    }

    public List<Order> getOrders() {
        return orderDao.findAll();
    }

    public void addOrderItem(OrderItem item) {
        validateOrderItem(item);
        orderItemDao.save(item);
    }

    public void updateOrderItem(OrderItem item) {
        validateOrderItem(item);
        orderItemDao.update(item);
    }

    public Optional<OrderItem> getOrderItem(String orderItemId) {
        ValidationUtils.requireText(orderItemId, "orderItemId");
        return orderItemDao.findById(orderItemId);
    }

    public List<OrderItem> getOrderItems(String orderId) {
        ValidationUtils.requireText(orderId, "orderId");
        return orderItemDao.findByOrderId(orderId);
    }

    private void validateOrder(Order order) {
        ValidationUtils.requireText(order.getOrderId(), "orderId");
        ValidationUtils.requireText(order.getCustomerId(), "customerId");
        ValidationUtils.requireText(order.getOrderStatus(), "orderStatus");
        ValidationUtils.requireText(order.getPaymentStatus(), "paymentStatus");
        ValidationUtils.requireText(order.getSalesChannel(), "salesChannel");
        ValidationUtils.requirePositive(order.getTotalAmount(), "totalAmount");
        if (order.getOrderDate() == null) {
            throw new IllegalArgumentException("orderDate cannot be null");
        }
    }

    private void validateOrderItem(OrderItem item) {
        ValidationUtils.requireText(item.getOrderItemId(), "orderItemId");
        ValidationUtils.requireText(item.getOrderId(), "orderId");
        ValidationUtils.requireText(item.getProductId(), "productId");
        ValidationUtils.requireNonNegative(item.getOrderedQuantity(), "orderedQuantity");
        if (item.getOrderedQuantity() == 0) {
            throw new IllegalArgumentException("orderedQuantity must be greater than zero");
        }
        ValidationUtils.requirePositive(item.getUnitPrice(), "unitPrice");
        ValidationUtils.requirePositive(item.getLineTotal(), "lineTotal");
    }
}
