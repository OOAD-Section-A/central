package com.scm.packing.integration.warehouse;

import com.scm.packing.mvc.model.PackingItem;

import java.util.List;

/**
 * Integration contract for Warehouse Management System consumers.
 */
public interface IWarehousePackingIntegration {

    String createPackingJob(PackingRequest request);

    PackingStatusResponse getPackingStatus(String jobId);

    List<PackingItem> getPackedItems(String jobId);
}
