package com.jackfruit.scm.database.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class CommissionModels {

    private CommissionModels() {
    }

    public record Agent(String agentId, String agentName, int level,
                        String parentAgentId, String downstreamAgents, String status) {
    }

    public record CommissionSale(String saleId, String agentId, BigDecimal saleAmount,
                                 LocalDateTime saleDate, String status) {
    }

    public record CommissionTier(String tierId, int tierLevel, BigDecimal minSales,
                                 BigDecimal maxSales, BigDecimal commissionPct) {
    }

    public record CommissionHistory(String commissionId, String agentId, LocalDate periodStart,
                                    LocalDate periodEnd, BigDecimal totalSales, String tierBreakdown,
                                    BigDecimal totalCommission, LocalDateTime calculatedAt) {
    }
}
