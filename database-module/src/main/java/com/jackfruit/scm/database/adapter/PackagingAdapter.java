package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.PackagingModels.BundlePromotion;
import com.jackfruit.scm.database.model.PackagingModels.ContractSkuPrice;
import com.jackfruit.scm.database.model.PackagingModels.PackagingJob;
import com.jackfruit.scm.database.model.PackagingModels.PackagingDiscountPolicy;
import com.jackfruit.scm.database.model.PackagingModels.PackagingPromotion;
import com.jackfruit.scm.database.model.PackagingModels.PromotionEligibleSku;
import com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord;
import com.jackfruit.scm.database.model.PackagingModels.RepairRequest;
import java.util.List;

public class PackagingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public PackagingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createPackagingJob(PackagingJob job) {
        facade.packaging().createPackagingJob(job);
    }

    public void createRepairRequest(RepairRequest request) {
        facade.packaging().createRepairRequest(request);
    }

    public void createReceiptRecord(ReceiptRecord record) {
        facade.packaging().createReceiptRecord(record);
    }

    public List<ContractSkuPrice> listContractSkuPrices() {
        return facade.packaging().listContractSkuPrices();
    }

    public List<PackagingDiscountPolicy> listDiscountPolicies() {
        return facade.packaging().listDiscountPolicies();
    }

    public List<BundlePromotion> listBundlePromotions() {
        return facade.packaging().listBundlePromotions();
    }

    public List<PackagingPromotion> listPromotions() {
        return facade.packaging().listPromotions();
    }

    public List<PromotionEligibleSku> listPromotionEligibleSkus() {
        return facade.packaging().listPromotionEligibleSkus();
    }
}
