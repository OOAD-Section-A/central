package com.scm.packing.mvc.model;

import java.util.Collections;
import java.util.List;

/**
 * Domain model representing a customer order retrieved from the database
 * (or from the flat-file fallback's seed data).
 *
 * <p><b>GRASP – Information Expert:</b> The order knows its own identity,
 * customer, items, and whether it has already been packed.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Pure data holder — no
 * persistence, UI, or packing logic.</p>
 */
public class Order {

    private final String orderId;
    private final String customerId;
    private final String customerName;
    private final List<PackingItem> items;

    /** Whether this order has already been assigned to a packing job. */
    private volatile boolean packed;

    public Order(String orderId, String customerId, String customerName,
                 List<PackingItem> items) {
        this.orderId      = orderId;
        this.customerId   = customerId;
        this.customerName = customerName;
        this.items        = items;
        this.packed       = false;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getOrderId()               { return orderId; }
    public String getCustomerId()            { return customerId; }
    public String getCustomerName()          { return customerName; }
    public List<PackingItem> getItems()      { return Collections.unmodifiableList(items); }
    public boolean isPacked()                { return packed; }

    public void setPacked(boolean packed)     { this.packed = packed; }

    @Override
    public String toString() {
        return String.format("Order[%s, customer=%s (%s), items=%d, packed=%b]",
                orderId, customerName, customerId, items.size(), packed);
    }
}
