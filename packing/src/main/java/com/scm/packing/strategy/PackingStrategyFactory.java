package com.scm.packing.strategy;

import com.scm.packing.mvc.model.PackingItem;
import com.scm.packing.mvc.model.PackingJob;

import java.util.List;

/**
 * Factory that creates the appropriate {@link IPackingStrategy} for a given
 * packing job.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The decision of
 * which concrete strategy to instantiate is encapsulated here.  Callers
 * ask for "a strategy for this job" and receive the correct implementation
 * without knowing the concrete class.</p>
 *
 * <p><b>SOLID – Open/Closed:</b> To support a new strategy (e.g.
 * {@code HazardousPackingStrategy}), add the class and a new branch here —
 * no other code needs to change.</p>
 *
 * <p><b>GRASP – Creator:</b> The factory has the information needed to decide
 * which strategy fits (it inspects the item list), so it is the natural
 * creator of strategy objects.</p>
 *
 * <p><b>Note:</b> This is <i>not</i> a Singleton.  A new factory can be
 * created wherever needed, or the static helper can be used directly.</p>
 */
public class PackingStrategyFactory {

    /**
     * Returns the best packing strategy for the given job.
     *
     * <ul>
        *   <li>Only fragile items → {@link FragilePackingStrategy}</li>
        *   <li>Only non-fragile items → {@link StandardPackingStrategy}</li>
        *   <li>Mixture of both → {@link MixedPackingStrategy}</li>
     * </ul>
     *
     * @param job the packing job to inspect
     * @return a ready-to-use strategy instance
     */
    public IPackingStrategy createStrategy(PackingJob job) {
        // -----------------------------------------------------------
        // Factory Method pattern: decision logic lives here,
        // concrete classes are hidden from the caller.
        // -----------------------------------------------------------

        boolean hasFragile = false;
        boolean hasStandard = false;

        for (PackingItem item : job.getItems()) {
            if (item.isFragile()) {
                hasFragile = true;
            } else {
                hasStandard = true;
            }
        }

        if (hasFragile && hasStandard) {
            return new MixedPackingStrategy();
        }
        if (hasFragile) {
            return new FragilePackingStrategy();
        }
        return new StandardPackingStrategy();
    }
}
