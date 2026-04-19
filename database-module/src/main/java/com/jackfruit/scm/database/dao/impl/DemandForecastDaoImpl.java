package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.DemandForecastDao;
import com.jackfruit.scm.database.model.DemandForecast;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class DemandForecastDaoImpl extends AbstractJdbcDao implements DemandForecastDao {

    @Override
    public void save(DemandForecast forecast) {
        executeUpdate(
                """
                INSERT INTO demand_forecasts
                (forecast_id, product_id, forecast_period, forecast_date, predicted_demand, confidence_score,
                 reorder_signal, suggested_order_qty, lifecycle_stage, algorithm_used, generated_at, source_event_reference)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, forecast.getForecastId());
                    statement.setString(2, forecast.getProductId());
                    statement.setString(3, forecast.getForecastPeriod());
                    statement.setDate(4, forecast.getForecastDate() == null ? null : Date.valueOf(forecast.getForecastDate()));
                    statement.setInt(5, forecast.getPredictedDemand());
                    statement.setBigDecimal(6, forecast.getConfidenceScore());
                    statement.setBoolean(7, forecast.isReorderSignal());
                    if (forecast.getSuggestedOrderQty() == null) {
                        statement.setNull(8, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(8, forecast.getSuggestedOrderQty());
                    }
                    statement.setString(9, forecast.getLifecycleStage());
                    statement.setString(10, forecast.getAlgorithmUsed());
                    statement.setTimestamp(11, Timestamp.valueOf(forecast.getGeneratedAt()));
                    statement.setString(12, forecast.getSourceEventReference());
                });
    }

    @Override
    public void update(DemandForecast forecast) {
        executeUpdate(
                """
                UPDATE demand_forecasts
                SET forecast_date = ?, predicted_demand = ?, confidence_score = ?, reorder_signal = ?,
                    suggested_order_qty = ?, lifecycle_stage = ?, algorithm_used = ?, generated_at = ?,
                    source_event_reference = ?
                WHERE forecast_id = ?
                """,
                statement -> {
                    statement.setDate(1, forecast.getForecastDate() == null ? null : Date.valueOf(forecast.getForecastDate()));
                    statement.setInt(2, forecast.getPredictedDemand());
                    statement.setBigDecimal(3, forecast.getConfidenceScore());
                    statement.setBoolean(4, forecast.isReorderSignal());
                    if (forecast.getSuggestedOrderQty() == null) {
                        statement.setNull(5, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(5, forecast.getSuggestedOrderQty());
                    }
                    statement.setString(6, forecast.getLifecycleStage());
                    statement.setString(7, forecast.getAlgorithmUsed());
                    statement.setTimestamp(8, Timestamp.valueOf(forecast.getGeneratedAt()));
                    statement.setString(9, forecast.getSourceEventReference());
                    statement.setString(10, forecast.getForecastId());
                });
    }

    @Override
    public Optional<DemandForecast> findById(String forecastId) {
        return queryForObject(
                "SELECT * FROM demand_forecasts WHERE forecast_id = ?",
                resultSet -> new DemandForecast(
                        resultSet.getString("forecast_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("forecast_period"),
                        resultSet.getDate("forecast_date") == null ? null : resultSet.getDate("forecast_date").toLocalDate(),
                        resultSet.getInt("predicted_demand"),
                        resultSet.getBigDecimal("confidence_score"),
                        resultSet.getBoolean("reorder_signal"),
                        (Integer) resultSet.getObject("suggested_order_qty"),
                        resultSet.getString("lifecycle_stage"),
                        resultSet.getString("algorithm_used"),
                        resultSet.getTimestamp("generated_at").toLocalDateTime(),
                        resultSet.getString("source_event_reference")),
                statement -> statement.setString(1, forecastId));
    }

    @Override
    public List<DemandForecast> findAll() {
        return queryForList(
                "SELECT * FROM demand_forecasts",
                resultSet -> new DemandForecast(
                        resultSet.getString("forecast_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("forecast_period"),
                        resultSet.getDate("forecast_date") == null ? null : resultSet.getDate("forecast_date").toLocalDate(),
                        resultSet.getInt("predicted_demand"),
                        resultSet.getBigDecimal("confidence_score"),
                        resultSet.getBoolean("reorder_signal"),
                        (Integer) resultSet.getObject("suggested_order_qty"),
                        resultSet.getString("lifecycle_stage"),
                        resultSet.getString("algorithm_used"),
                        resultSet.getTimestamp("generated_at").toLocalDateTime(),
                        resultSet.getString("source_event_reference")));
    }
}
