package com.scm.packing.worker;

import com.scm.packing.integration.exceptions.IExceptionDispatcher;
import com.scm.packing.mvc.model.*;
import com.scm.packing.strategy.IPackingStrategy;

import javax.swing.SwingWorker;
import java.util.List;

/**
 * Background worker that processes a single {@link PackingJob}.
 *
 * <p><b>Multithreading — SwingWorker:</b> All heavy work runs on a pooled
 * background thread via {@link #doInBackground()}.  Intermediate progress
 * strings are published to the EDT via {@link #publish} and
 * {@link #process}, keeping the UI responsive.</p>
 *
 * <p>When the worker finishes, {@link #done()} updates the job's final
 * status, generates a {@link BarcodeLabel} for traceability, and notifies
 * the model's observers.</p>
 *
 * <p><b>Design Pattern – Strategy (Behavioral):</b> The packing algorithm
 * is delegated to an {@link IPackingStrategy} — the worker does not know
 * or care which strategy it uses.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Coordinate background execution
 * of a packing strategy and barcode generation — nothing else.</p>
 */
public class PackingWorker extends SwingWorker<Boolean, String> {

    private final PackingJob job;
    private final PackingModel model;
    private final IPackingStrategy strategy;
    private final IExceptionDispatcher exceptionDispatcher;

    public PackingWorker(PackingJob job, PackingModel model, IPackingStrategy strategy) {
        this.job = job;
        this.model = model;
        this.strategy = strategy;
        this.exceptionDispatcher = model.getExceptionDispatcher();
    }

    /**
     * Runs on a <b>background thread</b>.  Iterates over every item,
     * delegating to the strategy, and publishes progress.
     *
     * <p><b>MULTITHREADING:</b> Mutable fields on {@link PackingJob}
     * ({@code status}, {@code progress}) are {@code volatile}, so updates
     * are visible to the EDT without explicit synchronization.</p>
     */
    @Override
    protected Boolean doInBackground() {
        List<PackingItem> items = job.getItems();
        int total = items.size();

        // Transition to PACKING
        job.setStatus(PackingJobStatus.PACKING);
        model.updateJob(job);

        publish("▶ Started packing job " + job.getJobId()
                + " (order: " + job.getOrderId() + ")"
                + " using [" + strategy.getStrategyName() + "] strategy"
                + " (" + total + " items)");

        for (int i = 0; i < total; i++) {
            if (isCancelled()) {
                publish("⚠ Job " + job.getJobId() + " was cancelled.");
                return false;
            }

            PackingItem item = items.get(i);

            try {
                // -----------------------------------------------------------
                // STRATEGY PATTERN: Delegate actual packing to the strategy.
                // -----------------------------------------------------------
                String log = strategy.packItem(item, i, total);
                publish(log);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                publish("⚠ Interrupted while packing item " + item.getItemId());
                return false;

            } catch (Exception e) {
                // Exception 159: ITEM_NOT_AVAILABLE_FOR_PACKING (MAJOR)
                exceptionDispatcher.dispatch(159, "MAJOR", "Packing",
                        "Item " + item.getItemId() + " failed: " + e.getMessage());
                publish("✗ ERROR packing item " + item.getItemId() + ": " + e.getMessage());
                return false;
            }

            // Update progress (0–100)
            int pct = (int) (((i + 1) / (double) total) * 100);
            job.setProgress(pct);
            setProgress(pct);
            model.updateJob(job);
        }

        return true;
    }

    /**
     * Runs on the <b>EDT</b>.  Receives log messages from the background
     * thread and pushes them to the model's status channel.
     */
    @Override
    protected void process(List<String> chunks) {
        for (String msg : chunks) {
            model.publishStatus(msg);
        }
    }

    /**
     * Runs on the <b>EDT</b> after completion.  Sets final status and
     * generates a barcode label for traceability.
     */
    @Override
    protected void done() {
        try {
            boolean success = get();

            if (success) {
                job.setStatus(PackingJobStatus.PACKED);
                job.setProgress(100);

                // -----------------------------------------------------------
                // LABELING & TRACEABILITY: Generate a barcode for tracking.
                // -----------------------------------------------------------
                BarcodeLabel label = new BarcodeLabel(job.getJobId(), job.getOrderId());
                model.addBarcode(label);

                // Mark the source order as packed
                model.markOrderPacked(job.getOrderId());

                model.publishStatus("✓ Job " + job.getJobId() + " packed successfully.");
            } else {
                job.setStatus(PackingJobStatus.FAILED);
                model.publishStatus("✗ Job " + job.getJobId() + " failed.");
            }
        } catch (Exception e) {
            job.setStatus(PackingJobStatus.FAILED);
            // Exception 209: REPAIR_EXECUTION_FAILED (MAJOR)
            exceptionDispatcher.dispatch(209, "MAJOR", "Packing",
                    "Unexpected error completing job " + job.getJobId() + ": " + e.getMessage());
            model.publishStatus("✗ Job " + job.getJobId() + " failed with exception.");
        }

        model.updateJob(job);
    }
}
