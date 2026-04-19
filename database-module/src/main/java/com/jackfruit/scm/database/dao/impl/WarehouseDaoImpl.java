package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.WarehouseDao;
import com.jackfruit.scm.database.model.Warehouse;
import java.util.List;
import java.util.Optional;

public class WarehouseDaoImpl extends AbstractJdbcDao implements WarehouseDao {

    @Override
    public void save(Warehouse warehouse) {
        executeUpdate(
                "INSERT INTO warehouses (warehouse_id, warehouse_name) VALUES (?, ?)",
                statement -> {
                    statement.setString(1, warehouse.getWarehouseId());
                    statement.setString(2, warehouse.getWarehouseName());
                });
    }

    @Override
    public void update(Warehouse warehouse) {
        executeUpdate(
                "UPDATE warehouses SET warehouse_name = ? WHERE warehouse_id = ?",
                statement -> {
                    statement.setString(1, warehouse.getWarehouseName());
                    statement.setString(2, warehouse.getWarehouseId());
                });
    }

    @Override
    public Optional<Warehouse> findById(String warehouseId) {
        return queryForObject(
                "SELECT warehouse_id, warehouse_name FROM warehouses WHERE warehouse_id = ?",
                resultSet -> new Warehouse(resultSet.getString("warehouse_id"), resultSet.getString("warehouse_name")),
                statement -> statement.setString(1, warehouseId));
    }

    @Override
    public List<Warehouse> findAll() {
        return queryForList(
                "SELECT warehouse_id, warehouse_name FROM warehouses",
                resultSet -> new Warehouse(resultSet.getString("warehouse_id"), resultSet.getString("warehouse_name")));
    }
}
