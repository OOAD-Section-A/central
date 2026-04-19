package com.jackfruit.scm.database.observer;

public class ForecastingListener implements SupplyChainEventListener<StockChangeEvent> {

    @Override
    public void onEvent(StockChangeEvent event) {
        System.out.println("Forecasting subsystem notified for product " + event.productId()
                + " in warehouse " + event.warehouseId());
    }
}
