# Delivery Integration Guide

This package exposes packing objects for the Delivery subsystem.

## Contract

Delivery should depend on the `IDeliveryPackingGateway` interface and use `DeliveryPackingGateway` as the default implementation.

## Entry Points

Use `DeliveryPackingGateway`:

- `getPackedJobs()`
- `getPackedJob(String jobId)`
- `getBarcodeForJob(String jobId)`
- `getAllPackingUnits()`

This allows Delivery to pull all packed jobs, inspect individual jobs, fetch barcode labels, and read palletized unit data.

## Minimal Usage

```java
DeliveryPackingGateway deliveryGateway = new DeliveryPackingGateway(packingModel);

List<PackingJob> readyJobs = deliveryGateway.getPackedJobs();
Optional<PackingJob> oneJob = deliveryGateway.getPackedJob("WMS-PKJ-0001");
Optional<BarcodeLabel> barcode = deliveryGateway.getBarcodeForJob("WMS-PKJ-0001");
List<PackingUnit> units = deliveryGateway.getAllPackingUnits();
```

## JAR Integration Option

Build the subsystem JAR:

```bash
mvn clean package
```

Use one of these artifacts in consuming subsystem classpaths:

- `target/packing-subsystem-1.0-SNAPSHOT.jar`
- `target/packing-subsystem-1.0-SNAPSHOT-all.jar` (includes dependencies)
