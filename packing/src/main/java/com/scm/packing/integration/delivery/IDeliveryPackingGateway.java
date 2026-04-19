package com.scm.packing.integration.delivery;

import com.scm.packing.mvc.model.BarcodeLabel;
import com.scm.packing.mvc.model.PackingJob;
import com.scm.packing.mvc.model.PackingUnit;

import java.util.List;
import java.util.Optional;

/**
 * Delivery-facing integration contract for consuming packed artifacts.
 */
public interface IDeliveryPackingGateway {

    List<PackingJob> getPackedJobs();

    Optional<PackingJob> getPackedJob(String jobId);

    Optional<BarcodeLabel> getBarcodeForJob(String jobId);

    List<PackingUnit> getAllPackingUnits();
}
