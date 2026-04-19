package com.scm.packing.mvc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Domain model representing a barcode label generated during the packing
 * process for <b>Labeling &amp; Traceability</b>.
 *
 * <p>Each packed job receives a unique barcode that encodes the job ID,
 * order ID, and a timestamp.  This barcode is used for real-time tracking
 * and identification throughout the supply chain.</p>
 *
 * <p><b>GRASP – Information Expert:</b> The label knows how to produce
 * its encoded string and human-readable summary.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> Pure data + encoding logic.</p>
 */
public class BarcodeLabel {

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final String jobId;
    private final String orderId;
    private final LocalDateTime generatedAt;
    private final String barcodeString;

    /**
     * Creates a barcode label for a completed packing job.
     *
     * @param jobId   the packing job ID
     * @param orderId the corresponding order ID
     */
    public BarcodeLabel(String jobId, String orderId) {
        this.jobId        = jobId;
        this.orderId      = orderId;
        this.generatedAt  = LocalDateTime.now();
        // Encode the barcode string: JOB|ORDER|TIMESTAMP
        this.barcodeString = String.format("SCM|%s|%s|%s",
                jobId, orderId, generatedAt.format(TS_FMT));
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getJobId()           { return jobId; }
    public String getOrderId()         { return orderId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    /**
     * Returns the raw barcode-encoded string suitable for printing as
     * a Code-128 barcode.
     */
    public String getBarcodeString()   { return barcodeString; }

    /**
     * Returns a human-readable summary for the activity log.
     */
    public String toLogString() {
        return String.format("Barcode generated: %s  (job=%s, order=%s, time=%s)",
                barcodeString, jobId, orderId, generatedAt.format(TS_FMT));
    }
}
