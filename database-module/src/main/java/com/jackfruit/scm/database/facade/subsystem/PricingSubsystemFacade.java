package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.PricingModels.ContractPricing;
import com.jackfruit.scm.database.model.PricingModels.CustomerSegmentation;
import com.jackfruit.scm.database.model.PricingModels.DiscountPolicy;
import com.jackfruit.scm.database.model.PricingModels.DiscountRuleResult;
import com.jackfruit.scm.database.model.PricingModels.PriceApproval;
import com.jackfruit.scm.database.model.PricingModels.PriceConfiguration;
import com.jackfruit.scm.database.model.PricingModels.Promotion;
import com.jackfruit.scm.database.model.PricingModels.TierDefinition;
import com.jackfruit.scm.database.service.JdbcOperations;
import com.jackfruit.scm.database.service.PricingService;
import java.sql.Timestamp;
import java.util.List;

public class PricingSubsystemFacade {

    private final PricingService pricingService;
    private final JdbcOperations jdbcOperations;

    public PricingSubsystemFacade(PricingService pricingService, JdbcOperations jdbcOperations) {
        this.pricingService = pricingService;
        this.jdbcOperations = jdbcOperations;
    }

    public void publishPrice(PriceList priceList) {
        pricingService.createPrice(priceList);
    }

    public List<PriceList> listPrices() {
        return pricingService.getAllPrices();
    }

    public void createTierDefinition(TierDefinition tierDefinition) {
        jdbcOperations.update(
                "INSERT INTO tier_definitions (tier_id, tier_name, min_spend_threshold, default_discount_pct) VALUES (?, ?, ?, ?)",
                statement -> {
                    statement.setInt(1, tierDefinition.tierId());
                    statement.setString(2, tierDefinition.tierName());
                    statement.setBigDecimal(3, tierDefinition.minSpendThreshold());
                    statement.setBigDecimal(4, tierDefinition.defaultDiscountPct());
                });
    }

    public List<TierDefinition> listTierDefinitions() {
        return jdbcOperations.query(
                "SELECT tier_id, tier_name, min_spend_threshold, default_discount_pct FROM tier_definitions",
                resultSet -> new TierDefinition(
                        resultSet.getInt("tier_id"),
                        resultSet.getString("tier_name"),
                        resultSet.getBigDecimal("min_spend_threshold"),
                        resultSet.getBigDecimal("default_discount_pct")));
    }

