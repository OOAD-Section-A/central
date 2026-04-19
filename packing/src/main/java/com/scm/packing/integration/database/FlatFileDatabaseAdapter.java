package com.scm.packing.integration.database;

import com.scm.packing.mvc.model.Order;
import com.scm.packing.mvc.model.PackingItem;
import com.scm.packing.mvc.model.PackingJob;
import com.scm.packing.mvc.model.PackingJobStatus;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flat-file fallback implementation of {@link IDatabaseLayer}.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This class adapts a
 * simple CSV-based flat file to the {@link IDatabaseLayer} contract so
 * that the application can run <i>without</i> the external SCM MySQL
 * database.</p>
 *
 * <p><b>Fallback behaviour:</b> Data is written to a temporary file
 * ({@code packing_data.tmp}) in the working directory.  A JVM shutdown
 * hook deletes the file automatically so that no stale data survives
 * across sessions (as required by the specification).</p>
 *
 * <p><b>Seed data:</b> On construction, the adapter populates itself with
 * realistic sample orders mimicking what the SCM {@code orders} and
 * {@code order_items} tables would contain.  This lets the UI demonstrate
 * order selection without a live database.</p>
 *
 * <p><b>Multithreading:</b> An internal {@link ConcurrentHashMap} ensures
 * that concurrent reads (EDT) and writes (worker threads) do not corrupt
 * state.  File I/O is synchronised on the file path object to prevent
 * interleaved writes.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class is solely responsible
 * for flat-file persistence; business logic lives elsewhere.</p>
 */
public class FlatFileDatabaseAdapter implements IDatabaseLayer {

    /** On-disk file used for serialisation. */
    private static final String FILE_NAME = "packing_data.tmp";
    private final Path filePath;

    /**
     * In-memory mirror of persisted jobs.
     * ConcurrentHashMap → safe reads from EDT, safe writes from workers.
     */
    private final Map<String, PackingJob> jobMap = new ConcurrentHashMap<>();

