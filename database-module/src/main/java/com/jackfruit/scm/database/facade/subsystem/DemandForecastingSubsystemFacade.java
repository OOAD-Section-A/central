package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.dao.ForecastTimeseriesDao;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.DemandForecastingModels.ForecastPerformanceMetric;
import com.jackfruit.scm.database.model.DemandForecastingModels.HolidayCalendar;
import com.jackfruit.scm.database.model.DemandForecastingModels.InventorySupply;
import com.jackfruit.scm.database.model.DemandForecastingModels.ProductLifecycleStage;
import com.jackfruit.scm.database.model.DemandForecastingModels.ProductMetadata;
import com.jackfruit.scm.database.model.DemandForecastingModels.PromotionalCalendar;
import com.jackfruit.scm.database.model.DemandForecastingModels.SalesRecord;
import com.jackfruit.scm.database.model.ForecastTimeseries;
import com.jackfruit.scm.database.service.ForecastService;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Date;
import java.util.List;

public class DemandForecastingSubsystemFacade {

    private final ForecastService forecastService;
    private final JdbcOperations jdbcOperations;
    private final ForecastTimeseriesDao forecastTimeseriesDao;

    public DemandForecastingSubsystemFacade(ForecastService forecastService, JdbcOperations jdbcOperations, ForecastTimeseriesDao forecastTimeseriesDao) {
        this.forecastService = forecastService;
        this.jdbcOperations = jdbcOperations;
        this.forecastTimeseriesDao = forecastTimeseriesDao;
    }

    public void createForecast(DemandForecast forecast) {
        forecastService.createForecast(forecast);
    }

    public List<DemandForecast> listForecasts() {
        return forecastService.getForecasts();
    }

    public void createSalesRecord(SalesRecord salesRecord) {
        jdbcOperations.update(
                "INSERT INTO sales_records (sale_id, product_id, store_id, sale_date, quantity_sold, unit_price, revenue, region) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, salesRecord.saleId());
                    statement.setString(2, salesRecord.productId());
                    statement.setString(3, salesRecord.storeId());
                    statement.setDate(4, Date.valueOf(salesRecord.saleDate()));
                    statement.setInt(5, salesRecord.quantitySold());
                    statement.setBigDecimal(6, salesRecord.unitPrice());
                    statement.setBigDecimal(7, salesRecord.revenue());
                    statement.setString(8, salesRecord.region());
                });
    }

    public List<SalesRecord> listSalesRecords() {
        return jdbcOperations.query(
                "SELECT * FROM sales_records",
                resultSet -> new SalesRecord(
                        resultSet.getString("sale_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("store_id"),
                        resultSet.getDate("sale_date").toLocalDate(),
                        resultSet.getInt("quantity_sold"),
                        resultSet.getBigDecimal("unit_price"),
                        resultSet.getBigDecimal("revenue"),
                        resultSet.getString("region")));
    }

    public void createHolidayCalendar(HolidayCalendar holidayCalendar) {
        jdbcOperations.update(
                "INSERT INTO holiday_calendar (holiday_id, holiday_date, holiday_name, holiday_type, region_applicable) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, holidayCalendar.holidayId());
                    statement.setDate(2, Date.valueOf(holidayCalendar.holidayDate()));
                    statement.setString(3, holidayCalendar.holidayName());
                    statement.setString(4, holidayCalendar.holidayType());
                    statement.setString(5, holidayCalendar.regionApplicable());
                });
    }

    public void createPromotionalCalendar(PromotionalCalendar promotionalCalendar) {
        jdbcOperations.update(
                """
                INSERT INTO promotional_calendar
                (promo_calendar_id, promo_id, promo_name, promo_start_date, promo_end_date, discount_percentage, promo_type, applicable_products)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, promotionalCalendar.promoCalendarId());
                    statement.setString(2, promotionalCalendar.promoId());
                    statement.setString(3, promotionalCalendar.promoName());
                    statement.setDate(4, Date.valueOf(promotionalCalendar.promoStartDate()));
                    statement.setDate(5, Date.valueOf(promotionalCalendar.promoEndDate()));
                    statement.setBigDecimal(6, promotionalCalendar.discountPercentage());
                    statement.setString(7, promotionalCalendar.promoType());
                    statement.setString(8, promotionalCalendar.applicableProducts());
                });
    }

    public void createProductMetadata(ProductMetadata productMetadata) {
        jdbcOperations.update(
                """
                INSERT INTO product_metadata
                (product_id, product_name, category, sub_category, seasonality_type)
                VALUES (?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, productMetadata.productId());
                    statement.setString(2, productMetadata.productName());
                    statement.setString(3, productMetadata.category());
                    statement.setString(4, productMetadata.subCategory());
                    statement.setString(5, productMetadata.seasonalityType());
                });
    }

    public void createProductLifecycleStage(ProductLifecycleStage stage) {
        jdbcOperations.update(
                """
                INSERT INTO product_lifecycle_stages
                (lifecycle_id, product_id, current_stage, stage_start_date, previous_stage, transition_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stage.lifecycleId());
                    statement.setString(2, stage.productId());
                    statement.setString(3, stage.currentStage());
                    statement.setDate(4, Date.valueOf(stage.stageStartDate()));
                    statement.setString(5, stage.previousStage());
                    statement.setDate(6, stage.transitionDate() == null ? null : Date.valueOf(stage.transitionDate()));
                });
    }

    public void createInventorySupply(InventorySupply inventorySupply) {
        jdbcOperations.update(
                """
                INSERT INTO inventory_supply
                (product_id, current_stock, reorder_point, lead_time_days, supplier_id)
                VALUES (?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, inventorySupply.productId());
                    if (inventorySupply.currentStock() == null) {
                        statement.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(2, inventorySupply.currentStock());
                    }
                    if (inventorySupply.reorderPoint() == null) {
                        statement.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(3, inventorySupply.reorderPoint());
                    }
                    if (inventorySupply.leadTimeDays() == null) {
                        statement.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(4, inventorySupply.leadTimeDays());
                    }
                    statement.setString(5, inventorySupply.supplierId());
                });
    }

    public void createForecastPerformanceMetric(ForecastPerformanceMetric metric) {
        jdbcOperations.update(
                """
                INSERT INTO forecast_performance_metrics
                (eval_id, forecast_id, forecast_date, predicted_qty, actual_qty, mape, rmse, model_used)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, metric.evalId());
                    statement.setString(2, metric.forecastId());
                    statement.setDate(3, Date.valueOf(metric.forecastDate()));
                    statement.setInt(4, metric.predictedQty());
                    if (metric.actualQty() == null) {
                        statement.setNull(5, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(5, metric.actualQty());
                    }
                    statement.setBigDecimal(6, metric.mape());
                    statement.setBigDecimal(7, metric.rmse());
                    statement.setString(8, metric.modelUsed());
                });
    }

    public void createForecastTimeseries(ForecastTimeseries timeseries) {
        forecastTimeseriesDao.save(timeseries);
    }

    public void createBatchForecastTimeseries(List<ForecastTimeseries> timeseriesList) {
        for (ForecastTimeseries timeseries : timeseriesList) {
            forecastTimeseriesDao.save(timeseries);
        }
    }
}
