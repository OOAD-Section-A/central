package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.OrderItem;

public class OrderAdapter {

    private final SupplyChainDatabaseFacade facade;

    public OrderAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createOrder(Order order) {
        facade.orders().createOrder(order);
    }

    public void addOrderItem(OrderItem item) {
        facade.orders().addOrderItem(item);
    }
}