    /**
     * In-memory store of seed orders.
     * ConcurrentHashMap → safe reads from EDT during order selection.
     */
    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    /**
     * Creates the adapter, seeds sample orders, and registers a JVM
     * shutdown hook that wipes the temporary file on exit.
     */
    public FlatFileDatabaseAdapter() {
        this.filePath = Paths.get(FILE_NAME);

        // -----------------------------------------------------------
        // MULTITHREADING / LIFECYCLE: Shutdown hook runs on a dedicated
        // thread when the JVM exits, cleaning up the temp file.
        // -----------------------------------------------------------
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(filePath);
                System.out.println("[FlatFileDB] Temporary data file deleted on shutdown.");
            } catch (IOException e) {
                System.err.println("[FlatFileDB] Could not delete temp file: " + e.getMessage());
            }
        }, "FlatFileDB-Cleanup"));

        // Load any residual data (shouldn't exist, but defensive)
        loadFromDisk();

        // Populate seed orders for the demo
        seedOrders();
    }

    // ---------------------------------------------------------------
    // Seed data — realistic SCM orders
    // ---------------------------------------------------------------

    /**
     * Populates the order store with sample data mimicking the SCM
     * {@code orders} and {@code order_items} tables.
     *
     * <p>This allows the packing UI to demonstrate full order-selection
     * workflows without needing a live MySQL connection.</p>
     */
    private void seedOrders() {
        // Customer: Priya Sharma — two orders (no fragile items)
        orderMap.put("ORD-1001", new Order("ORD-1001", "CUST-101", "Priya Sharma", Arrays.asList(
                new PackingItem("SKU-A001", "Wireless Bluetooth Headphones", 0.35, false),
                new PackingItem("SKU-A002", "USB-C Charging Cable (2m)", 0.08, false),
                new PackingItem("SKU-A003", "Silicone Phone Case", 0.05, false)
        )));
        orderMap.put("ORD-1002", new Order("ORD-1002", "CUST-101", "Priya Sharma", Arrays.asList(
                new PackingItem("SKU-B001", "Ceramic Coffee Mug", 0.45, false),
                new PackingItem("SKU-B002", "Stainless Steel Spoon Set (6pc)", 0.60, false)
        )));

        // Customer: Rahul Verma — one order (no fragile items)
        orderMap.put("ORD-1003", new Order("ORD-1003", "CUST-102", "Rahul Verma", Arrays.asList(
                new PackingItem("SKU-C001", "Cotton Polo T-Shirt (L)", 0.30, false),
                new PackingItem("SKU-C002", "Denim Jeans (32W)", 0.70, false),
                new PackingItem("SKU-C003", "Leather Belt (Black)", 0.25, false),
                new PackingItem("SKU-C004", "Running Shoes (UK 9)", 0.85, false)
        )));

        // Customer: Ananya Iyer — one order with FRAGILE items (glassware)
        orderMap.put("ORD-1004", new Order("ORD-1004", "CUST-103", "Ananya Iyer", Arrays.asList(
                new PackingItem("SKU-D001", "Crystal Wine Glass Set (4pc)", 1.80, true),
                new PackingItem("SKU-D002", "Porcelain Dinner Plate Set (6pc)", 3.20, true),
                new PackingItem("SKU-D003", "Linen Table Runner", 0.40, false)
        )));

        // Customer: Vikram Desai — two orders (keyboard+accessories standard, monitor fragile)
        orderMap.put("ORD-1005", new Order("ORD-1005", "CUST-104", "Vikram Desai", Arrays.asList(
                new PackingItem("SKU-E001", "Mechanical Keyboard (Cherry MX)", 0.90, false),
                new PackingItem("SKU-E002", "Ergonomic Mouse Pad", 0.30, false)
        )));
        orderMap.put("ORD-1006", new Order("ORD-1006", "CUST-104", "Vikram Desai", Arrays.asList(
                new PackingItem("SKU-F001", "27\" IPS Monitor", 5.50, true),
                new PackingItem("SKU-F002", "HDMI Cable (3m)", 0.12, false),
                new PackingItem("SKU-F003", "Monitor Stand (Adjustable)", 2.30, false)
        )));

        // Customer: Meera Nair — one large order (no fragile items)
        orderMap.put("ORD-1007", new Order("ORD-1007", "CUST-105", "Meera Nair", Arrays.asList(
                new PackingItem("SKU-G001", "Yoga Mat (6mm, Purple)", 1.20, false),
                new PackingItem("SKU-G002", "Resistance Bands Set (5pc)", 0.45, false),
                new PackingItem("SKU-G003", "Foam Roller (45cm)", 0.60, false),
                new PackingItem("SKU-G004", "Water Bottle (750ml)", 0.35, false),
                new PackingItem("SKU-G005", "Gym Bag (Duffel)", 0.80, false)
        )));

        // Customer: Arjun Patel — one order (no fragile items)
        orderMap.put("ORD-1008", new Order("ORD-1008", "CUST-106", "Arjun Patel", Arrays.asList(
                new PackingItem("SKU-H001", "Hardcover Novel - The Midnight Library", 0.50, false),
                new PackingItem("SKU-H002", "Spiral Notebook A5 (200pg)", 0.35, false),
                new PackingItem("SKU-H003", "Fountain Pen (Fine Nib)", 0.04, false)
        )));

        System.out.println("[FlatFileDB] Seeded " + orderMap.size() + " sample orders.");
    }

    // ---------------------------------------------------------------
    // IDatabaseLayer — Order operations
    // ---------------------------------------------------------------

    @Override
    public List<Order> loadOrders() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public void updateOrder(Order order) {
        orderMap.put(order.getOrderId(), order);
    }

    // ---------------------------------------------------------------
    // IDatabaseLayer — Job operations
    // ---------------------------------------------------------------

    @Override
    public void saveJob(PackingJob job) {
        jobMap.put(job.getJobId(), job);
        flushToDisk();
    }

    @Override
    public void updateJob(PackingJob job) {
        jobMap.put(job.getJobId(), job);
        flushToDisk();
    }

    @Override
    public List<PackingJob> loadAllJobs() {
        return new ArrayList<>(jobMap.values());
    }

    @Override
    public void clearAll() {
        jobMap.clear();
        flushToDisk();
    }

    // ---------------------------------------------------------------
    // Private helpers — CSV serialisation
    // ---------------------------------------------------------------

    /**
     * Writes every job to disk as simple CSV lines.
     * Synchronised on {@code filePath} to prevent interleaved writes
     * from concurrent worker threads.
     */
    private void flushToDisk() {
        synchronized (filePath) {
            try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                for (PackingJob job : jobMap.values()) {
                    // Format: jobId|orderId|status|progress|item1;item2;...
                    StringBuilder itemStr = new StringBuilder();
                    for (PackingItem item : job.getItems()) {
                        if (itemStr.length() > 0) itemStr.append(";");
                        itemStr.append(String.format("%s~%s~%.2f~%b",
                                item.getItemId(), item.getDescription(),
                                item.getWeightKg(), item.isFragile()));
                    }
                    writer.write(String.format("%s|%s|%s|%d|%s%n",
                            job.getJobId(), job.getOrderId(),
                            job.getStatus().name(), job.getProgress(),
                            itemStr.toString()));
                }
            } catch (IOException e) {
                System.err.println("[FlatFileDB] Flush error: " + e.getMessage());
            }
        }
    }

    /**
     * Reads the flat file back into memory if it exists.
     */
    private void loadFromDisk() {
        if (!Files.exists(filePath)) return;

        synchronized (filePath) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 5) continue;

                    String jobId   = parts[0];
                    String orderId = parts[1];
                    PackingJobStatus status = PackingJobStatus.valueOf(parts[2]);
                    int progress   = Integer.parseInt(parts[3]);

                    List<PackingItem> items = new ArrayList<>();
                    if (!parts[4].isEmpty()) {
                        for (String itemToken : parts[4].split(";")) {
                            String[] ip = itemToken.split("~", -1);
                            if (ip.length >= 4) {
                                items.add(new PackingItem(
                                        ip[0], ip[1],
                                        Double.parseDouble(ip[2]),
                                        Boolean.parseBoolean(ip[3])));
                            }
                        }
                    }

                    PackingJob job = new PackingJob(jobId, orderId, items);
                    job.setStatus(status);
                    job.setProgress(progress);
                    jobMap.put(jobId, job);
                }
            } catch (IOException e) {
                System.err.println("[FlatFileDB] Load error: " + e.getMessage());
            }
        }
    }
}
