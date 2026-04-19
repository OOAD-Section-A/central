package com.scm.packing.strategy;

import com.scm.packing.mvc.model.PackingItem;

import java.util.List;

/**
 * Strategy interface for the <b>Strategy (Behavioral) design pattern</b>.
 *
 * <p>Different packing approaches (standard, fragile, bulk) implement this
 * interface.  The {@link com.scm.packing.worker.PackingWorker} delegates to
 * the chosen strategy at runtime, which means the packing algorithm can be
 * swapped without changing the worker code.</p>
 *
 * <p><b>SOLID – Open/Closed:</b> New strategies can be added by creating a
 * new implementation of this interface — no existing code needs to change.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> The worker depends on this
 * abstraction, not on any concrete strategy class.</p>
 */
public interface IPackingStrategy {

    /**
     * Returns the human-readable name of this strategy (shown in UI logs).
     *
     * @return strategy name, e.g. "Standard" or "Fragile"
     */
    String getStrategyName();

    /**
     * Packs a single item.  The implementation may simulate delays, print
     * labels, or apply special wrapping depending on the strategy.
     *
     * <p>This method is called from a <b>background thread</b>
     * ({@code SwingWorker.doInBackground}), so it is safe to sleep here
     * to simulate real work.</p>
     *
     * @param item           the item to pack
     * @param itemIndex      0-based index of the item within the job
     * @param totalItems     total number of items in the job
     * @return a short log message describing what was done
     * @throws Exception if a packing error occurs (e.g. item too heavy)
     */
    String packItem(PackingItem item, int itemIndex, int totalItems) throws Exception;

    /**
     * Returns the estimated time in milliseconds to pack one item using
     * this strategy.  Used for progress-bar smoothing.
     *
     * @return estimated ms per item
     */
    long estimatedTimePerItemMs();
}
