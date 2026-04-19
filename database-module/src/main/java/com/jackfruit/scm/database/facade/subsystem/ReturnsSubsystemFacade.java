package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.ReturnsModels.ProductReturn;
import com.jackfruit.scm.database.model.ReturnsModels.ReturnGrowthStatistic;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class ReturnsSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public ReturnsSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createProductReturn(ProductReturn productReturn) {
        jdbcOperations.update(
                """
                INSERT INTO product_returns
                (return_request_id, order_id, customer_id, product_details, defect_details, customer_feedback,
                 transport_details, warranty_valid_until, return_status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, productReturn.returnRequestId());
                    statement.setString(2, productReturn.orderId());
                    statement.setString(3, productReturn.customerId());
                    statement.setString(4, productReturn.productDetails());
                    statement.setString(5, productReturn.defectDetails());
                    statement.setString(6, productReturn.customerFeedback());
                    statement.setString(7, productReturn.transportDetails());
                    statement.setTimestamp(8, productReturn.warrantyValidUntil() == null ? null : Timestamp.valueOf(productReturn.warrantyValidUntil()));
                    statement.setString(9, productReturn.returnStatus());
                    statement.setTimestamp(10, Timestamp.valueOf(productReturn.createdAt()));
                });
    }

    public List<ProductReturn> listProductReturns() {
        return jdbcOperations.query(
                "SELECT * FROM product_returns",
                resultSet -> new ProductReturn(
                        resultSet.getString("return_request_id"),
                        resultSet.getString("order_id"),
                        resultSet.getString("customer_id"),
                        resultSet.getString("product_details"),
                        resultSet.getString("defect_details"),
                        resultSet.getString("customer_feedback"),
                        resultSet.getString("transport_details"),
                        resultSet.getTimestamp("warranty_valid_until") == null ? null : resultSet.getTimestamp("warranty_valid_until").toLocalDateTime(),
                        resultSet.getString("return_status"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void updateDefectDetails(String returnRequestId, String defectDetails) {
        jdbcOperations.update(
                "UPDATE product_returns SET defect_details = ? WHERE return_request_id = ?",
                statement -> {
                    statement.setString(1, defectDetails);
                    statement.setString(2, returnRequestId);
                });
    }

    public void updateCustomerFeedback(String returnRequestId, String customerFeedback) {
        jdbcOperations.update(
                "UPDATE product_returns SET customer_feedback = ? WHERE return_request_id = ?",
                statement -> {
                    statement.setString(1, customerFeedback);
                    statement.setString(2, returnRequestId);
                });
    }

    public void createReturnGrowthStatistic(ReturnGrowthStatistic statistic) {
        jdbcOperations.update(
                "INSERT INTO return_growth_statistics (growth_stat_id, return_request_id, metric_period, return_rate, resolution_rate, recorded_at) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, statistic.growthStatId());
                    statement.setString(2, statistic.returnRequestId());
                    statement.setString(3, statistic.metricPeriod());
                    statement.setBigDecimal(4, statistic.returnRate());
                    statement.setBigDecimal(5, statistic.resolutionRate());
                    statement.setTimestamp(6, Timestamp.valueOf(statistic.recordedAt()));
                });
    }

    public List<ReturnGrowthStatistic> listReturnGrowthStatistics() {
        return jdbcOperations.query(
                "SELECT * FROM return_growth_statistics",
                resultSet -> new ReturnGrowthStatistic(
                        resultSet.getString("growth_stat_id"),
                        resultSet.getString("return_request_id"),
                        resultSet.getString("metric_period"),
                        resultSet.getBigDecimal("return_rate"),
                        resultSet.getBigDecimal("resolution_rate"),
                        resultSet.getTimestamp("recorded_at").toLocalDateTime()));
    }
}
