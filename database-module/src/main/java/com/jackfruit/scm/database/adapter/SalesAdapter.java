package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.PriceList;

public class SalesAdapter {

    private final SupplyChainDatabaseFacade facade;

    public SalesAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void publishPrice(PriceList priceList) {
        facade.pricing().publishPrice(priceList);
    }
}
