package com.jackfruit.scm.database.observer;

public interface SupplyChainEventListener<T> {

    void onEvent(T event);
}
