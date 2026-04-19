package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.PricingModels.ContractPricing;
import com.jackfruit.scm.database.model.PricingModels.CustomerSegmentation;
import com.jackfruit.scm.database.model.PricingModels.DiscountPolicy;
import com.jackfruit.scm.database.model.PricingModels.DiscountRuleResult;
import com.jackfruit.scm.database.model.PricingModels.PriceApproval;
import com.jackfruit.scm.database.model.PricingModels.PriceConfiguration;
import com.jackfruit.scm.database.model.PricingModels.Promotion;
import com.jackfruit.scm.database.model.PricingModels.TierDefinition;

public class PricingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public PricingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void publishPrice(PriceList priceList) {
        facade.pricing().publishPrice(priceList);
    }

    public void createTierDefinition(TierDefinition tierDefinition) {
        facade.pricing().createTierDefinition(tierDefinition);
    }

    public void createCustomerSegmentation(CustomerSegmentation segmentation) {
        facade.pricing().createCustomerSegmentation(segmentation);
    }

    public void createPriceConfiguration(PriceConfiguration priceConfiguration) {
        facade.pricing().createPriceConfiguration(priceConfiguration);
    }

    public void createDiscountRuleResult(DiscountRuleResult discountRuleResult) {
        facade.pricing().createDiscountRuleResult(discountRuleResult);
    }

    public void createPromotion(Promotion promotion) {
        facade.pricing().createPromotion(promotion);
    }

    public void createDiscountPolicy(DiscountPolicy discountPolicy) {
        facade.pricing().createDiscountPolicy(discountPolicy);
    }

    public void createContractPricing(ContractPricing contractPricing) {
        facade.pricing().createContractPricing(contractPricing);
    }

    public void createPriceApproval(PriceApproval priceApproval) {
        facade.pricing().createPriceApproval(priceApproval);
    }
}
