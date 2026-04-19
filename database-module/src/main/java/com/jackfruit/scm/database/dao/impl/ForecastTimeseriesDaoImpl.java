package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.ForecastTimeseriesDao;
import com.jackfruit.scm.database.model.ForecastTimeseries;
import java.util.List;
import java.util.Optional;

public class ForecastTimeseriesDaoImpl extends AbstractJdbcDao implements ForecastTimeseriesDao {

    @Override
    public void save(ForecastTimeseries forecastTimeseries) {
        executeUpdate(
                """
                INSERT INTO forecast_timeseries
                (id, forecast_id, time_index, forecast_value, lower_bound, upper_bound)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, forecastTimeseries.getId());
                    statement.setString(2, forecastTimeseries.getForecastId());
                    statement.setInt(3, forecastTimeseries.getTimeIndex());
                    statement.setBigDecimal(4, forecastTimeseries.getForecastValue());
                    statement.setBigDecimal(5, forecastTimeseries.getLowerBound());
                    statement.setBigDecimal(6, forecastTimeseries.getUpperBound());
                });
    }

    @Override
    public Optional<ForecastTimeseries> findById(String id) {
        return queryForObject(
                "SELECT * FROM forecast_timeseries WHERE id = ?",
                resultSet -> new ForecastTimeseries(
                        resultSet.getString("id"),
                        resultSet.getString("forecast_id"),
                        resultSet.getInt("time_index"),
                        resultSet.getBigDecimal("forecast_value"),
                        resultSet.getBigDecimal("lower_bound"),
                        resultSet.getBigDecimal("upper_bound")),
                statement -> statement.setString(1, id));
    }

    @Override
    public List<ForecastTimeseries> findByForecastId(String forecastId) {
        return queryForList(
                "SELECT * FROM forecast_timeseries WHERE forecast_id = ? ORDER BY time_index ASC",
                resultSet -> new ForecastTimeseries(
                        resultSet.getString("id"),
                        resultSet.getString("forecast_id"),
                        resultSet.getInt("time_index"),
                        resultSet.getBigDecimal("forecast_value"),
                        resultSet.getBigDecimal("lower_bound"),
                        resultSet.getBigDecimal("upper_bound")),
                statement -> statement.setString(1, forecastId));
    }

    @Override
    public List<ForecastTimeseries> findAll() {
        return queryForList(
                "SELECT * FROM forecast_timeseries ORDER BY forecast_id, time_index ASC",
                resultSet -> new ForecastTimeseries(
                        resultSet.getString("id"),
                        resultSet.getString("forecast_id"),
                        resultSet.getInt("time_index"),
                        resultSet.getBigDecimal("forecast_value"),
                        resultSet.getBigDecimal("lower_bound"),
                        resultSet.getBigDecimal("upper_bound")));
    }
}
