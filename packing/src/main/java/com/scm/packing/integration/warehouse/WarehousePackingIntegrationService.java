package com.scm.packing.integration.warehouse;

import com.scm.packing.mvc.model.PackingItem;
import com.scm.packing.mvc.model.PackingJob;
import com.scm.packing.mvc.model.PackingJobStatus;
import com.scm.packing.mvc.model.PackingModel;
import com.scm.packing.strategy.IPackingStrategy;
import com.scm.packing.strategy.PackingStrategyFactory;
import com.scm.packing.worker.PackingWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WMS integration facade that exposes packing lifecycle entry points.
 */
public class WarehousePackingIntegrationService implements IWarehousePackingIntegration {

    private static final String WMS_JOB_PREFIX = "WMS-PKJ-";

    private final PackingModel model;
    private final PackingStrategyFactory strategyFactory;
    private final AtomicInteger jobIdCounter = new AtomicInteger(1);

    public WarehousePackingIntegrationService(PackingModel model, PackingStrategyFactory strategyFactory) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.strategyFactory = Objects.requireNonNull(strategyFactory, "strategyFactory must not be null");
    }

    /**
     * Creates and asynchronously starts a new packing job from a WMS request.
     *
     * @param request WMS payload containing order ID and items
     * @return generated packing job ID
     */
    @Override
    public String createPackingJob(PackingRequest request) {
        validateRequest(request);

        String jobId = WMS_JOB_PREFIX + String.format("%04d", jobIdCounter.getAndIncrement());
        List<PackingItem> items = toPackingItems(request.getItems());

        PackingJob job = new PackingJob(jobId, request.getOrderId(), items);
        model.addJob(job);

        IPackingStrategy strategy = strategyFactory.createStrategy(job);
        new PackingWorker(job, model, strategy).execute();

        model.publishStatus("WMS submitted packing job " + jobId + " for order " + request.getOrderId());
        return jobId;
    }

    /**
     * Returns current status and progress for a packing job.
     */
    @Override
    public PackingStatusResponse getPackingStatus(String jobId) {
        PackingJob job = model.getJob(jobId);
        if (job == null) {
            return new PackingStatusResponse(jobId, "NOT_FOUND", 0);
        }
        return new PackingStatusResponse(job.getJobId(), job.getStatus().name(), job.getProgress());
    }

    /**
     * Returns packed items if the job has completed successfully.
     */
    @Override
    public List<PackingItem> getPackedItems(String jobId) {
        PackingJob job = model.getJob(jobId);
        if (job == null || job.getStatus() != PackingJobStatus.PACKED) {
            return Collections.emptyList();
        }
        return new ArrayList<>(job.getItems());
    }

    private void validateRequest(PackingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.getOrderId() == null || request.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("request.orderId must not be blank");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("request.items must not be empty");
        }
    }

    private List<PackingItem> toPackingItems(List<PackingRequestItem> requestItems) {
        List<PackingItem> items = new ArrayList<>(requestItems.size());
        for (PackingRequestItem requestItem : requestItems) {
            items.add(new PackingItem(
                    requestItem.getItemId(),
                    requestItem.getDescription(),
                    requestItem.getWeightKg(),
                    requestItem.isFragile()));
        }
        return items;
    }
}
