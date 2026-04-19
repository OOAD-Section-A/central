package com.scm.packing.mvc.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Domain model representing a single packing job — an order that needs to be
 * physically packed at the warehouse packing station.
 *
 * <p><b>GRASP – Information Expert:</b> The job owns its identifier, order
 * reference, item list, status, and progress percentage.  All queries about
 * "what needs to be packed?" are answered by this class.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class holds job state only.
 * It does not decide <i>how</i> to pack (that is the Strategy) or <i>where</i>
 * to persist (that is the Database layer).</p>
 *
 * <p><b>Multithreading note:</b> {@code items} is backed by a
 * {@link CopyOnWriteArrayList} so it can be read safely from the EDT while a
 * worker thread updates progress.  The mutable fields {@code status} and
 * {@code progress} are declared {@code volatile} to ensure visibility across
 * threads without heavy locking.</p>
 */
public class PackingJob {

    private final String jobId;
    private final String orderId;
    private final List<PackingItem> items;

    /** Volatile – written by worker threads, read by EDT for table rendering. */
    private volatile PackingJobStatus status;

    /** Volatile – 0–100 progress percentage updated by the worker thread. */
    private volatile int progress;

    /**
     * Creates a new packing job in {@link PackingJobStatus#PENDING} state.
     *
     * @param jobId   unique job identifier
     * @param orderId the order this job fulfils
     * @param items   the items to pack
     */
    public PackingJob(String jobId, String orderId, List<PackingItem> items) {
        this.jobId   = jobId;
        this.orderId = orderId;
        // Thread-safe list: worker thread may iterate while EDT reads for display
        this.items   = new CopyOnWriteArrayList<>(items);
        this.status  = PackingJobStatus.PENDING;
        this.progress = 0;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getJobId()               { return jobId; }
    public String getOrderId()             { return orderId; }
    public List<PackingItem> getItems()    { return Collections.unmodifiableList(items); }
    public PackingJobStatus getStatus()    { return status; }
    public int getProgress()               { return progress; }

    // ---------------------------------------------------------------
    // Mutators (called from worker / controller threads)
    // ---------------------------------------------------------------

    /**
     * Transitions the job to a new status.
     *
     * @param status the new {@link PackingJobStatus}
     */
    public void setStatus(PackingJobStatus status) {
        this.status = status;
    }

    /**
     * Updates the progress percentage (0–100).
     *
     * @param progress clamped internally to [0, 100]
     */
    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
    }

    @Override
    public String toString() {
        return String.format("PackingJob[%s, order=%s, status=%s, progress=%d%%]",
                jobId, orderId, status.getDisplayLabel(), progress);
    }
}
