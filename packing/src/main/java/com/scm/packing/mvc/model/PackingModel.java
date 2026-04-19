package com.scm.packing.mvc.model;

import com.scm.packing.integration.database.IDatabaseLayer;
import com.scm.packing.integration.exceptions.IExceptionDispatcher;
import com.scm.packing.observer.PackingEventType;
import com.scm.packing.observer.PackingObserver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central model of the MVC triad.  Holds the canonical collections of
 * {@link Order}s, {@link PackingJob}s, {@link BarcodeLabel}s, and
 * {@link PackingUnit}s; notifies registered {@link PackingObserver}s
 * whenever any collection or individual entity changes.
 *
 * <p><b>Design Pattern – Observer (Behavioral):</b> This class is the
 * <i>subject</i>.  Views register as observers and are notified on every
 * state change, keeping the model completely UI-agnostic.</p>
 *
 * <p><b>Multithreading:</b></p>
 * <ul>
 *   <li>All maps are {@link ConcurrentHashMap}s — safe for concurrent
 *       reads by the EDT and writes by worker threads.</li>
 *   <li>The observer list is a {@link CopyOnWriteArrayList} — supports
 *       safe iteration during notification even if observers are
 *       added/removed concurrently.</li>
 *   <li>Unit ID generation uses {@link AtomicInteger} for thread safety.</li>
 * </ul>
 *
 * <p><b>SOLID – Single Responsibility:</b> The model manages state and
 * observer notification.  It does not know about the UI or persistence
 * formats.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> The model depends on
 * {@link IDatabaseLayer} and {@link IExceptionDispatcher} abstractions
 * injected via the constructor — it never instantiates concrete adapters.</p>
 *
 * <p><b>GRASP – Information Expert:</b> The model is the expert on the
 * current set of orders, packing jobs, barcodes, and units.</p>
 */
public class PackingModel {

    // ---------------------------------------------------------------
    // Dependencies (injected — no Singletons)
    // ---------------------------------------------------------------

    private final IDatabaseLayer databaseLayer;
    private final IExceptionDispatcher exceptionDispatcher;

    // ---------------------------------------------------------------
    // Observable state
    // ---------------------------------------------------------------

    /** Thread-safe map: orderId → Order. */
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    /** Thread-safe map: jobId → PackingJob. */
    private final Map<String, PackingJob> jobs = new ConcurrentHashMap<>();

    /** Thread-safe map: jobId → BarcodeLabel (generated after packing). */
    private final Map<String, BarcodeLabel> barcodes = new ConcurrentHashMap<>();

    /** Thread-safe list of unitised groups. */
    private final List<PackingUnit> units = new CopyOnWriteArrayList<>();

    /** Thread-safe unit ID counter. */
    private final AtomicInteger unitIdCounter = new AtomicInteger(1);

    /**
     * Thread-safe observer list.
     * CopyOnWriteArrayList allows safe iteration during notification
     * even if a listener is removed mid-iteration.
     */
    private final List<PackingObserver> observers = new CopyOnWriteArrayList<>();

    // ---------------------------------------------------------------
    // Constructor — Dependency Injection (no Singleton)
    // ---------------------------------------------------------------

    public PackingModel(IDatabaseLayer databaseLayer, IExceptionDispatcher exceptionDispatcher) {
        this.databaseLayer = databaseLayer;
        this.exceptionDispatcher = exceptionDispatcher;
    }

    // ---------------------------------------------------------------
    // Observer registration
    // ---------------------------------------------------------------

    public void addObserver(PackingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PackingObserver observer) {
        observers.remove(observer);
    }

    // ---------------------------------------------------------------
    // Event notification (called from any thread)
    // ---------------------------------------------------------------

    private void fireEvent(PackingEventType type, PackingJob job, String message) {
        for (PackingObserver observer : observers) {
            observer.onPackingEvent(type, job, message);
        }
    }

    // ---------------------------------------------------------------
    // Order management
    // ---------------------------------------------------------------

    /**
     * Loads all orders from the database (or flat-file seed data).
     */
    public void loadOrders() {
        try {
            List<Order> loaded = databaseLayer.loadOrders();
            for (Order order : loaded) {
                orders.put(order.getOrderId(), order);
            }
            publishStatus("Loaded " + loaded.size() + " orders from persistence layer.");
        } catch (Exception e) {
            exceptionDispatcher.dispatchUnregistered(
                    "Failed to load orders: " + e.getMessage());
            publishStatus("⚠ Warning: Could not load orders.");
        }
    }

