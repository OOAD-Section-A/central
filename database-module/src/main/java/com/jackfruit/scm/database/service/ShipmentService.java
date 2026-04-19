package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.ShipmentDao;
import com.jackfruit.scm.database.model.Shipment;
import com.jackfruit.scm.database.observer.ShipmentAlertEvent;
import com.jackfruit.scm.database.observer.ShipmentAlertSubject;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class ShipmentService {

    private final ShipmentDao shipmentDao;
    private final ShipmentAlertSubject shipmentAlertSubject;

    public ShipmentService(ShipmentDao shipmentDao, ShipmentAlertSubject shipmentAlertSubject) {
        this.shipmentDao = shipmentDao;
        this.shipmentAlertSubject = shipmentAlertSubject;
    }

    public void createShipment(Shipment shipment) {
        validateShipment(shipment);
        shipmentDao.save(shipment);
        notifyIfAttentionRequired(shipment);
    }

    public void updateShipment(Shipment shipment) {
        validateShipment(shipment);
        shipmentDao.update(shipment);
        notifyIfAttentionRequired(shipment);
    }

    public Optional<Shipment> getShipment(String shipmentId) {
        ValidationUtils.requireText(shipmentId, "shipmentId");
        return shipmentDao.findById(shipmentId);
    }

    public List<Shipment> getShipments() {
        return shipmentDao.findAll();
    }

    private void validateShipment(Shipment shipment) {
        ValidationUtils.requireText(shipment.getDeliveryId(), "deliveryId");
        ValidationUtils.requireText(shipment.getOrderId(), "orderId");
        ValidationUtils.requireText(shipment.getCustomerId(), "customerId");
        ValidationUtils.requireText(shipment.getWarehouseId(), "warehouseId");
        ValidationUtils.requireText(shipment.getDeliveryStatus(), "deliveryStatus");
        ValidationUtils.requirePositive(shipment.getDeliveryCost(), "deliveryCost");
        if (shipment.getCreatedAt() == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
    }

    private void notifyIfAttentionRequired(Shipment shipment) {
        String status = shipment.getDeliveryStatus();
        if ("DELAYED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
            shipmentAlertSubject.notifyListeners(
                    new ShipmentAlertEvent(shipment.getDeliveryId(), status, "Shipment requires UI and exception follow-up"));
        }
    }
}
