package com.jackfruit.scm.database.observer;

public class UiExceptionListener implements SupplyChainEventListener<ShipmentAlertEvent> {

    @Override
    public void onEvent(ShipmentAlertEvent event) {
        System.out.println("UI/Exception subsystem notified for shipment " + event.shipmentId()
                + " with status " + event.status());
    }
}
