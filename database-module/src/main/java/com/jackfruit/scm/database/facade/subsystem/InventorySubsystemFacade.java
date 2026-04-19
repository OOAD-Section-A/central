package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.InventoryModels.Product;
import com.jackfruit.scm.database.model.InventoryModels.ProductBatch;
import com.jackfruit.scm.database.model.InventoryModels.ExpiryTracking;
import com.jackfruit.scm.database.model.InventoryModels.StockAdjustment;
import com.jackfruit.scm.database.model.InventoryModels.ReorderManagement;
import com.jackfruit.scm.database.model.InventoryModels.StockReservation;
import com.jackfruit.scm.database.model.InventoryModels.StockFreeze;
import com.jackfruit.scm.database.model.InventoryModels.DeadStock;
import com.jackfruit.scm.database.model.InventoryModels.StockLevel;
import com.jackfruit.scm.database.model.InventoryModels.StockValuation;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class InventorySubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public InventorySubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createProduct(Product product) {
        jdbcOperations.update(
                """
                INSERT INTO products
                (product_id, product_name, sku, category, sub_category, supplier_id, unit_of_measure,
                 zone, reorder_threshold, product_image_reference, storage_conditions, shelf_life_days, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, product.productId());
                    statement.setString(2, product.productName());
                    statement.setString(3, product.sku());
                    statement.setString(4, product.category());
                    statement.setString(5, product.subCategory());
                    statement.setString(6, product.supplierId());
                    statement.setString(7, product.unitOfMeasure());
                    statement.setString(8, product.zone());
                    if (product.reorderThreshold() == null) {
                        statement.setNull(9, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(9, product.reorderThreshold());
                    }
                    statement.setString(10, product.productImageReference());
                    statement.setString(11, product.storageConditions());
                    if (product.shelfLifeDays() == null) {
                        statement.setNull(12, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(12, product.shelfLifeDays());
                    }
                    statement.setTimestamp(13, Timestamp.valueOf(product.createdAt()));
                });
    }

    public List<Product> listProducts() {
        return jdbcOperations.query(
                "SELECT * FROM products",
                resultSet -> new Product(
                        resultSet.getString("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("sku"),
                        resultSet.getString("category"),
                        resultSet.getString("sub_category"),
                        resultSet.getString("supplier_id"),
                        resultSet.getString("unit_of_measure"),
                        resultSet.getString("zone"),
                        (Integer) resultSet.getObject("reorder_threshold"),
                        resultSet.getString("product_image_reference"),
                        resultSet.getString("storage_conditions"),
                        (Integer) resultSet.getObject("shelf_life_days"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createProductBatch(ProductBatch batch) {
        jdbcOperations.update(
                """
                INSERT INTO product_batches
                (batch_id, product_id, lot_id, manufacturing_date, supplier_id, batch_status, linked_sku,
                 quantity_received, receipt_date, expiry_date, lot_status, perishability_flag, received_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, batch.batchId());
                    statement.setString(2, batch.productId());
                    statement.setString(3, batch.lotId());
                    statement.setDate(4, Date.valueOf(batch.manufacturingDate()));
                    statement.setString(5, batch.supplierId());
                    statement.setString(6, batch.batchStatus());
                    statement.setString(7, batch.linkedSku());
                    statement.setObject(8, batch.quantityReceived());
                    statement.setTimestamp(9, batch.receiptDate() == null ? null : Timestamp.valueOf(batch.receiptDate()));
                    statement.setDate(10, batch.expiryDate() == null ? null : Date.valueOf(batch.expiryDate()));
                    statement.setString(11, batch.lotStatus());
                    statement.setObject(12, batch.perishabilityFlag());
                    statement.setTimestamp(13, Timestamp.valueOf(batch.receivedDate()));
                });
    }

    public void createStockLevel(StockLevel stockLevel) {
        jdbcOperations.update(
                """
                INSERT INTO stock_levels
                (stock_level_id, product_id, current_stock_qty, reserved_stock_qty, available_stock_qty,
                 reorder_threshold, reorder_quantity, safety_stock_level, zone_assignment, stock_health_status,
                 snapshot_timestamp, last_updated)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockLevel.stockLevelId());
                    statement.setString(2, stockLevel.productId());
                    statement.setInt(3, stockLevel.currentStockQty());
                    statement.setInt(4, stockLevel.reservedStockQty());
                    statement.setInt(5, stockLevel.availableStockQty());
                    statement.setInt(6, stockLevel.reorderThreshold());
                    statement.setInt(7, stockLevel.reorderQuantity());
                    statement.setInt(8, stockLevel.safetyStockLevel());
                    statement.setString(9, stockLevel.zoneAssignment());
                    statement.setString(10, stockLevel.stockHealthStatus());
                    statement.setTimestamp(11, stockLevel.snapshotTimestamp() == null ? null : Timestamp.valueOf(stockLevel.snapshotTimestamp()));
                    statement.setTimestamp(12, Timestamp.valueOf(stockLevel.lastUpdated()));
                });
    }

    public void updateStockLevel(StockLevel stockLevel) {
        jdbcOperations.update(
                """
                UPDATE stock_levels
                SET product_id = ?, current_stock_qty = ?, reserved_stock_qty = ?, available_stock_qty = ?,
                    reorder_threshold = ?, reorder_quantity = ?, safety_stock_level = ?, zone_assignment = ?,
                    stock_health_status = ?, snapshot_timestamp = ?, last_updated = ?
                WHERE stock_level_id = ?
                """,
                statement -> {
                    statement.setString(1, stockLevel.productId());
                    statement.setInt(2, stockLevel.currentStockQty());
                    statement.setInt(3, stockLevel.reservedStockQty());
                    statement.setInt(4, stockLevel.availableStockQty());
                    statement.setInt(5, stockLevel.reorderThreshold());
                    statement.setInt(6, stockLevel.reorderQuantity());
                    statement.setInt(7, stockLevel.safetyStockLevel());
                    statement.setString(8, stockLevel.zoneAssignment());
                    statement.setString(9, stockLevel.stockHealthStatus());
                    statement.setTimestamp(10, stockLevel.snapshotTimestamp() == null ? null : Timestamp.valueOf(stockLevel.snapshotTimestamp()));
                    statement.setTimestamp(11, Timestamp.valueOf(stockLevel.lastUpdated()));
                    statement.setString(12, stockLevel.stockLevelId());
                });
    }

    public List<StockLevel> listStockLevels() {
        return jdbcOperations.query(
                "SELECT * FROM stock_levels",
                resultSet -> new StockLevel(
                        resultSet.getString("stock_level_id"),
                        resultSet.getString("product_id"),
                        resultSet.getInt("current_stock_qty"),
                        resultSet.getInt("reserved_stock_qty"),
                        resultSet.getInt("available_stock_qty"),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("reorder_quantity"),
                        resultSet.getInt("safety_stock_level"),
                        resultSet.getString("zone_assignment"),
                        resultSet.getString("stock_health_status"),
                        resultSet.getTimestamp("snapshot_timestamp") == null ? null : resultSet.getTimestamp("snapshot_timestamp").toLocalDateTime(),
                        resultSet.getTimestamp("last_updated").toLocalDateTime()));
    }

    public List<ProductBatch> listProductBatches() {
        return jdbcOperations.query(
                "SELECT * FROM product_batches",
                resultSet -> new ProductBatch(
                        resultSet.getString("batch_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("lot_id"),
                        resultSet.getDate("manufacturing_date").toLocalDate(),
                        resultSet.getString("supplier_id"),
                        resultSet.getString("batch_status"),
                        resultSet.getString("linked_sku"),
                        (Integer) resultSet.getObject("quantity_received"),
                        resultSet.getTimestamp("receipt_date") == null ? null : resultSet.getTimestamp("receipt_date").toLocalDateTime(),
                        resultSet.getDate("expiry_date") == null ? null : resultSet.getDate("expiry_date").toLocalDate(),
                        resultSet.getString("lot_status"),
                        (Boolean) resultSet.getObject("perishability_flag"),
                        resultSet.getTimestamp("received_date").toLocalDateTime()));
    }

    public void createExpiryTracking(ExpiryTracking expiryTracking) {
        jdbcOperations.update(
                """
                INSERT INTO expiry_tracking
                (expiry_id, batch_id, expiry_date, days_remaining, expiry_status, alert_flag, expiry_trigger_flag, lot_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, expiryTracking.expiryId());
                    statement.setString(2, expiryTracking.batchId());
                    statement.setDate(3, Date.valueOf(expiryTracking.expiryDate()));
                    statement.setInt(4, expiryTracking.daysRemaining());
                    statement.setString(5, expiryTracking.expiryStatus());
                    statement.setBoolean(6, expiryTracking.alertFlag());
                    statement.setString(7, expiryTracking.expiryTriggerFlag());
                    statement.setString(8, expiryTracking.lotStatus());
                });
    }

    public List<ExpiryTracking> listExpiryTracking() {
        return jdbcOperations.query(
                "SELECT * FROM expiry_tracking",
                resultSet -> new ExpiryTracking(
                        resultSet.getString("expiry_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getDate("expiry_date").toLocalDate(),
                        resultSet.getInt("days_remaining"),
                        resultSet.getString("expiry_status"),
                        resultSet.getBoolean("alert_flag"),
                        resultSet.getString("expiry_trigger_flag"),
                        resultSet.getString("lot_status")));
    }

    public void createStockAdjustment(StockAdjustment adjustment) {
        jdbcOperations.update(
                """
                INSERT INTO stock_adjustments
                (adjustment_id, product_id, batch_id, adjustment_type, quantity_adjusted, reason, adjusted_by,
                 adjusted_at, sku_reference, performer, reason_note, audit_lock_flag, adjustment_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, adjustment.adjustmentId());
                    statement.setString(2, adjustment.productId());
                    statement.setString(3, adjustment.batchId());
                    statement.setString(4, adjustment.adjustmentType());
                    statement.setInt(5, adjustment.quantityAdjusted());
                    statement.setString(6, adjustment.reason());
                    statement.setString(7, adjustment.adjustedBy());
                    statement.setTimestamp(8, Timestamp.valueOf(adjustment.adjustedAt()));
                    statement.setString(9, adjustment.skuReference());
                    statement.setString(10, adjustment.performer());
                    statement.setString(11, adjustment.reasonNote());
                    statement.setBoolean(12, adjustment.auditLockFlag());
                    statement.setTimestamp(13, adjustment.adjustmentDate() == null ? null : Timestamp.valueOf(adjustment.adjustmentDate()));
                });
    }

    public List<StockAdjustment> listStockAdjustments() {
        return jdbcOperations.query(
                "SELECT * FROM stock_adjustments",
                resultSet -> new StockAdjustment(
                        resultSet.getString("adjustment_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getString("adjustment_type"),
                        resultSet.getInt("quantity_adjusted"),
                        resultSet.getString("reason"),
                        resultSet.getString("adjusted_by"),
                        resultSet.getTimestamp("adjusted_at").toLocalDateTime(),
                        resultSet.getString("sku_reference"),
                        resultSet.getString("performer"),
                        resultSet.getString("reason_note"),
                        resultSet.getBoolean("audit_lock_flag"),
                        resultSet.getTimestamp("adjustment_date") == null ? null : resultSet.getTimestamp("adjustment_date").toLocalDateTime()));
    }

    public void createReorderManagement(ReorderManagement reorderManagement) {
        jdbcOperations.update(
                """
                INSERT INTO reorder_management
                (reorder_id, product_id, current_stock, reorder_threshold, reorder_quantity, supplier_id, reorder_status,
                 last_reorder_date, supplier_name, suggested_reorder_qty, order_date, order_reference)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, reorderManagement.reorderId());
                    statement.setString(2, reorderManagement.productId());
                    statement.setInt(3, reorderManagement.currentStock());
                    statement.setInt(4, reorderManagement.reorderThreshold());
                    statement.setInt(5, reorderManagement.reorderQuantity());
                    statement.setString(6, reorderManagement.supplierId());
                    statement.setString(7, reorderManagement.reorderStatus());
                    statement.setTimestamp(8, reorderManagement.lastReorderDate() == null ? null : Timestamp.valueOf(reorderManagement.lastReorderDate()));
                    statement.setString(9, reorderManagement.supplierName());
                    statement.setObject(10, reorderManagement.suggestedReorderQty());
                    statement.setTimestamp(11, reorderManagement.orderDate() == null ? null : Timestamp.valueOf(reorderManagement.orderDate()));
                    statement.setString(12, reorderManagement.orderReference());
                });
    }

    public List<ReorderManagement> listReorderManagement() {
        return jdbcOperations.query(
                "SELECT * FROM reorder_management",
                resultSet -> new ReorderManagement(
                        resultSet.getString("reorder_id"),
                        resultSet.getString("product_id"),
                        resultSet.getInt("current_stock"),
                        resultSet.getInt("reorder_threshold"),
                        resultSet.getInt("reorder_quantity"),
                        resultSet.getString("supplier_id"),
                        resultSet.getString("reorder_status"),
                        resultSet.getTimestamp("last_reorder_date") == null ? null : resultSet.getTimestamp("last_reorder_date").toLocalDateTime(),
                        resultSet.getString("supplier_name"),
                        (Integer) resultSet.getObject("suggested_reorder_qty"),
                        resultSet.getTimestamp("order_date") == null ? null : resultSet.getTimestamp("order_date").toLocalDateTime(),
                        resultSet.getString("order_reference")));
    }

    public void createStockReservation(StockReservation stockReservation) {
        jdbcOperations.update(
                """
                INSERT INTO stock_reservations
                (reservation_id, product_id, order_id, reserved_qty, reservation_status, reserved_at, expiry_time,
                 linked_sku, reserved_quantity)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockReservation.reservationId());
                    statement.setString(2, stockReservation.productId());
                    statement.setString(3, stockReservation.orderId());
                    statement.setInt(4, stockReservation.reservedQty());
                    statement.setString(5, stockReservation.reservationStatus());
                    statement.setTimestamp(6, Timestamp.valueOf(stockReservation.reservedAt()));
                    statement.setTimestamp(7, stockReservation.expiryTime() == null ? null : Timestamp.valueOf(stockReservation.expiryTime()));
                    statement.setString(8, stockReservation.linkedSku());
                    statement.setObject(9, stockReservation.reservedQuantity());
                });
    }

    public List<StockReservation> listStockReservations() {
        return jdbcOperations.query(
                "SELECT * FROM stock_reservations",
                resultSet -> new StockReservation(
                        resultSet.getString("reservation_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("order_id"),
                        resultSet.getInt("reserved_qty"),
                        resultSet.getString("reservation_status"),
                        resultSet.getTimestamp("reserved_at").toLocalDateTime(),
                        resultSet.getTimestamp("expiry_time") == null ? null : resultSet.getTimestamp("expiry_time").toLocalDateTime(),
                        resultSet.getString("linked_sku"),
                        (Integer) resultSet.getObject("reserved_quantity")));
    }

    public void createStockFreeze(StockFreeze stockFreeze) {
        jdbcOperations.update(
                """
                INSERT INTO stock_freeze
                (freeze_id, product_id, batch_id, freeze_status, freeze_reason, frozen_by, frozen_at,
                 freeze_status_flag, reason_for_freeze, freeze_applied_by, freeze_timestamp)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockFreeze.freezeId());
                    statement.setString(2, stockFreeze.productId());
                    statement.setString(3, stockFreeze.batchId());
                    statement.setBoolean(4, stockFreeze.freezeStatus());
                    statement.setString(5, stockFreeze.freezeReason());
                    statement.setString(6, stockFreeze.frozenBy());
                    statement.setTimestamp(7, Timestamp.valueOf(stockFreeze.frozenAt()));
                    statement.setObject(8, stockFreeze.freezeStatusFlag());
                    statement.setString(9, stockFreeze.reasonForFreeze());
                    statement.setString(10, stockFreeze.freezeAppliedBy());
                    statement.setTimestamp(11, stockFreeze.freezeTimestamp() == null ? null : Timestamp.valueOf(stockFreeze.freezeTimestamp()));
                });
    }

    public List<StockFreeze> listStockFreeze() {
        return jdbcOperations.query(
                "SELECT * FROM stock_freeze",
                resultSet -> new StockFreeze(
                        resultSet.getString("freeze_id"),
                        resultSet.getString("product_id"),
                        resultSet.getString("batch_id"),
                        resultSet.getBoolean("freeze_status"),
                        resultSet.getString("freeze_reason"),
                        resultSet.getString("frozen_by"),
                        resultSet.getTimestamp("frozen_at").toLocalDateTime(),
                        (Boolean) resultSet.getObject("freeze_status_flag"),
                        resultSet.getString("reason_for_freeze"),
                        resultSet.getString("freeze_applied_by"),
                        resultSet.getTimestamp("freeze_timestamp") == null ? null : resultSet.getTimestamp("freeze_timestamp").toLocalDateTime()));
    }

    public void createDeadStock(DeadStock deadStock) {
        jdbcOperations.update(
                """
                INSERT INTO dead_stock
                (dead_stock_id, product_id, last_movement_date, stagnant_days, stagnant_quantity, action_flag, action_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, deadStock.deadStockId());
                    statement.setString(2, deadStock.productId());
                    statement.setTimestamp(3, Timestamp.valueOf(deadStock.lastMovementDate()));
                    statement.setInt(4, deadStock.stagnantDays());
                    statement.setInt(5, deadStock.stagnantQuantity());
                    statement.setString(6, deadStock.actionFlag());
                    statement.setString(7, deadStock.actionStatus());
                });
    }

    public List<DeadStock> listDeadStock() {
        return jdbcOperations.query(
                "SELECT * FROM dead_stock",
                resultSet -> new DeadStock(
                        resultSet.getString("dead_stock_id"),
                        resultSet.getString("product_id"),
                        resultSet.getTimestamp("last_movement_date").toLocalDateTime(),
                        resultSet.getInt("stagnant_days"),
                        resultSet.getInt("stagnant_quantity"),
                        resultSet.getString("action_flag"),
                        resultSet.getString("action_status")));
    }

    public void createStockValuation(StockValuation stockValuation) {
        jdbcOperations.update(
                """
                INSERT INTO stock_valuation
                (valuation_id, product_id, unit_cost, total_quantity, total_value, reserved_value, valuation_method,
                 total_inventory_value, reserved_stock_value, dead_stock_value, monthly_writeoff_value,
                 stock_value_by_category, monthly_valuation_trend)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, stockValuation.valuationId());
                    statement.setString(2, stockValuation.productId());
                    statement.setBigDecimal(3, stockValuation.unitCost());
                    statement.setInt(4, stockValuation.totalQuantity());
                    statement.setBigDecimal(5, stockValuation.totalValue());
                    statement.setBigDecimal(6, stockValuation.reservedValue());
                    statement.setString(7, stockValuation.valuationMethod());
                    statement.setBigDecimal(8, stockValuation.totalInventoryValue());
                    statement.setBigDecimal(9, stockValuation.reservedStockValue());
                    statement.setBigDecimal(10, stockValuation.deadStockValue());
                    statement.setBigDecimal(11, stockValuation.monthlyWriteoffValue());
                    statement.setString(12, stockValuation.stockValueByCategory());
                    statement.setString(13, stockValuation.monthlyValuationTrend());
                });
    }

    public List<StockValuation> listStockValuation() {
        return jdbcOperations.query(
                "SELECT * FROM stock_valuation",
                resultSet -> new StockValuation(
                        resultSet.getString("valuation_id"),
                        resultSet.getString("product_id"),
                        resultSet.getBigDecimal("unit_cost"),
                        resultSet.getInt("total_quantity"),
                        resultSet.getBigDecimal("total_value"),
                        resultSet.getBigDecimal("reserved_value"),
                        resultSet.getString("valuation_method"),
                        resultSet.getBigDecimal("total_inventory_value"),
                        resultSet.getBigDecimal("reserved_stock_value"),
                        resultSet.getBigDecimal("dead_stock_value"),
                        resultSet.getBigDecimal("monthly_writeoff_value"),
                        resultSet.getString("stock_value_by_category"),
                        resultSet.getString("monthly_valuation_trend")));
    }
}
