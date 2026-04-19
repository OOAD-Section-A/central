package com.scm.packing.integration.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WMS payload used by createPackingJob.
 */
public class PackingRequest {

    private final String orderId;
    private final List<PackingRequestItem> items;

    public PackingRequest(String orderId, List<PackingRequestItem> items) {
        this.orderId = orderId;
        this.items = new ArrayList<>(items);
    }

    public String getOrderId() {
        return orderId;
    }

    public List<PackingRequestItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
