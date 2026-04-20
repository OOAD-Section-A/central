package com.scm.packing.integration.database;

import com.scm.packing.mvc.model.Order;
import com.scm.packing.mvc.model.PackingJob;

import java.util.List;

/**
 * Abstraction for the persistence layer used by the Packing subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This interface is the
 * "target" that the rest of the application codes against.  Concrete
 * implementations adapt either the real SCM database facade or a local
 * flat-file fallback to this common contract.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> High-level modules (Model,
 * Controller) depend on this interface, never on a concrete database
 * class.  The concrete adapter is injected via the constructor.</p>
 *
 * <p><b>SOLID – Interface Segregation:</b> Only packing-relevant
 * persistence methods are declared here — no warehouse, delivery, or
 * forecasting operations.</p>
 */
public interface IDatabaseLayer {

    // ---------------------------------------------------------------
    // Order operations
    // ---------------------------------------------------------------

    /**
     * Returns all orders available for packing.
     *
     * <p>When the SCM database is connected, this would query the
     * {@code orders} and {@code order_items} tables via
     * {@code facade.orders().listOrderItems(...)}.  In the flat-file
     * fallback, sample seed data is returned.</p>
     *
     * @return list of orders with their items
     */
    List<Order> loadOrders();

    /**
     * Updates the packed status of an order.
     *
     * @param order the order to update
     */
    void updateOrder(Order order);

    // ---------------------------------------------------------------
    // Packing job operations
    // ---------------------------------------------------------------

    /**
     * Persists a new packing job.
     *
     * @param job the job to save
     */
    void saveJob(PackingJob job);

    /**
     * Updates an existing job (status, progress).
     *
     * @param job the job with updated fields
     */
    void updateJob(PackingJob job);

    /**
     * Deletes a persisted packing job record so the order can be packed again.
     *
     * @param job the job to delete/unpack
     * @return {@code true} if one or more persisted records were removed
     */
    boolean deleteJob(PackingJob job);

    /**
     * Returns all jobs currently known to the persistence layer.
     *
     * @return list of packing jobs
     */
    List<PackingJob> loadAllJobs();

    /**
     * Deletes every persisted job.  Called on shutdown for the flat-file
     * fallback to honour the "no persistence across sessions" rule.
     */
    void clearAll();
}
