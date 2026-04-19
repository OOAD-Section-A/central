package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.LogisticsModels.LogisticsRoute;
import com.jackfruit.scm.database.model.LogisticsModels.LogisticsShipment;
import com.jackfruit.scm.database.model.LogisticsModels.ShipmentAlert;

public class LogisticsAdapter {

    private final SupplyChainDatabaseFacade facade;

    public LogisticsAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createShipment(LogisticsShipment shipment) {
        facade.logistics().createShipment(shipment);
    }

    public void createRoute(LogisticsRoute route) {
        facade.logistics().createRoute(route);
    }

    public void createShipmentAlert(ShipmentAlert alert) {
        facade.logistics().createShipmentAlert(alert);
    }
}
