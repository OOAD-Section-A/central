package wms.integration.database;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.WarehouseModels.GoodsReceipt;
import com.jackfruit.scm.database.model.WarehouseModels.StockMovement;
import com.jackfruit.scm.database.model.WarehouseModels.StockRecord;

import java.time.LocalDateTime;

/**
 * Adapter that connects to the external SCM database via the shared
 * {@code SupplyChainDatabaseFacade}.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> Adapts the external
 * database module's API to the internal {@link IWMSDatabaseLayer}
 * interface expected by the WMS subsystem.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> The rest of the WMS never
 * imports this class directly — it only references
 * {@link IWMSDatabaseLayer}.</p>
 *
 * <p><b>SOLID – Single Responsibility:</b> This class only translates
 * between {@link IWMSDatabaseLayer} calls and the SCM facade methods.</p>
 *
 * <p>Relevant facade paths used:
 * <ul>
 *   <li>{@code facade.warehouse().createGoodsReceipt(...)}</li>
 *   <li>{@code facade.warehouse().createStockMovement(...)}</li>
 *   <li>{@code facade.warehouse().createStockRecord(...)}</li>
 *   <li>{@code facade.warehouse().listStockRecords()}</li>
 * </ul></p>
 */
public class SCMDatabaseAdapter implements IWMSDatabaseLayer {

    private static final String SUBSYSTEM = "Warehouse Management";
    private final SupplyChainDatabaseFacade facade;

    public SCMDatabaseAdapter() {
        this.facade = new SupplyChainDatabaseFacade();
        System.out.println("[WMS SCMDatabaseAdapter] Connected to SupplyChainDatabaseFacade.");
    }

    /**
     * Validates whether a PO exists by checking the warehouse's goods receipt
     * records (if a GR exists for this PO, it was at some point opened).
     *
     * <p>Since the current facade does not expose a direct purchase-order
     * lookup, we conservatively return {@code true} so that inbound receiving
     * can proceed. A richer implementation would call a dedicated orders
     * facade method when it becomes available.</p>
     */
    @Override
    public boolean validatePurchaseOrder(String poNumber) {
        // Optimistic validation — the WMS subsystem's business rules still apply
        // on top of this. The database constraint will catch a truly invalid FK.
        System.out.println("[WMS SCMDatabaseAdapter] PO validation requested for: " + poNumber);
        return poNumber != null && !poNumber.isBlank();
    }

    @Override
    public void recordStockMovement(String sku, String fromLocationId, String toLocationId, int quantity) {
        try {
            String movementId = "MOV-" + sku + "-" + System.currentTimeMillis();
            StockMovement movement = new StockMovement(
                    movementId,
                    "TRANSFER",
                    fromLocationId,
                    toLocationId,
                    sku,
                    quantity,
                    LocalDateTime.now()
            );
            facade.warehouse().createStockMovement(movement);
            System.out.println("[WMS SCMDatabaseAdapter] Recorded stock movement: " + movementId);
        } catch (Exception e) {
            System.err.println("[WMS SCMDatabaseAdapter] recordStockMovement failed for SKU "
                    + sku + ": " + e.getMessage());
        }
    }

    @Override
    public boolean isBinAvailable(String binId) {
        try {
            return facade.warehouse().listStockRecords().stream()
                    .filter(r -> binId.equals(r.binId()))
                    .mapToInt(StockRecord::quantity)
                    .sum() < 1000;  // conservative capacity guard
        } catch (Exception e) {
            System.err.println("[WMS SCMDatabaseAdapter] isBinAvailable check failed: " + e.getMessage());
            return true; // fail-open to avoid blocking operations
        }
    }

    @Override
    public void persistGoodsReceipt(String grnId, String poNumber, String sku, int qty) {
        try {
            GoodsReceipt gr = new GoodsReceipt(
                    grnId,
                    poNumber,
                    "WMS-SUPPLIER",   // supplier resolved by business layer
                    sku,
                    qty,
                    qty,
                    LocalDateTime.now(),
                    "ACCEPTED"
            );
            facade.warehouse().createGoodsReceipt(gr);
            System.out.println("[WMS SCMDatabaseAdapter] Goods receipt persisted: " + grnId);
        } catch (Exception e) {
            System.err.println("[WMS SCMDatabaseAdapter] persistGoodsReceipt failed: " + e.getMessage());
        }
    }

    @Override
    public void updateStockRecord(String sku, String binId, int quantity) {
        try {
            String stockId = "STK-" + sku + "-" + binId;
            StockRecord record = new StockRecord(
                    stockId,
                    sku,
                    binId,
                    quantity,
                    LocalDateTime.now()
            );
            facade.warehouse().createStockRecord(record);
            System.out.println("[WMS SCMDatabaseAdapter] Stock record updated: " + stockId + " qty=" + quantity);
        } catch (Exception e) {
            System.err.println("[WMS SCMDatabaseAdapter] updateStockRecord failed for SKU "
                    + sku + ": " + e.getMessage());
        }
    }
}
