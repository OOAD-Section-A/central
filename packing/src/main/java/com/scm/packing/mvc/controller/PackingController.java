package com.scm.packing.mvc.controller;

import com.scm.packing.mvc.model.*;
import com.scm.packing.strategy.IPackingStrategy;
import com.scm.packing.strategy.PackingStrategyFactory;
import com.scm.packing.worker.PackingWorker;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Controller of the MVC triad.  Receives user actions from the View,
 * translates them into Model mutations, and kicks off background workers.
 *
 * <p><b>MVC role:</b> The controller bridges the View (Swing panels) and
 * the Model ({@link PackingModel}).  The view never modifies the model
 * directly.</p>
 *
 * <p><b>GRASP – Controller:</b> First object beyond the UI layer that
 * receives and coordinates system operations.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> Injected with a
 * {@link PackingModel} and a {@link PackingStrategyFactory}.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Orchestrates user-initiated
 * commands — does not render UI, persist data, or pack items.</p>
 *
 * <p><b>Multithreading:</b> The job-ID counter is an
 * {@link AtomicInteger} to avoid race conditions.</p>
 */
public class PackingController {

    private static final Pattern PACKING_JOB_ID_PATTERN = Pattern.compile("^PKJ-(\\d{4})(?:-\\d+)?$");

    private final PackingModel model;
    private final PackingStrategyFactory strategyFactory;

    /** Thread-safe job ID generator. */
    private final AtomicInteger jobIdCounter = new AtomicInteger(1);

    public PackingController(PackingModel model, PackingStrategyFactory strategyFactory) {
        this.model = model;
        this.strategyFactory = strategyFactory;
    }

    // ---------------------------------------------------------------
    // Order selection → Packing (groups by customer)
    // ---------------------------------------------------------------

    /**
     * Packs selected orders.  Orders belonging to the <b>same customer</b>
     * are merged into a single packing job — their items are combined
     * into one package.  Different customers get separate jobs.
     *
     * <p><b>MULTITHREADING:</b> Each job gets its own SwingWorker so
     * multiple customer packages can be packed concurrently.</p>
     *
     * @param orderIds the selected order IDs to pack
     * @return the number of jobs actually submitted
     */
    public int packSelectedOrders(List<String> orderIds) {
        // 1. Resolve orders and group them by customer ID
        Map<String, List<Order>> byCustomer = new LinkedHashMap<>();

        for (String orderId : orderIds) {
            Order order = model.getOrder(orderId);

            if (order == null) {
                model.getExceptionDispatcher().dispatch(159, "MAJOR", "Packing",
                        "Order " + orderId + " not found in database.");
                model.publishStatus("⚠ Order " + orderId + " not found — skipping.");
                continue;
            }

            if (order.isPacked()) {
                model.publishStatus("⚠ Order " + orderId + " is already packed — skipping.");
                continue;
            }

            byCustomer.computeIfAbsent(order.getCustomerId(), k -> new ArrayList<>())
                       .add(order);
        }

        // 2. For each customer, merge items from all their orders into one job
        int submitted = 0;

        for (Map.Entry<String, List<Order>> entry : byCustomer.entrySet()) {
            List<Order> customerOrders = entry.getValue();
            String customerName = customerOrders.get(0).getCustomerName();

            // Build a combined order-ID string (e.g. "ORD-1001, ORD-1002")
            String combinedOrderId = customerOrders.stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.joining(", "));

            // Merge all items from all orders
            List<PackingItem> allItems = new ArrayList<>();
            for (Order order : customerOrders) {
                allItems.addAll(order.getItems());
            }

            // Create one packing job per customer
            String jobId = "PKJ-" + String.format("%04d", jobIdCounter.getAndIncrement());
            PackingJob job = new PackingJob(jobId, combinedOrderId, allItems);
            model.addJob(job);

            // -----------------------------------------------------------
            // FACTORY METHOD + STRATEGY: The factory inspects the merged
            // item list and picks the correct packing strategy.
            // -----------------------------------------------------------
            IPackingStrategy strategy = strategyFactory.createStrategy(job);

            // -----------------------------------------------------------
            // MULTITHREADING: Start a SwingWorker for this customer's job.
            // Multiple customers' jobs run concurrently.
            // -----------------------------------------------------------
            PackingWorker worker = new PackingWorker(job, model, strategy);
            worker.execute();

            // Mark all source orders as packed
            for (Order order : customerOrders) {
                model.markOrderPacked(order.getOrderId());
            }

            int orderCount = customerOrders.size();
            model.publishStatus("Job " + jobId + " submitted for " + customerName
                    + " (" + orderCount + (orderCount == 1 ? " order" : " orders")
                    + ", " + allItems.size() + " items, strategy: "
                    + strategy.getStrategyName() + ")");
            submitted++;
        }

