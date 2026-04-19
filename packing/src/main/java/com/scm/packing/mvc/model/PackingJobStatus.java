package com.scm.packing.mvc.model;

/**
 * Enum representing the lifecycle status of a packing job.
 *
 * <p><b>GRASP – Information Expert:</b> The status values and their display labels
 * are owned by the type that knows them best — the enum itself.</p>
 *
 * <p><b>SOLID – Open/Closed:</b> New statuses can be added here without modifying
 * any class that switches on this enum (as long as a default branch exists).</p>
 */
public enum PackingJobStatus {

    /** Job is queued and waiting to be picked up by a worker thread. */
    PENDING("Pending"),

    /** Job is currently being processed on a background thread. */
    PACKING("Packing…"),

    /** Job has been successfully packed and is ready for dispatch. */
    PACKED("Packed"),

    /** Job encountered an error during packing (e.g. missing item, strategy failure). */
    FAILED("Failed");

    // ---------------------------------------------------------------
    // Instance state
    // ---------------------------------------------------------------

    private final String displayLabel;

    PackingJobStatus(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    /**
     * Human-readable label shown in the Swing UI table cells.
     *
     * @return a short user-friendly string for this status
     */
    public String getDisplayLabel() {
        return displayLabel;
    }
}
