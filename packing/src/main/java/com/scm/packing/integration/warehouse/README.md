# Warehouse Integration Guide

This package exposes entry points for Warehouse Management System (WMS) integration.

## Entry Points

Use `WarehousePackingIntegrationService`:

- `createPackingJob(PackingRequest request)`
- `getPackingStatus(String jobId)`
- `getPackedItems(String jobId)`

## Data Contract

`PackingRequest` contains:

- `orderId`
- `items` (`List<PackingRequestItem>`)

`PackingRequestItem` contains:

- `itemId`
- `description`
- `weightKg`
- `fragile`

## Minimal Usage

```java
WarehousePackingIntegrationService wms =
        new WarehousePackingIntegrationService(packingModel, strategyFactory);

PackingRequest request = new PackingRequest(
        "ORD-2001",
        List.of(
                new PackingRequestItem("SKU-1", "Phone", 0.45, false),
                new PackingRequestItem("SKU-2", "Glass Bottle", 0.75, true)
        )
);

String jobId = wms.createPackingJob(request);
PackingStatusResponse status = wms.getPackingStatus(jobId);
List<PackingItem> packedItems = wms.getPackedItems(jobId);
```

`getPackedItems` returns an empty list until the job reaches `PACKED` status.

## JAR Integration Option

Build the subsystem JAR:

```bash
mvn clean package
```

Use one of these artifacts in consuming subsystem classpaths:

- `target/packing-subsystem-1.0-SNAPSHOT.jar`
- `target/packing-subsystem-1.0-SNAPSHOT-all.jar` (includes dependencies)

For Maven-to-Maven integration, the preferred option is adding this subsystem as a dependency instead of manually copying JAR files.
