package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class BarcodeRfidEvent {

    private String eventId;
    private String productId;
    private String rfidTag;
    private String productName;
    private String category;
    private String description;
    private String transactionId;
    private String warehouseId;
    private LocalDateTime eventTimestamp;
    private String status;
    private String source;

    public BarcodeRfidEvent() {
    }

    public BarcodeRfidEvent(String eventId, String productId, String rfidTag, String productName,
                            String category, String description, String transactionId,
                            LocalDateTime eventTimestamp, String status, String source) {
        this.eventId = eventId;
        this.productId = productId;
        this.rfidTag = rfidTag;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.transactionId = transactionId;
        this.eventTimestamp = eventTimestamp;
        this.status = status;
        this.source = source;
    }

    public BarcodeRfidEvent(String eventId, String productId, String eventType, String sourceDevice,
                            String warehouseId, LocalDateTime eventTimestamp, String rawPayload) {
        this(eventId, productId, null, null, null, rawPayload, null, eventTimestamp, eventType, sourceDevice);
        this.warehouseId = warehouseId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public void setRfidTag(String rfidTag) {
        this.rfidTag = rfidTag;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventType() {
        return status;
    }

    public void setEventType(String eventType) {
        this.status = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceDevice() {
        return source;
    }

    public void setSourceDevice(String sourceDevice) {
        this.source = sourceDevice;
    }

    public String getRawPayload() {
        return description;
    }

    public void setRawPayload(String rawPayload) {
        this.description = rawPayload;
    }
}
