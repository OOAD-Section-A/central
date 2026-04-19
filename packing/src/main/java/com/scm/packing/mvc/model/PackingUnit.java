package com.scm.packing.mvc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain model representing a <b>pallet</b> — a unitized group of packed
 * jobs grouped together for efficient warehouse handling and shipping.
 *
 * <p><b>Unitization</b> groups individually packed customer orders into
 * larger shipping units (pallets) for easier handling by manual labour
 * and automated warehouse systems.</p>
 *
 * <p><b>GRASP – Information Expert:</b> The pallet knows its constituent
 * jobs, its current and maximum capacity, and its total weight.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Manages membership of a
 * single pallet group — nothing else.</p>
 */
public class PackingUnit {

    private final String unitId;
    private final int maxCapacity;
    private final List<PackingJob> jobs;

    /** Default pallet capacity — number of packed jobs per pallet. */
    public static final int DEFAULT_CAPACITY = 20;

    /**
     * Creates a new pallet with a specified capacity.
     *
     * @param unitId      unique pallet identifier
     * @param maxCapacity maximum number of packed jobs this pallet can hold
     */
    public PackingUnit(String unitId, int maxCapacity) {
        this.unitId      = unitId;
        this.maxCapacity = maxCapacity;
        this.jobs        = new ArrayList<>();
    }

    /**
     * Creates a pallet with the default capacity ({@value #DEFAULT_CAPACITY}).
     */
    public PackingUnit(String unitId) {
        this(unitId, DEFAULT_CAPACITY);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getUnitId()               { return unitId; }
    public int getMaxCapacity()             { return maxCapacity; }
    public List<PackingJob> getJobs()       { return Collections.unmodifiableList(jobs); }
    public int getCurrentSize()             { return jobs.size(); }
    public boolean isFull()                 { return jobs.size() >= maxCapacity; }

    /**
     * Calculates the total weight of all items across all jobs on this pallet.
     */
    public double getTotalWeightKg() {
        return jobs.stream()
                .flatMap(j -> j.getItems().stream())
                .mapToDouble(PackingItem::getWeightKg)
                .sum();
    }

    /**
     * Returns a comma-separated list of job IDs on this pallet.
     */
    public String getJobIdsSummary() {
        StringBuilder sb = new StringBuilder();
        for (PackingJob job : jobs) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(job.getJobId());
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // Mutators
    // ---------------------------------------------------------------

    /**
     * Adds a packed job to this pallet.
     *
     * @param job the packed job to add
     * @return {@code true} if added, {@code false} if the pallet is full
     */
    public boolean addJob(PackingJob job) {
        if (isFull()) return false;
        if (containsJob(job.getJobId())) return false; // no duplicates
        jobs.add(job);
        return true;
    }

    /**
     * Removes a job from this pallet by job ID.
     *
     * @param jobId the ID of the job to remove
     * @return {@code true} if the job was found and removed
     */
    public boolean removeJob(String jobId) {
        return jobs.removeIf(j -> j.getJobId().equals(jobId));
    }

    /**
     * Checks whether a job is already on this pallet.
     */
    public boolean containsJob(String jobId) {
        return jobs.stream().anyMatch(j -> j.getJobId().equals(jobId));
    }

    @Override
    public String toString() {
        return String.format("Pallet[%s, %d/%d jobs, %.2f kg]",
                unitId, jobs.size(), maxCapacity, getTotalWeightKg());
    }
}
