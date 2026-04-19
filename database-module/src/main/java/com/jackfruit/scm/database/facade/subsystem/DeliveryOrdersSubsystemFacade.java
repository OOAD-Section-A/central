package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.Shipment;
import com.jackfruit.scm.database.service.ShipmentService;
import java.util.List;
import java.util.Optional;

public class DeliveryOrdersSubsystemFacade {

    private final ShipmentService shipmentService;

    public DeliveryOrdersSubsystemFacade(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    public void createDeliveryOrder(Shipment shipment) {
        shipmentService.createShipment(shipment);
    }

    public void updateDeliveryOrder(Shipment shipment) {
        shipmentService.updateShipment(shipment);
    }

    public Optional<Shipment> getDeliveryOrder(String shipmentId) {
        return shipmentService.getShipment(shipmentId);
    }

    public List<Shipment> listDeliveryOrders() {
        return shipmentService.getShipments();
    }
}
