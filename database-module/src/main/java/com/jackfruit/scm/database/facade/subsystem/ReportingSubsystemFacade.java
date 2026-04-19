package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.ReportingModels.CustomerTierCacheRow;
import com.jackfruit.scm.database.model.ReportingModels.ExceptionReportRow;
import com.jackfruit.scm.database.model.ReportingModels.InventoryStockReportRow;
import com.jackfruit.scm.database.model.ReportingModels.PriceDiscountReportRow;
import com.jackfruit.scm.database.model.ReportingModels.DashboardReportRow;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.util.List;

public class ReportingSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public ReportingSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public List<InventoryStockReportRow> getInventoryStockReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_inventory_stock_report",
                resultSet -> new InventoryStockReportRow(
                        resultSet.getString("productID"),
                        resultSet.getString("bin_id"),
                        resultSet.getString("zone_id"),
                        resultSet.getString("warehouseID"),
                        resultSet.getInt("current_stock_level"),
                        resultSet.getTimestamp("last_updated").toLocalDateTime()));
    }

    public List<PriceDiscountReportRow> getPriceDiscountReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_price_discount_report",
                resultSet -> new PriceDiscountReportRow(
                        resultSet.getString("productID"),
                        resultSet.getBigDecimal("product_price"),
                        resultSet.getString("region_code"),
                        resultSet.getString("channel"),
                        resultSet.getString("currency_code"),
                        resultSet.getString("status")));
    }

    public List<ExceptionReportRow> getExceptionReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_exception_report",
                resultSet -> new ExceptionReportRow(
                        resultSet.getString("exceptionID"),
                        resultSet.getString("exceptionType"),
                        resultSet.getString("severity_level"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime(),
                        resultSet.getString("requested_by"),
                        resultSet.getString("justification_text")));
    }

    public List<CustomerTierCacheRow> getCustomerTierCacheReport() {
        return jdbcOperations.query(
                "SELECT * FROM customer_tier_cache",
                resultSet -> new CustomerTierCacheRow(
                        resultSet.getString("customer_id"),
                        resultSet.getString("tier"),
                        resultSet.getTimestamp("evaluated_at").toLocalDateTime()));
    }

    public List<DashboardReportRow> getDashboardReport() {
        return jdbcOperations.query(
                "SELECT * FROM vw_reporting_dashboard",
                resultSet -> new DashboardReportRow(
                        resultSet.getString("order_id"),
                        resultSet.getTimestamp("order_date") == null ? null : resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getTimestamp("delivery_date") == null ? null : resultSet.getTimestamp("delivery_date").toLocalDateTime(),
                        resultSet.getString("order_status"),
                        (Double) resultSet.getObject("fulfillment_time"),
                        (Integer) resultSet.getObject("order_quantity"),
                        resultSet.getString("product_id"),
                        resultSet.getString("product_name"),
                        (Integer) resultSet.getObject("current_stock_level"),
                        (Integer) resultSet.getObject("reorder_level"),
                        (Boolean) resultSet.getObject("stock_out_flag"),
                        (Double) resultSet.getObject("inventory_turnover_rate"),
                        resultSet.getString("supplier_id"),
                        resultSet.getString("supplier_name"),
                        (Double) resultSet.getObject("supplier_performance_score"),
                        (Double) resultSet.getObject("lead_time"),
                        (Double) resultSet.getObject("on_time_supply_rate"),
                        resultSet.getString("shipment_id"),
                        resultSet.getTimestamp("dispatch_date") == null ? null : resultSet.getTimestamp("dispatch_date").toLocalDateTime(),
                        resultSet.getString("delivery_status"),
                        (Double) resultSet.getObject("transit_time"),
                        (Boolean) resultSet.getObject("delay_flag"),
                        resultSet.getString("delivery_location"),
                        resultSet.getString("warehouse_id"),
                        (Integer) resultSet.getObject("storage_capacity"),
                        (Double) resultSet.getObject("utilization_rate"),
                        (Integer) resultSet.getObject("inbound_quantity"),
                        (Integer) resultSet.getObject("outbound_quantity"),
                        resultSet.getBigDecimal("product_price"),
                        resultSet.getBigDecimal("discount_applied"),
                        (Integer) resultSet.getObject("sales_volume"),
                        resultSet.getBigDecimal("revenue"),
                        (Integer) resultSet.getObject("demand_forecast"),
                        resultSet.getString("forecast_period"),
                        (Integer) resultSet.getObject("predicted_inventory_needs"),
                        resultSet.getString("exception_id"),
                        resultSet.getString("exception_type"),
                        resultSet.getString("severity_level"),
                        resultSet.getTimestamp("timestamp") == null ? null : resultSet.getTimestamp("timestamp").toLocalDateTime()));
    }
}
