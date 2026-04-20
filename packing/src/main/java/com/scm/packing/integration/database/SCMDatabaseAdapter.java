package com.scm.packing.integration.database;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.InventoryModels;
import com.jackfruit.scm.database.model.PackagingModels;
import com.scm.packing.mvc.model.Order;
import com.scm.packing.mvc.model.PackingJob;
import com.scm.packing.mvc.model.PackingItem;
import com.scm.packing.mvc.model.PackingJobStatus;

import java.math.BigDecimal;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter that connects to the external SCM database via the shared
 * {@code SupplyChainDatabaseFacade}.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This class adapts the
 * external database module's API to the internal {@link IDatabaseLayer}
 * interface expected by the Packing subsystem.</p>
 *
 * <p><b>Integration note:</b> This class depends on the
 * {@code database-module} JAR being on the classpath.  If the JAR is
 * absent, the application should fall back to
 * {@link FlatFileDatabaseAdapter} instead.  The decision is made at
 * startup in {@link DatabaseLayerFactory}.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> The rest of the application
 * never imports this class directly — it only references
 * {@link IDatabaseLayer}.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class only translates
 * between {@link IDatabaseLayer} calls and the SCM facade methods.</p>
 */
public class SCMDatabaseAdapter implements IDatabaseLayer {

    private final SupplyChainDatabaseFacade facade;

    /**
     * Tracks package IDs already written in this JVM to avoid duplicate
     * inserts when the model publishes repeated update events.
     */
    private final Set<String> persistedPackageIds = ConcurrentHashMap.newKeySet();

    public SCMDatabaseAdapter() {
        this.facade = new SupplyChainDatabaseFacade();
        System.out.println("[SCMDatabaseAdapter] Connected to SupplyChainDatabaseFacade.");
    }

    // ---------------------------------------------------------------
    // Order operations
    // ---------------------------------------------------------------

