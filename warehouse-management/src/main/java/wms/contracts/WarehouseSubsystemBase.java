package wms.contracts;

import wms.integration.database.IWMSDatabaseLayer;

/**
 * Description: Abstract Bridge for other subsystems (Order Fulfillment, Real-Time Monitoring).
 */

public abstract class WarehouseSubsystemBase {
    
    protected IWMSDatabaseLayer databaseLayer;

    public WarehouseSubsystemBase(IWMSDatabaseLayer databaseLayer) {
        this.databaseLayer = databaseLayer;
    }

    // Abstract hook for Order Fulfillment (Subsystem 3 & 4)
    public abstract boolean reserveStockForOrder(String sku, int quantity);

    // Abstract hook for Barcode/RFID Tracker (Subsystem 11)
    public abstract void processInboundScan(String barcode, String dockId);
    
    // Common concrete method that can be used by all extending classes
    public String getSubsystemStatus() {
        return "Warehouse Management Subsystem: Active";
    }
}
