package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DemandForecast {

    private String forecastId;
    private String productId;
    private String forecastPeriod;
    private LocalDate forecastDate;
    private int predictedDemand;
    private BigDecimal confidenceScore;
    private boolean reorderSignal;
    private Integer suggestedOrderQty;
    private String lifecycleStage;
    private String algorithmUsed;
    private LocalDateTime generatedAt;
    private String sourceEventReference;

    public DemandForecast() {
    }

    public DemandForecast(String forecastId, String productId, String forecastPeriod, int predictedDemand,
                          BigDecimal confidenceScore, LocalDateTime generatedAt, String sourceEventReference) {
        this(forecastId, productId, forecastPeriod, null, predictedDemand, confidenceScore, false, null,
                null, null, generatedAt, sourceEventReference);
    }

    public DemandForecast(String forecastId, String productId, String forecastPeriod, LocalDate forecastDate,
                          int predictedDemand, BigDecimal confidenceScore, boolean reorderSignal,
                          Integer suggestedOrderQty, String lifecycleStage, String algorithmUsed,
                          LocalDateTime generatedAt, String sourceEventReference) {
        this.forecastId = forecastId;
        this.productId = productId;
        this.forecastPeriod = forecastPeriod;
        this.forecastDate = forecastDate;
        this.predictedDemand = predictedDemand;
        this.confidenceScore = confidenceScore;
        this.reorderSignal = reorderSignal;
        this.suggestedOrderQty = suggestedOrderQty;
        this.lifecycleStage = lifecycleStage;
        this.algorithmUsed = algorithmUsed;
        this.generatedAt = generatedAt;
        this.sourceEventReference = sourceEventReference;
    }

    public String getForecastId() {
        return forecastId;
    }

    public void setForecastId(String forecastId) {
        this.forecastId = forecastId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getForecastPeriod() {
        return forecastPeriod;
    }

    public void setForecastPeriod(String forecastPeriod) {
        this.forecastPeriod = forecastPeriod;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(LocalDate forecastDate) {
        this.forecastDate = forecastDate;
    }

    public int getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(int predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public boolean isReorderSignal() {
        return reorderSignal;
    }

    public void setReorderSignal(boolean reorderSignal) {
        this.reorderSignal = reorderSignal;
    }

    public Integer getSuggestedOrderQty() {
        return suggestedOrderQty;
    }

    public void setSuggestedOrderQty(Integer suggestedOrderQty) {
        this.suggestedOrderQty = suggestedOrderQty;
    }

    public String getLifecycleStage() {
        return lifecycleStage;
    }

    public void setLifecycleStage(String lifecycleStage) {
        this.lifecycleStage = lifecycleStage;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getSourceEventReference() {
        return sourceEventReference;
    }

    public void setSourceEventReference(String sourceEventReference) {
        this.sourceEventReference = sourceEventReference;
    }
}
