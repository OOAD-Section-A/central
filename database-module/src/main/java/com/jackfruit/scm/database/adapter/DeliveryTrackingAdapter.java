package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Shipment;

public class DeliveryTrackingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public DeliveryTrackingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void upsertShipment(Shipment shipment) {
        if (facade.deliveryOrders().getDeliveryOrder(shipment.getDeliveryId()).isPresent()) {
            facade.deliveryOrders().updateDeliveryOrder(shipment);
        } else {
            facade.deliveryOrders().createDeliveryOrder(shipment);
        }
    }
}
