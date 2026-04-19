package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.StockLedgerModels.StockLedgerEntry;
import java.util.List;

public class StockLedgerAdapter {

    private final SupplyChainDatabaseFacade facade;

    public StockLedgerAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createLedgerEntry(StockLedgerEntry entry) {
        facade.stockLedger().createLedgerEntry(entry);
    }

    public List<StockLedgerEntry> listLedgerEntries() {
        return facade.stockLedger().listLedgerEntries();
    }
}
