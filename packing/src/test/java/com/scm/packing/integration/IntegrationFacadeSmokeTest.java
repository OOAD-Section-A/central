package com.scm.packing.integration;

import com.scm.packing.integration.database.FlatFileDatabaseAdapter;
import com.scm.packing.integration.delivery.DeliveryPackingGateway;
import com.scm.packing.integration.exceptions.FallbackConsoleLogger;
import com.scm.packing.integration.warehouse.PackingRequest;
import com.scm.packing.integration.warehouse.PackingRequestItem;
import com.scm.packing.integration.warehouse.PackingStatusResponse;
import com.scm.packing.integration.warehouse.WarehousePackingIntegrationService;
import com.scm.packing.mvc.model.PackingItem;
import com.scm.packing.mvc.model.PackingModel;
import com.scm.packing.strategy.PackingStrategyFactory;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationFacadeSmokeTest {

    @Test
    void warehouseCreatePackingJobShouldReachPackedAndExposeItems() throws Exception {
        PackingModel model = new PackingModel(new FlatFileDatabaseAdapter(), new FallbackConsoleLogger());
        WarehousePackingIntegrationService warehouse =
                new WarehousePackingIntegrationService(model, new PackingStrategyFactory());

        PackingRequest request = new PackingRequest(
                "ORD-INT-001",
                List.of(new PackingRequestItem("SKU-INT-1", "Integration Test Item", 0.5, false))
        );

        String jobId = warehouse.createPackingJob(request);
        PackingStatusResponse finalStatus = waitForCompletion(warehouse, jobId, Duration.ofSeconds(8));

        assertEquals("PACKED", finalStatus.getStatus());
        List<PackingItem> packedItems = warehouse.getPackedItems(jobId);
        assertEquals(1, packedItems.size());
        assertEquals("SKU-INT-1", packedItems.get(0).getItemId());
    }

    @Test
    void deliveryGatewayShouldExposePackedObjects() throws Exception {
        PackingModel model = new PackingModel(new FlatFileDatabaseAdapter(), new FallbackConsoleLogger());
        WarehousePackingIntegrationService warehouse =
                new WarehousePackingIntegrationService(model, new PackingStrategyFactory());
        DeliveryPackingGateway delivery = new DeliveryPackingGateway(model);

        String jobId = warehouse.createPackingJob(new PackingRequest(
                "ORD-INT-002",
                List.of(new PackingRequestItem("SKU-INT-2", "Delivery Integration Item", 0.3, false))
        ));

        PackingStatusResponse finalStatus = waitForCompletion(warehouse, jobId, Duration.ofSeconds(8));
        assertEquals("PACKED", finalStatus.getStatus());

        assertTrue(delivery.getPackedJob(jobId).isPresent());
        assertTrue(delivery.getBarcodeForJob(jobId).isPresent());
        assertTrue(delivery.getPackedJobs().stream().anyMatch(job -> jobId.equals(job.getJobId())));
    }

    private PackingStatusResponse waitForCompletion(
            WarehousePackingIntegrationService warehouse,
            String jobId,
            Duration timeout) throws InterruptedException {

        Instant deadline = Instant.now().plus(timeout);
        PackingStatusResponse status = warehouse.getPackingStatus(jobId);

        while (Instant.now().isBefore(deadline)) {
            status = warehouse.getPackingStatus(jobId);
            if ("PACKED".equals(status.getStatus()) || "FAILED".equals(status.getStatus())) {
                return status;
            }
            Thread.sleep(100);
        }

        fail("Timed out waiting for packing completion for job " + jobId + ". Last status=" + status.getStatus());
        return status;
    }
}
