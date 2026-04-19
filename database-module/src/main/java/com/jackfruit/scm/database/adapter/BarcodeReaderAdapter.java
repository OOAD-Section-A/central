package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;

public class BarcodeReaderAdapter {

    private final SupplyChainDatabaseFacade facade;

    public BarcodeReaderAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void recordScan(BarcodeRfidEvent event) {
        facade.barcode().recordBarcodeEvent(event);
    }
}
