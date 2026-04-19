package com.scm.packing.integration.delivery;

import com.scm.packing.mvc.model.BarcodeLabel;
import com.scm.packing.mvc.model.PackingJob;
import com.scm.packing.mvc.model.PackingJobStatus;
import com.scm.packing.mvc.model.PackingModel;
import com.scm.packing.mvc.model.PackingUnit;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Delivery-facing integration gateway for retrieving packing objects.
 */
public class DeliveryPackingGateway implements IDeliveryPackingGateway {

    private final PackingModel model;

    public DeliveryPackingGateway(PackingModel model) {
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    /**
     * Returns jobs that are packed and ready for delivery pickup.
     */
    public List<PackingJob> getPackedJobs() {
        return model.getAllJobs().stream()
                .filter(job -> job.getStatus() == PackingJobStatus.PACKED)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns a single packed job if available.
     */
    public Optional<PackingJob> getPackedJob(String jobId) {
        PackingJob job = model.getJob(jobId);
        if (job == null || job.getStatus() != PackingJobStatus.PACKED) {
            return Optional.empty();
        }
        return Optional.of(job);
    }

    /**
     * Returns the generated barcode for a packed job.
     */
    public Optional<BarcodeLabel> getBarcodeForJob(String jobId) {
        return Optional.ofNullable(model.getBarcode(jobId));
    }

    /**
     * Returns all created packing pallets/units.
     */
    public List<PackingUnit> getAllPackingUnits() {
        return model.getAllUnits();
    }
}
