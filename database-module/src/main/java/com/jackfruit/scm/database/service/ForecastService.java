package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.DemandForecastDao;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ForecastService {

    private final DemandForecastDao demandForecastDao;

    public ForecastService(DemandForecastDao demandForecastDao) {
        this.demandForecastDao = demandForecastDao;
    }

    public void createForecast(DemandForecast forecast) {
        validateForecast(forecast);
        demandForecastDao.save(forecast);
    }

    public void updateForecast(DemandForecast forecast) {
        validateForecast(forecast);
        demandForecastDao.update(forecast);
    }

    public Optional<DemandForecast> getForecast(String forecastId) {
        ValidationUtils.requireText(forecastId, "forecastId");
        return demandForecastDao.findById(forecastId);
    }

    public List<DemandForecast> getForecasts() {
        return demandForecastDao.findAll();
    }

    private void validateForecast(DemandForecast forecast) {
        ValidationUtils.requireText(forecast.getForecastId(), "forecastId");
        ValidationUtils.requireText(forecast.getProductId(), "productId");
        ValidationUtils.requireText(forecast.getForecastPeriod(), "forecastPeriod");
        ValidationUtils.requireNonNegative(forecast.getPredictedDemand(), "predictedDemand");
        ValidationUtils.requireRange(forecast.getConfidenceScore(), BigDecimal.ZERO, new BigDecimal("100"), "confidenceScore");
        if (forecast.getSuggestedOrderQty() != null) {
            ValidationUtils.requireNonNegative(forecast.getSuggestedOrderQty(), "suggestedOrderQty");
        }
        if (forecast.getGeneratedAt() == null) {
            throw new IllegalArgumentException("generatedAt cannot be null");
        }
    }
}