    @Override
    public List<Order> loadOrders() {
        try {
            Map<String, String> productNames = loadProductNamesById();
            Set<String> packedOrderIds = loadPackedOrderIds();
            List<Order> results = new ArrayList<>();

            for (com.jackfruit.scm.database.model.Order dbOrder : facade.orders().listOrders()) {
                String orderId = safeText(dbOrder.getOrderId(), "UNKNOWN-ORDER");
                String customerId = safeText(dbOrder.getCustomerId(), "UNKNOWN-CUSTOMER");
                List<PackingItem> items = mapOrderItems(orderId, productNames);

                Order mapped = new Order(orderId, customerId, customerId, items);
                mapped.setPacked(packedOrderIds.contains(orderId));
                results.add(mapped);
            }

            return results;
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] loadOrders failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void updateOrder(Order order) {
        // The current facade does not expose an order-status update API.
        // Packed status is derived from persisted packaging jobs instead.
        System.out.println("[SCMDatabaseAdapter] updateOrder informational no-op: " + order.getOrderId());
    }

    // ---------------------------------------------------------------
    // Job operations
    // ---------------------------------------------------------------

    @Override
    public void saveJob(PackingJob job) {
        persistJobIfTerminal(job);
    }

    @Override
    public void updateJob(PackingJob job) {
        persistJobIfTerminal(job);
    }

    @Override
    public boolean deleteJob(PackingJob job) {
        List<String> orderIds = splitOrderIds(job.getOrderId());
        List<String> packageIds = new ArrayList<>();

        for (int i = 0; i < orderIds.size(); i++) {
            packageIds.add(buildPackageId(job.getJobId(), i));
        }

        if (packageIds.isEmpty()) {
            packageIds.add(job.getJobId());
        }

        int deletedRows = 0;

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);

            try {
                deletedRows += deletePackagingRowsByPackageId(connection, packageIds);

                // Fallback for legacy rows that might not use the expected package-id pattern.
                if (deletedRows == 0) {
                    deletedRows += deletePackagingRowsByOrderId(connection, orderIds);
                }

                connection.commit();
            } catch (SQLException deleteError) {
                connection.rollback();
                throw deleteError;
            }
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] Failed to delete job " + job.getJobId()
                    + ": " + e.getMessage());
            return false;
        }

        for (String packageId : packageIds) {
            persistedPackageIds.remove(packageId);
        }

        return deletedRows > 0;
    }

    @Override
    public List<PackingJob> loadAllJobs() {
        try {
            Map<String, String> productNames = loadProductNamesById();
            List<PackingJob> jobs = new ArrayList<>();

            for (PackagingModels.PackagingJob dbJob : facade.packaging().listPackagingJobs()) {
                String packageId = safeText(dbJob.packageId(), "PKG-UNKNOWN");
                String orderId = safeText(dbJob.orderId(), "UNKNOWN-ORDER");
                List<PackingItem> items = mapOrderItems(orderId, productNames);

                PackingJob job = new PackingJob(packageId, orderId, items);
                PackingJobStatus status = mapStatus(dbJob.packagingStatus());
                job.setStatus(status);
                job.setProgress(defaultProgress(status));

                persistedPackageIds.add(packageId);
                jobs.add(job);
            }

            return jobs;
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] loadAllJobs failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void clearAll() {
        // Real DB mode is persistent by design; destructive clear is intentionally disabled.
        System.out.println("[SCMDatabaseAdapter] clearAll ignored in SCM database mode.");
    }

    private void persistJobIfTerminal(PackingJob job) {
        if (job.getStatus() != PackingJobStatus.PACKED && job.getStatus() != PackingJobStatus.FAILED) {
            return;
        }

        List<String> orderIds = splitOrderIds(job.getOrderId());
        for (int i = 0; i < orderIds.size(); i++) {
            String orderId = orderIds.get(i);
            String packageId = buildPackageId(job.getJobId(), i);

            if (!persistedPackageIds.add(packageId)) {
                continue;
            }

            try {
                facade.packaging().createPackagingJob(new PackagingModels.PackagingJob(
                        packageId,
                        orderId,
                        Math.max(1, job.getItems().size()),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        job.getStatus().name(),
                        "PACKING_SUBSYSTEM",
                        LocalDateTime.now()));
            } catch (Exception e) {
                // Allow later retries if insert fails (FK/DB state) by removing the marker.
                persistedPackageIds.remove(packageId);
                System.err.println("[SCMDatabaseAdapter] Failed to persist job "
                        + job.getJobId() + " for order " + orderId + ": " + e.getMessage());
            }
        }
    }

    private Map<String, String> loadProductNamesById() {
        Map<String, String> names = new HashMap<>();
        try {
            for (InventoryModels.Product product : facade.inventory().listProducts()) {
                names.put(product.productId(), safeText(product.productName(), product.productId()));
            }
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] Could not load product names: " + e.getMessage());
        }
        return names;
    }

    private Set<String> loadPackedOrderIds() {
        Set<String> packed = new HashSet<>();
        try {
            for (PackagingModels.PackagingJob job : facade.packaging().listPackagingJobs()) {
                if (mapStatus(job.packagingStatus()) == PackingJobStatus.PACKED) {
                    packed.add(safeText(job.orderId(), ""));
                }
            }
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] Could not read packaging status rows: " + e.getMessage());
        }
        return packed;
    }

    private List<PackingItem> mapOrderItems(String orderId, Map<String, String> productNames) {
        List<PackingItem> items = new ArrayList<>();

        try {
            List<com.jackfruit.scm.database.model.OrderItem> dbItems = facade.orders().listOrderItems(orderId);

            for (com.jackfruit.scm.database.model.OrderItem dbItem : dbItems) {
                String productId = safeText(dbItem.getProductId(), "UNKNOWN-PRODUCT");
                String description = productNames.getOrDefault(productId, productId);
                boolean fragile = looksFragile(description);
                int quantity = Math.max(1, dbItem.getOrderedQuantity());

                for (int i = 1; i <= quantity; i++) {
                    String itemId = quantity == 1 ? productId : productId + "-" + i;
                    items.add(new PackingItem(itemId, description, 0.50d, fragile));
                }
            }
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] Could not map order items for "
                    + orderId + ": " + e.getMessage());
        }

        return items;
    }

    private static String safeText(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static List<String> splitOrderIds(String rawOrderIds) {
        if (rawOrderIds == null || rawOrderIds.isBlank()) {
            return List.of("UNKNOWN-ORDER");
        }

        List<String> parsed = new ArrayList<>();
        for (String token : rawOrderIds.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                parsed.add(trimmed);
            }
        }

        if (parsed.isEmpty()) {
            parsed.add("UNKNOWN-ORDER");
        }
        return parsed;
    }

    private static String buildPackageId(String jobId, int index) {
        return index == 0 ? jobId : jobId + "-" + (index + 1);
    }

    private Connection openConnection() throws Exception {
        Properties properties = new Properties();

        try (InputStream input = SCMDatabaseAdapter.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalStateException("database.properties not found on classpath");
            }
            properties.load(input);
        }

        String url = safeText(properties.getProperty("db.url"), "");
        String user = safeText(properties.getProperty("db.username"), "");
        if (user.isBlank()) {
            user = safeText(properties.getProperty("db.user"), "");
        }
        String password = properties.getProperty("db.password", "");

        if (url.isBlank() || user.isBlank()) {
            throw new IllegalStateException("Missing db.url or db.username/db.user in database.properties");
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static int deletePackagingRowsByPackageId(Connection connection, List<String> packageIds)
            throws SQLException {
        if (packageIds.isEmpty()) {
            return 0;
        }

        String placeholders = String.join(",", Collections.nCopies(packageIds.size(), "?"));
        String sql = "DELETE FROM packaging_jobs WHERE package_id IN (" + placeholders + ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < packageIds.size(); i++) {
                statement.setString(i + 1, packageIds.get(i));
            }
            return statement.executeUpdate();
        }
    }

    private static int deletePackagingRowsByOrderId(Connection connection, List<String> orderIds)
            throws SQLException {
        if (orderIds.isEmpty()) {
            return 0;
        }

        String placeholders = String.join(",", Collections.nCopies(orderIds.size(), "?"));
        String sql = "DELETE FROM packaging_jobs WHERE order_id IN (" + placeholders + ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < orderIds.size(); i++) {
                statement.setString(i + 1, orderIds.get(i));
            }
            return statement.executeUpdate();
        }
    }

    private static PackingJobStatus mapStatus(String dbStatus) {
        if (dbStatus == null) {
            return PackingJobStatus.PENDING;
        }

        String normalized = dbStatus.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("FAIL")) {
            return PackingJobStatus.FAILED;
        }
        if (normalized.contains("PACKED") || normalized.contains("DONE") || normalized.contains("COMPLETE")) {
            return PackingJobStatus.PACKED;
        }
        if (normalized.contains("PACKING") || normalized.contains("IN_PROGRESS")) {
            return PackingJobStatus.PACKING;
        }
        return PackingJobStatus.PENDING;
    }

    private static int defaultProgress(PackingJobStatus status) {
        switch (status) {
            case PACKED:
            case FAILED:
                return 100;
            case PACKING:
                return 50;
            case PENDING:
            default:
                return 0;
        }
    }

    private static boolean looksFragile(String description) {
        String text = description == null ? "" : description.toLowerCase(Locale.ROOT);
        return text.contains("fragile")
            || text.contains("glass")
            || text.contains("crystal")
            || text.contains("porcelain")
                || text.contains("ceramic")
            || text.contains("glassware");
    }
}
