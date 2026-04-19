package com.scm.packing.strategy;

import com.scm.packing.mvc.model.PackingItem;

/**
 * Standard packing strategy for non-fragile, regular-weight items.
 *
 * <p><b>Design Pattern – Strategy (Behavioral):</b> This is one of several
 * interchangeable algorithms selected at runtime by the
 * {@link PackingStrategyFactory}.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class does one thing —
 * defines how a standard item is packed.</p>
 */
public class StandardPackingStrategy implements IPackingStrategy {

    @Override
    public String getStrategyName() {
        return "Standard";
    }

    /**
     * Packs a standard item: place in box → seal → print label.
     * Simulates ~400 ms of work per item.
     */
    @Override
    public String packItem(PackingItem item, int itemIndex, int totalItems) throws Exception {
        // -----------------------------------------------------------
        // MULTITHREADING: This runs on a SwingWorker background thread.
        // Thread.sleep simulates real physical packing time.
        // -----------------------------------------------------------
        Thread.sleep(400);

        return String.format("[Standard] Packed item %d/%d: %s (%.2f kg)",
                itemIndex + 1, totalItems, item.getDescription(), item.getWeightKg());
    }

    @Override
    public long estimatedTimePerItemMs() {
        return 400L;
    }
}