    /** Returns all orders. */
    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(new ArrayList<>(orders.values()));
    }

    /** Returns a single order by ID. */
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    /** Marks an order as packed. */
    public void markOrderPacked(String orderId) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setPacked(true);
            databaseLayer.updateOrder(order);
        }
    }

    // ---------------------------------------------------------------
    // Job management
    // ---------------------------------------------------------------

    public void addJob(PackingJob job) {
        jobs.put(job.getJobId(), job);

        try {
            databaseLayer.saveJob(job);
        } catch (Exception e) {
            // Exception 359: PACKAGE_CREATION_FAILED (MINOR)
            exceptionDispatcher.dispatch(359, "MINOR", "Packing",
                    "Failed to persist packing job " + job.getJobId() + ": " + e.getMessage());
        }

        fireEvent(PackingEventType.JOB_ADDED, job, "New job added: " + job.getJobId());
    }

    public void updateJob(PackingJob job) {
        jobs.put(job.getJobId(), job);

        try {
            databaseLayer.updateJob(job);
        } catch (Exception e) {
            exceptionDispatcher.dispatchUnregistered(
                    "Failed to update job " + job.getJobId() + ": " + e.getMessage());
        }

        fireEvent(PackingEventType.JOB_UPDATED, job, null);
    }

    public List<PackingJob> getAllJobs() {
        return Collections.unmodifiableList(new ArrayList<>(jobs.values()));
    }

    public PackingJob getJob(String jobId) {
        return jobs.get(jobId);
    }

    // ---------------------------------------------------------------
    // Barcode management (Labeling & Traceability)
    // ---------------------------------------------------------------

    /**
     * Registers a barcode label generated after a job is packed.
     */
    public void addBarcode(BarcodeLabel label) {
        barcodes.put(label.getJobId(), label);
        publishStatus("🏷 " + label.toLogString());
    }

    /** Returns the barcode for a specific job, or null. */
    public BarcodeLabel getBarcode(String jobId) {
        return barcodes.get(jobId);
    }

    /** Returns all generated barcodes. */
    public List<BarcodeLabel> getAllBarcodes() {
        return new ArrayList<>(barcodes.values());
    }

    // ---------------------------------------------------------------
    // Unitization management
    // ---------------------------------------------------------------

    /** Generates a unique pallet ID (thread-safe). */
    public String nextUnitId() {
        return "PLT-" + String.format("%03d", unitIdCounter.getAndIncrement());
    }

    /** Adds a pallet to the model. */
    public void addUnit(PackingUnit unit) {
        units.add(unit);
        publishStatus("📦 Created Pallet " + unit.getUnitId()
                + " with " + unit.getCurrentSize() + " jobs"
                + String.format(" (%.2f kg)", unit.getTotalWeightKg()));
    }

    /** Returns all pallets. */
    public List<PackingUnit> getAllUnits() {
        return Collections.unmodifiableList(units);
    }

    /** Looks up a pallet by its ID. */
    public PackingUnit getUnit(String unitId) {
        for (PackingUnit unit : units) {
            if (unit.getUnitId().equals(unitId)) return unit;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Status publishing
    // ---------------------------------------------------------------

    public void publishStatus(String message) {
        fireEvent(PackingEventType.STATUS_MESSAGE, null, message);
    }

    // ---------------------------------------------------------------
    // Database loading
    // ---------------------------------------------------------------

    public void loadFromDatabase() {
        try {
            List<PackingJob> loaded = databaseLayer.loadAllJobs();
            for (PackingJob job : loaded) {
                jobs.put(job.getJobId(), job);
                fireEvent(PackingEventType.JOB_ADDED, job, "Loaded from DB: " + job.getJobId());
            }
            if (!loaded.isEmpty()) {
                publishStatus("Loaded " + loaded.size() + " existing jobs.");
            }
        } catch (Exception e) {
            exceptionDispatcher.dispatchUnregistered(
                    "Failed to load jobs: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Accessors for subsystem dependencies
    // ---------------------------------------------------------------

    public IExceptionDispatcher getExceptionDispatcher() { return exceptionDispatcher; }
    public IDatabaseLayer getDatabaseLayer()             { return databaseLayer; }
}
