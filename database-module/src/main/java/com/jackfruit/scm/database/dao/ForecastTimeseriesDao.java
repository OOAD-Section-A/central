package com.jackfruit.scm.database.dao;

import com.jackfruit.scm.database.model.ForecastTimeseries;
import java.util.List;
import java.util.Optional;

public interface ForecastTimeseriesDao {

    void save(ForecastTimeseries forecastTimeseries);

    Optional<ForecastTimeseries> findById(String id);

    List<ForecastTimeseries> findByForecastId(String forecastId);

    List<ForecastTimeseries> findAll();
}
