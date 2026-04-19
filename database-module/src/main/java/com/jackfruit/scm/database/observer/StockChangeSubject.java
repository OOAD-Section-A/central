package com.jackfruit.scm.database.observer;

import java.util.ArrayList;
import java.util.List;

public class StockChangeSubject {

    private final List<SupplyChainEventListener<StockChangeEvent>> listeners = new ArrayList<>();

    public void subscribe(SupplyChainEventListener<StockChangeEvent> listener) {
        listeners.add(listener);
    }

    public void notifyListeners(StockChangeEvent event) {
        for (SupplyChainEventListener<StockChangeEvent> listener : listeners) {
            listener.onEvent(event);
        }
    }
}
