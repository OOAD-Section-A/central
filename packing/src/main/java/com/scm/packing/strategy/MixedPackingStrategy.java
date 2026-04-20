package com.scm.packing.strategy;

import com.scm.packing.mvc.model.PackingItem;

/**
 * Mixed packing strategy for jobs that contain both fragile and non-fragile items.
 * Fragile items are delegated to {@link FragilePackingStrategy}; all others use
 * {@link StandardPackingStrategy}.
 */
public class MixedPackingStrategy implements IPackingStrategy {

    private final StandardPackingStrategy standard = new StandardPackingStrategy();
    private final FragilePackingStrategy fragile = new FragilePackingStrategy();

    @Override
    public String getStrategyName() {
        return "Mixed";
    }

    @Override
    public String packItem(PackingItem item, int itemIndex, int totalItems) throws Exception {
        if (item.isFragile()) {
            return fragile.packItem(item, itemIndex, totalItems);
        }
        return standard.packItem(item, itemIndex, totalItems);
    }

    @Override
    public long estimatedTimePerItemMs() {
        return Math.round((standard.estimatedTimePerItemMs() + fragile.estimatedTimePerItemMs()) / 2.0);
    }
}
