package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.PackagingModels.BundlePromotion;
import com.jackfruit.scm.database.model.PackagingModels.ContractSkuPrice;
import com.jackfruit.scm.database.model.PackagingModels.PackagingJob;
import com.jackfruit.scm.database.model.PackagingModels.PackagingDiscountPolicy;
import com.jackfruit.scm.database.model.PackagingModels.PackagingPromotion;
import com.jackfruit.scm.database.model.PackagingModels.PromotionEligibleSku;
import com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord;
import com.jackfruit.scm.database.model.PackagingModels.RepairRequest;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class PackagingSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public PackagingSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createPackagingJob(PackagingJob job) {
        jdbcOperations.update(
                """
                INSERT INTO packaging_jobs
                (package_id, order_id, quantity, total_amount, discounts, packaging_status, packed_by, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, job.packageId());
                    statement.setString(2, job.orderId());
                    statement.setInt(3, job.quantity());
                    statement.setBigDecimal(4, job.totalAmount());
                    statement.setBigDecimal(5, job.discounts());
                    statement.setString(6, job.packagingStatus());
                    statement.setString(7, job.packedBy());
                    statement.setTimestamp(8, Timestamp.valueOf(job.createdAt()));
                });
    }

    public List<PackagingJob> listPackagingJobs() {
        return jdbcOperations.query(
                "SELECT * FROM packaging_jobs",
                resultSet -> new PackagingJob(
                        resultSet.getString("package_id"),
                        resultSet.getString("order_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getBigDecimal("discounts"),
                        resultSet.getString("packaging_status"),
                        resultSet.getString("packed_by"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createRepairRequest(RepairRequest request) {
        jdbcOperations.update(
                "INSERT INTO repair_requests (request_id, order_id, product_id, defect_details, request_status, requested_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, request.requestId());
                    statement.setString(2, request.orderId());
                    statement.setString(3, request.productId());
                    statement.setString(4, request.defectDetails());
                    statement.setString(5, request.requestStatus());
                    statement.setTimestamp(6, Timestamp.valueOf(request.requestedAt()));
                });
    }

    public void createReceiptRecord(ReceiptRecord record) {
        jdbcOperations.update(
                "INSERT INTO receipt_records (receipt_record_id, order_id, package_id, received_amount, receipt_status, recorded_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, record.receiptRecordId());
                    statement.setString(2, record.orderId());
                    statement.setString(3, record.packageId());
                    statement.setBigDecimal(4, record.receivedAmount());
                    statement.setString(5, record.receiptStatus());
                    statement.setTimestamp(6, Timestamp.valueOf(record.recordedAt()));
                });
    }

    public List<ContractSkuPrice> listContractSkuPrices() {
        return jdbcOperations.query(
                "SELECT * FROM contract_sku_prices",
                resultSet -> new ContractSkuPrice(
                        resultSet.getLong("id"),
                        resultSet.getString("contract_id"),
                        resultSet.getString("sku_id"),
                        resultSet.getBigDecimal("negotiated_price")));
    }

    public List<PackagingDiscountPolicy> listDiscountPolicies() {
        return jdbcOperations.query(
                "SELECT * FROM discount_policies",
                resultSet -> new PackagingDiscountPolicy(
                        resultSet.getString("policy_id"),
                        resultSet.getString("policy_name"),
                        resultSet.getString("stacking_rule"),
                        resultSet.getInt("priority_level"),
                        resultSet.getBigDecimal("max_discount_cap_pct"),
                        resultSet.getInt("perishability_days"),
                        resultSet.getBigDecimal("clearance_discount_pct"),
                        resultSet.getBoolean("is_active")));
    }

    public List<BundlePromotion> listBundlePromotions() {
        return jdbcOperations.query(
                "SELECT * FROM bundle_promotions",
                resultSet -> new BundlePromotion(
                        resultSet.getString("promo_id"),
                        resultSet.getString("promo_name"),
                        resultSet.getBigDecimal("discount_pct"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getBoolean("expired")));
    }

    public List<PackagingPromotion> listPromotions() {
        return jdbcOperations.query(
                "SELECT * FROM promotions",
                resultSet -> new PackagingPromotion(
                        resultSet.getString("promo_id"),
                        resultSet.getString("promo_name"),
                        resultSet.getString("coupon_code"),
                        resultSet.getString("discount_type"),
                        resultSet.getBigDecimal("discount_value"),
                        resultSet.getTimestamp("start_date").toLocalDateTime(),
                        resultSet.getTimestamp("end_date").toLocalDateTime(),
                        resultSet.getString("eligible_sku_ids"),
                        resultSet.getBigDecimal("min_cart_value"),
                        resultSet.getInt("max_uses"),
                        resultSet.getInt("current_use_count"),
                        resultSet.getBoolean("expired")));
    }

    public List<PromotionEligibleSku> listPromotionEligibleSkus() {
        return jdbcOperations.query(
                "SELECT * FROM promotion_eligible_skus",
                resultSet -> new PromotionEligibleSku(
                        resultSet.getLong("id"),
                        resultSet.getString("promo_id"),
                        resultSet.getString("sku_id")));
    }
}
