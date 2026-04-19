package com.jackfruit.scm.database.observer;

import java.util.ArrayList;
import java.util.List;

public class ShipmentAlertSubject {

    private final List<SupplyChainEventListener<ShipmentAlertEvent>> listeners = new ArrayList<>();

    public void subscribe(SupplyChainEventListener<ShipmentAlertEvent> listener) {
        listeners.add(listener);
    }

    public void notifyListeners(ShipmentAlertEvent event) {
        for (SupplyChainEventListener<ShipmentAlertEvent> listener : listeners) {
            listener.onEvent(event);
        }
    }
}
