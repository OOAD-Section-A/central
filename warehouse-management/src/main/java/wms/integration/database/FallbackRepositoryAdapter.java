package wms.integration.database;

import wms.contracts.IWMSRepository;

/**
 * Fallback in-memory database layer used when the SCM database-module JAR
 * is not on the classpath.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> Adapts the pre-existing
 * {@link IWMSRepository} mock contract to the new {@link IWMSDatabaseLayer}
 * interface so that the WMS can run without a live MySQL connection.</p>
 *
 * <p><b>SOLID – Liskov Substitution:</b> Any code expecting an
 * {@link IWMSDatabaseLayer} works identically regardless of whether it
 * receives this fallback or the real SCM adapter.</p>
 */
public class FallbackRepositoryAdapter implements IWMSDatabaseLayer {

    private final IWMSRepository legacy;

    public FallbackRepositoryAdapter(IWMSRepository legacy) {
        this.legacy = legacy;
        System.out.println("[WMS FallbackRepositoryAdapter] Running in in-memory/mock mode.");
    }

    @Override
    public boolean validatePurchaseOrder(String poNumber) {
        return legacy.validatePurchaseOrder(poNumber);
    }

    @Override
    public void recordStockMovement(String sku, String fromLocationId, String toLocationId, int quantity) {
        legacy.recordStockMovement(sku, fromLocationId, toLocationId, quantity);
    }

    @Override
    public boolean isBinAvailable(String binId) {
        return legacy.isBinAvailable(binId);
    }

    @Override
    public void persistGoodsReceipt(String grnId, String poNumber, String sku, int qty) {
        // IWMSRepository does not have a GRN method — log informational only.
        System.out.println("[WMS FallbackRepositoryAdapter] GRN would be persisted: "
                + grnId + " | PO=" + poNumber + " | SKU=" + sku + " | Qty=" + qty);
    }

    @Override
    public void updateStockRecord(String sku, String binId, int quantity) {
        // In-memory only — delegate to repository if it ever gains this capability.
        System.out.println("[WMS FallbackRepositoryAdapter] Stock record update (in-memory): "
                + sku + " -> bin=" + binId + " qty=" + quantity);
    }
}
