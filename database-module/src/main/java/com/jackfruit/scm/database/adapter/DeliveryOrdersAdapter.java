package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.Shipment;
import java.util.List;
import java.util.Optional;

public class DeliveryOrdersAdapter {

    private final SupplyChainDatabaseFacade facade;

    public DeliveryOrdersAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createDeliveryOrder(Shipment shipment) {
        facade.deliveryOrders().createDeliveryOrder(shipment);
    }

    public void updateDeliveryOrder(Shipment shipment) {
        facade.deliveryOrders().updateDeliveryOrder(shipment);
    }

    public Optional<Shipment> getDeliveryOrder(String shipmentId) {
        return facade.deliveryOrders().getDeliveryOrder(shipmentId);
    }

    public List<Shipment> listDeliveryOrders() {
        return facade.deliveryOrders().listDeliveryOrders();
    }
}
