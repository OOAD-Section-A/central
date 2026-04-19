package com.scm.packing.strategy;

import com.scm.packing.mvc.model.PackingItem;

/**
 * Fragile packing strategy for items marked as fragile.
 *
 * <p><b>Design Pattern – Strategy (Behavioral):</b> This strategy adds extra
 * wrapping and cushioning steps, taking longer but protecting delicate items.
 * It is swapped in automatically when the
 * {@link PackingStrategyFactory} detects fragile items in a job.</p>
 *
 * <p><b>SOLID – Liskov Substitution:</b> This class can be used anywhere an
 * {@link IPackingStrategy} is expected — the caller is unaware of the
 * extra steps involved.</p>
 */
public class FragilePackingStrategy implements IPackingStrategy {

    @Override
    public String getStrategyName() {
        return "Fragile";
    }

    /**
     * Packs a fragile item: bubble-wrap → cushion → place in reinforced box
     * → seal → print "FRAGILE" label.  Simulates ~800 ms per item.
     */
    @Override
    public String packItem(PackingItem item, int itemIndex, int totalItems) throws Exception {
        // -----------------------------------------------------------
        // MULTITHREADING: Runs on a background thread.
        // Extra time simulates bubble-wrapping, cushioning, etc.
        // -----------------------------------------------------------
        Thread.sleep(500);  // bubble-wrap phase
        Thread.sleep(300);  // cushion + seal phase

        return String.format("[Fragile] Carefully packed item %d/%d: %s (%.2f kg) — extra cushioning applied",
                itemIndex + 1, totalItems, item.getDescription(), item.getWeightKg());
    }

    @Override
    public long estimatedTimePerItemMs() {
        return 800L;
    }
}