        return submitted;
    }

    /**
     * Unpacks selected packed jobs so their source orders can be packed again.
     *
     * @param jobIds job IDs selected in the UI
     * @return number of jobs successfully unpacked
     */
    public int unpackSelectedJobs(List<String> jobIds) {
        int unpacked = 0;

        for (String jobId : jobIds) {
            PackingJob job = model.getJob(jobId);
            if (job == null) {
                model.publishStatus("⚠ Job " + jobId + " not found — skipping.");
                continue;
            }
            if (job.getStatus() != PackingJobStatus.PACKED) {
                model.publishStatus("⚠ Job " + jobId + " is not packed — skipping.");
                continue;
            }

            if (model.unpackJob(jobId)) {
                unpacked++;
            }
        }

        return unpacked;
    }

    // ---------------------------------------------------------------
    // Pallet creation (Unitization)
    // ---------------------------------------------------------------

    /**
     * Groups the specified packed jobs into a pallet.
     *
     * <p><b>UNITIZATION:</b> Groups individually packed customer orders
     * into a pallet for easier warehouse handling and shipping.</p>
     *
     * @param jobIds the packed job IDs to group onto a pallet
     * @return the created pallet, or {@code null} if no valid jobs
     */
    public PackingUnit createPallet(List<String> jobIds) {
        String unitId = model.nextUnitId();
        PackingUnit pallet = new PackingUnit(unitId);

        for (String jobId : jobIds) {
            PackingJob job = model.getJob(jobId);
            if (job == null) {
                model.publishStatus("⚠ Job " + jobId + " not found — skipping.");
                continue;
            }
            if (job.getStatus() != PackingJobStatus.PACKED) {
                model.publishStatus("⚠ Job " + jobId + " is not packed yet — skipping.");
                continue;
            }
            if (!pallet.addJob(job)) {
                model.publishStatus("⚠ Pallet " + unitId + " is full — cannot add " + jobId + ".");
                break;
            }
        }

        if (pallet.getCurrentSize() == 0) {
            model.publishStatus("⚠ No valid packed jobs to palletize.");
            return null;
        }

        model.addUnit(pallet);
        return pallet;
    }

    // ---------------------------------------------------------------
    // Startup
    // ---------------------------------------------------------------

    /**
     * Adds packed jobs to an existing pallet.
     *
     * @param palletId the target pallet ID
     * @param jobIds   the packed job IDs to add
     * @return number of jobs successfully added
     */
    public int addToPallet(String palletId, List<String> jobIds) {
        PackingUnit pallet = model.getUnit(palletId);
        if (pallet == null) {
            model.publishStatus("⚠ Pallet " + palletId + " not found.");
            return 0;
        }

        int added = 0;
        for (String jobId : jobIds) {
            PackingJob job = model.getJob(jobId);
            if (job == null) {
                model.publishStatus("⚠ Job " + jobId + " not found — skipping.");
                continue;
            }
            if (job.getStatus() != PackingJobStatus.PACKED) {
                model.publishStatus("⚠ Job " + jobId + " is not packed — skipping.");
                continue;
            }
            if (pallet.containsJob(jobId)) {
                model.publishStatus("⚠ Job " + jobId + " is already on this pallet.");
                continue;
            }
            if (!pallet.addJob(job)) {
                model.publishStatus("⚠ Pallet " + palletId + " is full.");
                break;
            }
            added++;
        }

        if (added > 0) {
            model.persistUnits();
            model.publishStatus("📦 Added " + added + " job(s) to Pallet " + palletId
                    + " — now " + pallet.getCurrentSize() + "/" + pallet.getMaxCapacity());
        }
        return added;
    }

    /**
     * Removes a job from an existing pallet.
     *
     * @param palletId the pallet to remove from
     * @param jobId    the job to remove
     * @return {@code true} if the job was removed
     */
    public boolean removeFromPallet(String palletId, String jobId) {
        PackingUnit pallet = model.getUnit(palletId);
        if (pallet == null) {
            model.publishStatus("⚠ Pallet " + palletId + " not found.");
            return false;
        }
        boolean removed = pallet.removeJob(jobId);
        if (removed) {
            model.persistUnits();
            model.publishStatus("📦 Removed job " + jobId + " from Pallet " + palletId
                    + " — now " + pallet.getCurrentSize() + "/" + pallet.getMaxCapacity());
        } else {
            model.publishStatus("⚠ Job " + jobId + " was not on Pallet " + palletId + ".");
        }
        return removed;
    }

    /**
     * Loads orders and any previously persisted jobs.
     */
    public void loadInitialData() {
        model.loadOrders();
        model.loadFromDatabase();
        syncJobCounterFromLoadedJobs();
    }

    private void syncJobCounterFromLoadedJobs() {
        int maxSeen = 0;

        for (PackingJob job : model.getAllJobs()) {
            Matcher matcher = PACKING_JOB_ID_PATTERN.matcher(job.getJobId());
            if (!matcher.matches()) {
                continue;
            }

            int sequence = Integer.parseInt(matcher.group(1));
            if (sequence > maxSeen) {
                maxSeen = sequence;
            }
        }

        int next = Math.max(1, maxSeen + 1);
        jobIdCounter.set(next);

        if (maxSeen > 0) {
            model.publishStatus("Continuing job IDs from PKJ-"
                    + String.format("%04d", maxSeen) + "; next is PKJ-"
                    + String.format("%04d", next) + ".");
        }
    }
}
