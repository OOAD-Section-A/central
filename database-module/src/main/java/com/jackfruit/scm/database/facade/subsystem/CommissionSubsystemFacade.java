package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.CommissionModels.Agent;
import com.jackfruit.scm.database.model.CommissionModels.CommissionHistory;
import com.jackfruit.scm.database.model.CommissionModels.CommissionSale;
import com.jackfruit.scm.database.model.CommissionModels.CommissionTier;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class CommissionSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public CommissionSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createAgent(Agent agent) {
        jdbcOperations.update(
                "INSERT INTO agents (agent_id, agent_name, level, parent_agent_id, downstream_agents, status) VALUES (?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, agent.agentId());
                    statement.setString(2, agent.agentName());
                    statement.setInt(3, agent.level());
                    statement.setString(4, agent.parentAgentId());
                    statement.setString(5, agent.downstreamAgents());
                    statement.setString(6, agent.status());
                });
    }

    public List<Agent> listAgents() {
        return jdbcOperations.query(
                "SELECT * FROM agents",
                resultSet -> new Agent(
                        resultSet.getString("agent_id"),
                        resultSet.getString("agent_name"),
                        resultSet.getInt("level"),
                        resultSet.getString("parent_agent_id"),
                        resultSet.getString("downstream_agents"),
                        resultSet.getString("status")));
    }

    public void createCommissionTier(CommissionTier tier) {
        jdbcOperations.update(
                "INSERT INTO commission_tiers (tier_id, tier_level, min_sales, max_sales, commission_pct) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, tier.tierId());
                    statement.setInt(2, tier.tierLevel());
                    statement.setBigDecimal(3, tier.minSales());
                    statement.setBigDecimal(4, tier.maxSales());
                    statement.setBigDecimal(5, tier.commissionPct());
                });
    }

    public List<CommissionSale> listCommissionSales() {
        return jdbcOperations.query(
                "SELECT * FROM commission_sales",
                resultSet -> new CommissionSale(
                        resultSet.getString("sale_id"),
                        resultSet.getString("agent_id"),
                        resultSet.getBigDecimal("sale_amount"),
                        resultSet.getTimestamp("sale_date").toLocalDateTime(),
                        resultSet.getString("status")));
    }

    public List<CommissionTier> listCommissionTiers() {
        return jdbcOperations.query(
                "SELECT * FROM commission_tiers",
                resultSet -> new CommissionTier(
                        resultSet.getString("tier_id"),
                        resultSet.getInt("tier_level"),
                        resultSet.getBigDecimal("min_sales"),
                        resultSet.getBigDecimal("max_sales"),
                        resultSet.getBigDecimal("commission_pct")));
    }

    public void createCommissionSale(CommissionSale sale) {
        jdbcOperations.update(
                "INSERT INTO commission_sales (sale_id, agent_id, sale_amount, sale_date, status) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, sale.saleId());
                    statement.setString(2, sale.agentId());
                    statement.setBigDecimal(3, sale.saleAmount());
                    statement.setTimestamp(4, Timestamp.valueOf(sale.saleDate()));
                    statement.setString(5, sale.status());
                });
    }

    public void createCommissionHistory(CommissionHistory history) {
        jdbcOperations.update(
                "INSERT INTO commission_history (commission_id, agent_id, period_start, period_end, total_sales, tier_breakdown, total_commission, calculated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                statement -> {
                    statement.setString(1, history.commissionId());
                    statement.setString(2, history.agentId());
                    statement.setDate(3, Date.valueOf(history.periodStart()));
                    statement.setDate(4, Date.valueOf(history.periodEnd()));
                    statement.setBigDecimal(5, history.totalSales());
                    statement.setString(6, history.tierBreakdown());
                    statement.setBigDecimal(7, history.totalCommission());
                    statement.setTimestamp(8, Timestamp.valueOf(history.calculatedAt()));
                });
    }

    public List<CommissionHistory> listCommissionHistory() {
        return jdbcOperations.query(
                "SELECT * FROM commission_history",
                resultSet -> new CommissionHistory(
                        resultSet.getString("commission_id"),
                        resultSet.getString("agent_id"),
                        resultSet.getDate("period_start").toLocalDate(),
                        resultSet.getDate("period_end").toLocalDate(),
                        resultSet.getBigDecimal("total_sales"),
                        resultSet.getString("tier_breakdown"),
                        resultSet.getBigDecimal("total_commission"),
                        resultSet.getTimestamp("calculated_at").toLocalDateTime()));
    }
}
