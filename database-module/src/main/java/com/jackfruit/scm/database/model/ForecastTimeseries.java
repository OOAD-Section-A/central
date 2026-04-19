package com.jackfruit.scm.database.model;

import java.math.BigDecimal;

public class ForecastTimeseries {

    private String id;
    private String forecastId;
    private Integer timeIndex;
    private BigDecimal forecastValue;
    private BigDecimal lowerBound;
    private BigDecimal upperBound;

    public ForecastTimeseries() {
    }

    public ForecastTimeseries(String id, String forecastId, Integer timeIndex,
                              BigDecimal forecastValue, BigDecimal lowerBound, BigDecimal upperBound) {
        this.id = id;
        this.forecastId = forecastId;
        this.timeIndex = timeIndex;
        this.forecastValue = forecastValue;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForecastId() {
        return forecastId;
    }

    public void setForecastId(String forecastId) {
        this.forecastId = forecastId;
    }

    public Integer getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(Integer timeIndex) {
        this.timeIndex = timeIndex;
    }

    public BigDecimal getForecastValue() {
        return forecastValue;
    }

    public void setForecastValue(BigDecimal forecastValue) {
        this.forecastValue = forecastValue;
    }

    public BigDecimal getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(BigDecimal lowerBound) {
        this.lowerBound = lowerBound;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(BigDecimal upperBound) {
        this.upperBound = upperBound;
    }
}
