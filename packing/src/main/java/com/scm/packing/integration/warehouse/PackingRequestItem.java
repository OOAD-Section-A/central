package com.scm.packing.integration.warehouse;

/**
 * WMS-facing item payload used to request packing for an order.
 */
public class PackingRequestItem {

    private final String itemId;
    private final String description;
    private final double weightKg;
    private final boolean fragile;

    public PackingRequestItem(String itemId, String description, double weightKg, boolean fragile) {
        this.itemId = itemId;
        this.description = description;
        this.weightKg = weightKg;
        this.fragile = fragile;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public boolean isFragile() {
        return fragile;
    }
}
