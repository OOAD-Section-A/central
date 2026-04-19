package com.jackfruit.scm.database.observer;

public record StockChangeEvent(String productId, String warehouseId, int quantityDelta, String reason) {
}
