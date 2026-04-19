package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.OrderItem;
import com.jackfruit.scm.database.service.OrderService;
import java.util.List;
import java.util.Optional;

public class OrdersSubsystemFacade {

    private final OrderService orderService;

    public OrdersSubsystemFacade(OrderService orderService) {
        this.orderService = orderService;
    }

    public void createOrder(Order order) {
        orderService.createOrder(order);
    }

    public List<Order> listOrders() {
        return orderService.getOrders();
    }

    public Optional<Order> getOrder(String orderId) {
        return orderService.getOrder(orderId);
    }

    public void addOrderItem(OrderItem item) {
        orderService.addOrderItem(item);
    }

    public List<OrderItem> listOrderItems(String orderId) {
        return orderService.getOrderItems(orderId);
    }
}
