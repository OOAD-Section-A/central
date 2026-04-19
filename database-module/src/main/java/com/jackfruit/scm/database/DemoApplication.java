package com.jackfruit.scm.database;

import com.jackfruit.scm.database.adapter.BarcodeReaderAdapter;
import com.jackfruit.scm.database.adapter.DeliveryTrackingAdapter;
import com.jackfruit.scm.database.adapter.InventoryAdapter;
import com.jackfruit.scm.database.adapter.OrderAdapter;
import com.jackfruit.scm.database.adapter.SalesAdapter;
import com.jackfruit.scm.database.config.SchemaBootstrapper;
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.OrderItem;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.Shipment;
import com.jackfruit.scm.database.model.Warehouse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DemoApplication {

    public static void main(String[] args) {
        SchemaBootstrapper.ensureSchemaInitialized();
        try (SupplyChainDatabaseFacade facade = new SupplyChainDatabaseFacade()) {
            String runSuffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
            String warehouseId = "WH-" + runSuffix;
            String orderId = "ORD-" + runSuffix;
            String orderItemId = "ITEM-" + runSuffix;
            String priceId = "PRICE-" + runSuffix;
            String barcodeEventId = "BAR-" + runSuffix;
            String forecastId = "DF-" + runSuffix;
            String shipmentId = "SHIP-" + runSuffix;
            InventoryAdapter inventoryAdapter = new InventoryAdapter(facade);
            SalesAdapter salesAdapter = new SalesAdapter(facade);
            OrderAdapter orderAdapter = new OrderAdapter(facade);
            BarcodeReaderAdapter barcodeReaderAdapter = new BarcodeReaderAdapter(facade);
            DeliveryTrackingAdapter deliveryTrackingAdapter = new DeliveryTrackingAdapter(facade);
            inventoryAdapter.syncWarehouse(new Warehouse(warehouseId, "Central Warehouse " + runSuffix));

            orderAdapter.createOrder(new Order(
                    orderId,
                    "CUST-77",
                    "CONFIRMED",
                    LocalDateTime.now(),
                    new BigDecimal("420.00"),
                    "PAID",
                    "ONLINE"));

            orderAdapter.addOrderItem(new OrderItem(
                    orderItemId,
                    orderId,
                    "PROD-APPLE-001",
                    3,
                    new BigDecimal("140.00"),
                    new BigDecimal("420.00")));

            salesAdapter.publishPrice(new PriceList(
                    priceId,
                    "SKU-APPLE-001",
                    "SOUTH",
                    "RETAIL",
                    "RETAIL",
                    new BigDecimal("120.00"),
                    new BigDecimal("100.00"),
                    "INR",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(30),
                    "ACTIVE"));

            barcodeReaderAdapter.recordScan(new BarcodeRfidEvent(
                    barcodeEventId,
                    "PROD-APPLE-001",
                    "SCAN_IN",
                    "RFID_GATE_A1",
                    warehouseId,
                    LocalDateTime.now(),
                    "{\"tag\":\"RFID-7788\"}"));

            facade.recordDemandForecast(new DemandForecast(
                    forecastId,
                    "PROD-APPLE-001",
                    "2026-Q2",
                    850,
                    new BigDecimal("92.50"),
                    LocalDateTime.now(),
                    barcodeEventId));

            deliveryTrackingAdapter.upsertShipment(new Shipment(
                    shipmentId,
                    orderId,
                    "CUST-77",
                    "Bengaluru, Karnataka",
                    "PENDING",
                    "STANDARD",
                    new BigDecimal("180.00"),
                    "AGENT-22",
                    warehouseId,
                    LocalDateTime.now(),
                    LocalDateTime.now()));

            System.out.println("Database module demo flow prepared successfully.");
        } catch (Exception ex) {
            Throwable rootCause = rootCauseOf(ex);
            System.out.println("Demo flow requires a reachable MySQL instance and the OOAD schema to be created first.");
            System.out.println("Top-level failure: " + ex.getMessage());
            if (rootCause != ex && rootCause.getMessage() != null) {
                System.out.println("Root cause: " + rootCause.getMessage());
            }
        }
    }

    private static Throwable rootCauseOf(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }
}
