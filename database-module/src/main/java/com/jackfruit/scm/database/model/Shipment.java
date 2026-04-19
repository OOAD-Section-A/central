package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Shipment {

    private String deliveryId;
    private String orderId;
    private String customerId;
    private String deliveryAddress;
    private String deliveryStatus;
    private LocalDateTime deliveryDate;
    private String deliveryType;
    private BigDecimal deliveryCost;
    private String assignedAgent;
    private String warehouseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Shipment() {
    }

    public Shipment(String deliveryId, String orderId, String customerId, String deliveryAddress,
                    String deliveryStatus, String deliveryType, BigDecimal deliveryCost,
                    String assignedAgent, String warehouseId, LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this(deliveryId, orderId, customerId, deliveryAddress, deliveryStatus, null, deliveryType,
                deliveryCost, assignedAgent, warehouseId, createdAt, updatedAt);
    }

    public Shipment(String deliveryId, String orderId, String customerId, String deliveryAddress,
                    String deliveryStatus, LocalDateTime deliveryDate, String deliveryType, BigDecimal deliveryCost,
                    String assignedAgent, String warehouseId, LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
        this.deliveryDate = deliveryDate;
        this.deliveryType = deliveryType;
        this.deliveryCost = deliveryCost;
        this.assignedAgent = assignedAgent;
        this.warehouseId = warehouseId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(BigDecimal deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
