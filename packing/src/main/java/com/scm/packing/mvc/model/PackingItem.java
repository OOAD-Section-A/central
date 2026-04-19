package com.scm.packing.mvc.model;

/**
 * Domain model representing a single item that must be packed inside a
 * {@link PackingJob}.
 *
 * <p><b>GRASP – Information Expert:</b> Each item knows its own identity,
 * description, weight, and fragility flag — the data needed by packing
 * strategies to decide how to handle it.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class is a pure data holder.
 * It carries no persistence, UI, or business-rule logic.</p>
 */
public class PackingItem {

    private final String itemId;
    private final String description;
    private final double weightKg;
    private final boolean fragile;

    /**
     * Constructs a new PackingItem.
     *
     * @param itemId      unique item identifier (e.g. SKU)
     * @param description human-readable description
     * @param weightKg    weight in kilograms
     * @param fragile     {@code true} if the item requires special handling
     */
    public PackingItem(String itemId, String description, double weightKg, boolean fragile) {
        this.itemId = itemId;
        this.description = description;
        this.weightKg = weightKg;
        this.fragile = fragile;
    }

    // ---------------------------------------------------------------
    // Getters (immutable object — no setters needed)
    // ---------------------------------------------------------------

    public String getItemId()      { return itemId; }
    public String getDescription() { return description; }
    public double getWeightKg()    { return weightKg; }
    public boolean isFragile()     { return fragile; }

    @Override
    public String toString() {
        return String.format("PackingItem[%s, %.2fkg%s]",
                itemId, weightKg, fragile ? ", FRAGILE" : "");
    }
}
