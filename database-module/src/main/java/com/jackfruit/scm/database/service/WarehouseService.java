package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.WarehouseDao;
import com.jackfruit.scm.database.model.Warehouse;
import com.jackfruit.scm.database.observer.StockChangeEvent;
import com.jackfruit.scm.database.observer.StockChangeSubject;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class WarehouseService {

    private final WarehouseDao warehouseDao;
    private final StockChangeSubject stockChangeSubject;

    public WarehouseService(WarehouseDao warehouseDao, StockChangeSubject stockChangeSubject) {
        this.warehouseDao = warehouseDao;
        this.stockChangeSubject = stockChangeSubject;
    }

    public void createWarehouse(Warehouse warehouse) {
        ValidationUtils.requireText(warehouse.getWarehouseId(), "warehouseId");
        ValidationUtils.requireText(warehouse.getWarehouseName(), "warehouseName");
        warehouseDao.save(warehouse);
        stockChangeSubject.notifyListeners(new StockChangeEvent("N/A", warehouse.getWarehouseId(), 0, "WAREHOUSE_CREATED"));
    }

    public void updateWarehouse(Warehouse warehouse) {
        ValidationUtils.requireText(warehouse.getWarehouseId(), "warehouseId");
        ValidationUtils.requireText(warehouse.getWarehouseName(), "warehouseName");
        warehouseDao.update(warehouse);
    }

    public Optional<Warehouse> getWarehouse(String warehouseId) {
        ValidationUtils.requireText(warehouseId, "warehouseId");
        return warehouseDao.findById(warehouseId);
    }

    public List<Warehouse> getWarehouses() {
        return warehouseDao.findAll();
    }
}
