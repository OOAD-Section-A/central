package com.jackfruit.scm.database.dao;

import com.jackfruit.scm.database.model.OrderItem;
import java.util.List;

public interface OrderItemDao extends CrudDao<OrderItem, String> {

    List<OrderItem> findByOrderId(String orderId);
}
