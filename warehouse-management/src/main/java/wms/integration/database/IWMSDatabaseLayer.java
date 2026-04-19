package wms.integration.database;

import java.util.List;

/**
 * Abstraction for the persistence layer used by the WMS subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This interface is the
 * "target" that the rest of the WMS codes against. Concrete implementations
 * adapt either the real SCM database facade or the pre-existing
 * {@code IWMSRepository} mock to this common contract.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> High-level modules (WarehouseFacade,
 * InboundReceivingController) depend on this interface, never on concrete
 * database classes.</p>
 */
public interface IWMSDatabaseLayer {

    /**
     * Validates whether a purchase order exists in the database.
     *
     * @param poNumber the purchase order number to validate
     * @return true if the PO exists and is open
     */
    boolean validatePurchaseOrder(String poNumber);

    /**
     * Records a stock movement transaction (Double-Entry Stock Keeping).
     * Maps to {@code inventory_transactions} / {@code stock_movements} table.
     *
     * @param sku            the product SKU being moved
     * @param fromLocationId origin bin or dock ID
     * @param toLocationId   destination bin ID
     * @param quantity       units moved
     */
    void recordStockMovement(String sku, String fromLocationId, String toLocationId, int quantity);

    /**
     * Checks whether a storage bin is available (not blocked, not full).
     *
     * @param binId the bin identifier
     * @return true if the bin can accept stock
     */
    boolean isBinAvailable(String binId);

    /**
     * Creates a Goods Receipt Note record in the database.
     *
     * @param grnId    generated GRN ID
     * @param poNumber referenced purchase order
     * @param sku      product SKU received
     * @param qty      accepted quantity
     */
    void persistGoodsReceipt(String grnId, String poNumber, String sku, int qty);

    /**
     * Persists a stock level update for a given SKU in a bin.
     *
     * @param sku      product SKU
     * @param binId    the target bin
     * @param quantity new absolute quantity in that bin
     */
    void updateStockRecord(String sku, String binId, int quantity);
}
