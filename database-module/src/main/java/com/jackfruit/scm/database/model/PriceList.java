package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceList {

    private String priceId;
    private String skuId;
    private String regionCode;
    private String channel;
    private String priceType;
    private BigDecimal basePrice;
    private BigDecimal priceFloor;
    private String currencyCode;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String status;

    public PriceList() {
    }

    public PriceList(String priceId, String skuId, String regionCode, String channel, String priceType,
                     BigDecimal basePrice, BigDecimal priceFloor, String currencyCode,
                     LocalDateTime effectiveFrom, LocalDateTime effectiveTo, String status) {
        this.priceId = priceId;
        this.skuId = skuId;
        this.regionCode = regionCode;
        this.channel = channel;
        this.priceType = priceType;
        this.basePrice = basePrice;
        this.priceFloor = priceFloor;
        this.currencyCode = currencyCode;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.status = status;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getPriceFloor() {
        return priceFloor;
    }

    public void setPriceFloor(BigDecimal priceFloor) {
        this.priceFloor = priceFloor;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
