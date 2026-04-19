package com.jackfruit.scm.database.observer;

public record ShipmentAlertEvent(String shipmentId, String status, String message) {
}
