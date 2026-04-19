package com.jackfruit.scm.database.dao;

import com.jackfruit.scm.database.dao.impl.BarcodeEventDaoImpl;
import com.jackfruit.scm.database.dao.impl.DemandForecastDaoImpl;
import com.jackfruit.scm.database.dao.impl.ExceptionLogDaoImpl;
import com.jackfruit.scm.database.dao.impl.ForecastTimeseriesDaoImpl;
import com.jackfruit.scm.database.dao.impl.OrderDaoImpl;
import com.jackfruit.scm.database.dao.impl.OrderItemDaoImpl;
import com.jackfruit.scm.database.dao.impl.PriceListDaoImpl;
import com.jackfruit.scm.database.dao.impl.ShipmentDaoImpl;
import com.jackfruit.scm.database.dao.impl.WarehouseDaoImpl;

public class DAOFactory {

    public WarehouseDao createWarehouseDao() {
        return new WarehouseDaoImpl();
    }

    public PriceListDao createPriceListDao() {
        return new PriceListDaoImpl();
    }

    public OrderDao createOrderDao() {
        return new OrderDaoImpl();
    }

    public OrderItemDao createOrderItemDao() {
        return new OrderItemDaoImpl();
    }

    public ShipmentDao createShipmentDao() {
        return new ShipmentDaoImpl();
    }

    public DemandForecastDao createDemandForecastDao() {
        return new DemandForecastDaoImpl();
    }

    public BarcodeEventDao createBarcodeEventDao() {
        return new BarcodeEventDaoImpl();
    }

    public ExceptionLogDao createExceptionLogDao() {
        return new ExceptionLogDaoImpl();
    }

    public ForecastTimeseriesDao createForecastTimeseriesDao() {
        return new ForecastTimeseriesDaoImpl();
    }
}
