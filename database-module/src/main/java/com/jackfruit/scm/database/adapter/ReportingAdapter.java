package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.ReportingModels.CustomerTierCacheRow;
import com.jackfruit.scm.database.model.ReportingModels.DashboardReportRow;
import com.jackfruit.scm.database.model.ReportingModels.ExceptionReportRow;
import com.jackfruit.scm.database.model.ReportingModels.InventoryStockReportRow;
import com.jackfruit.scm.database.model.ReportingModels.PriceDiscountReportRow;
import java.util.List;

public class ReportingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public ReportingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public List<InventoryStockReportRow> getInventoryStockReport() {
        return facade.reporting().getInventoryStockReport();
    }

    public List<PriceDiscountReportRow> getPriceDiscountReport() {
        return facade.reporting().getPriceDiscountReport();
    }

    public List<ExceptionReportRow> getExceptionReport() {
        return facade.reporting().getExceptionReport();
    }

    public List<CustomerTierCacheRow> getCustomerTierCacheReport() {
        return facade.reporting().getCustomerTierCacheReport();
    }

    public List<DashboardReportRow> getDashboardReport() {
        return facade.reporting().getDashboardReport();
    }
}