    public void createCustomerSegmentation(CustomerSegmentation segmentation) {
        jdbcOperations.update(
                """
                INSERT INTO customer_segmentation
                (segmentation_id, customer_id, cumulative_spend, historical_order_totals, assigned_tier_id, manual_override, override_tier_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, segmentation.segmentationId());
                    statement.setString(2, segmentation.customerId());
                    statement.setBigDecimal(3, segmentation.cumulativeSpend());
                    statement.setBigDecimal(4, segmentation.historicalOrderTotals());
                    statement.setInt(5, segmentation.assignedTierId());
                    statement.setBoolean(6, segmentation.manualOverride());
                    if (segmentation.overrideTierId() == null) {
                        statement.setNull(7, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(7, segmentation.overrideTierId());
                    }
                });
    }

    public void createPriceConfiguration(PriceConfiguration priceConfiguration) {
        jdbcOperations.update(
                """
                INSERT INTO price_configuration
                (price_config_id, sku_id, cogs_value, desired_margin_pct, computed_base_price, product_attributes, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, priceConfiguration.priceConfigId());
                    statement.setString(2, priceConfiguration.skuId());
                    statement.setBigDecimal(3, priceConfiguration.cogsValue());
                    statement.setBigDecimal(4, priceConfiguration.desiredMarginPct());
                    statement.setBigDecimal(5, priceConfiguration.computedBasePrice());
                    statement.setString(6, priceConfiguration.productAttributes());
                    statement.setTimestamp(7, Timestamp.valueOf(priceConfiguration.createdAt()));
                });
    }

    public void createDiscountRuleResult(DiscountRuleResult discountRuleResult) {
        jdbcOperations.update(
                """
                INSERT INTO discount_rule_results
                (order_line_id, order_id, quantity, batch_expiry_date, final_price, applied_discount_pct, discount_breakdown, computed_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, discountRuleResult.orderLineId());
                    statement.setString(2, discountRuleResult.orderId());
                    statement.setInt(3, discountRuleResult.quantity());
                    statement.setTimestamp(4, discountRuleResult.batchExpiryDate() == null ? null : Timestamp.valueOf(discountRuleResult.batchExpiryDate()));
                    statement.setBigDecimal(5, discountRuleResult.finalPrice());
                    statement.setBigDecimal(6, discountRuleResult.appliedDiscountPct());
                    statement.setString(7, discountRuleResult.discountBreakdown());
                    statement.setTimestamp(8, Timestamp.valueOf(discountRuleResult.computedAt()));
                });
    }

    public void createPromotion(Promotion promotion) {
        jdbcOperations.update(
                """
                INSERT INTO promotions
                (promo_id, promo_name, coupon_code, discount_type, discount_value, start_date, end_date,
                 eligible_sku_ids, min_cart_value, max_uses, current_use_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, promotion.promoId());
                    statement.setString(2, promotion.promoName());
                    statement.setString(3, promotion.couponCode());
                    statement.setString(4, promotion.discountType());
                    statement.setBigDecimal(5, promotion.discountValue());
                    statement.setTimestamp(6, Timestamp.valueOf(promotion.startDate()));
                    statement.setTimestamp(7, Timestamp.valueOf(promotion.endDate()));
                    statement.setString(8, promotion.eligibleSkuIds());
                    statement.setBigDecimal(9, promotion.minCartValue());
                    statement.setInt(10, promotion.maxUses());
                    statement.setInt(11, promotion.currentUseCount());
                });
    }

    public List<Promotion> listPromotions() {
        return jdbcOperations.query(
                "SELECT * FROM promotions",
                resultSet -> new Promotion(
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
                        resultSet.getInt("current_use_count")));
    }

    public void createDiscountPolicy(DiscountPolicy discountPolicy) {
        jdbcOperations.update(
                """
                INSERT INTO discount_policies
                (policy_id, policy_name, stacking_rule, priority_level, max_discount_cap_pct, perishability_days, clearance_discount_pct)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, discountPolicy.policyId());
                    statement.setString(2, discountPolicy.policyName());
                    statement.setString(3, discountPolicy.stackingRule());
                    statement.setInt(4, discountPolicy.priorityLevel());
                    statement.setBigDecimal(5, discountPolicy.maxDiscountCapPct());
                    statement.setInt(6, discountPolicy.perishabilityDays());
                    statement.setBigDecimal(7, discountPolicy.clearanceDiscountPct());
                });
    }

    public void createContractPricing(ContractPricing contractPricing) {
        jdbcOperations.update(
                """
                INSERT INTO contract_pricing
                (contract_id, contract_customer_id, contract_sku_id, negotiated_price, contract_start_date, contract_expiry_date, contract_status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, contractPricing.contractId());
                    statement.setString(2, contractPricing.contractCustomerId());
                    statement.setString(3, contractPricing.contractSkuId());
                    statement.setBigDecimal(4, contractPricing.negotiatedPrice());
                    statement.setTimestamp(5, Timestamp.valueOf(contractPricing.contractStartDate()));
                    statement.setTimestamp(6, Timestamp.valueOf(contractPricing.contractExpiryDate()));
                    statement.setString(7, contractPricing.contractStatus());
                });
    }

    public void createPriceApproval(PriceApproval priceApproval) {
        jdbcOperations.update(
                """
                INSERT INTO price_approvals
                (approval_id, request_type, requested_by, requested_discount_amt, justification_text,
                 approving_manager_id, approval_status, approval_timestamp, audit_log_flag, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, priceApproval.approvalId());
                    statement.setString(2, priceApproval.requestType());
                    statement.setString(3, priceApproval.requestedBy());
                    statement.setBigDecimal(4, priceApproval.requestedDiscountAmount());
                    statement.setString(5, priceApproval.justificationText());
                    statement.setString(6, priceApproval.approvingManagerId());
                    statement.setString(7, priceApproval.approvalStatus());
                    statement.setTimestamp(8, priceApproval.approvalTimestamp() == null ? null : Timestamp.valueOf(priceApproval.approvalTimestamp()));
                    statement.setBoolean(9, priceApproval.auditLogFlag());
                    statement.setTimestamp(10, Timestamp.valueOf(priceApproval.createdAt()));
                });
    }
}
