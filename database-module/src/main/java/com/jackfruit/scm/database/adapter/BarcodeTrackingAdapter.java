package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;

public class BarcodeTrackingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public BarcodeTrackingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void recordBarcodeEvent(BarcodeRfidEvent event) {
        facade.barcode().recordBarcodeEvent(event);
    }
}
