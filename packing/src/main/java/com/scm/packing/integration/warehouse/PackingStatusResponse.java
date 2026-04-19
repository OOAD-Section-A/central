package com.scm.packing.integration.warehouse;

/**
 * Status payload returned to WMS clients.
 */
public class PackingStatusResponse {

    private final String jobId;
    private final String status;
    private final int progress;

    public PackingStatusResponse(String jobId, String status, int progress) {
        this.jobId = jobId;
        this.status = status;
        this.progress = progress;
    }

    public String getJobId() {
        return jobId;
    }

    public String getStatus() {
        return status;
    }

    public int getProgress() {
        return progress;
    }
}
