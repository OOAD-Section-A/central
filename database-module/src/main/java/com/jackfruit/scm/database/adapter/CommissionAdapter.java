package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.CommissionModels.Agent;
import com.jackfruit.scm.database.model.CommissionModels.CommissionHistory;
import com.jackfruit.scm.database.model.CommissionModels.CommissionSale;
import com.jackfruit.scm.database.model.CommissionModels.CommissionTier;
import java.util.List;

public class CommissionAdapter {

    private final SupplyChainDatabaseFacade facade;

    public CommissionAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createAgent(Agent agent) {
        facade.commissions().createAgent(agent);
    }

    public void createSale(CommissionSale sale) {
        facade.commissions().createCommissionSale(sale);
    }

    public void createCommissionTier(CommissionTier tier) {
        facade.commissions().createCommissionTier(tier);
    }

    public void createCommissionHistory(CommissionHistory history) {
        facade.commissions().createCommissionHistory(history);
    }

    public List<Agent> listAgents() {
        return facade.commissions().listAgents();
    }

    public List<CommissionSale> listSales() {
        return facade.commissions().listCommissionSales();
    }

    public List<CommissionTier> listTiers() {
        return facade.commissions().listCommissionTiers();
    }

    public List<CommissionHistory> listCommissionHistory() {
        return facade.commissions().listCommissionHistory();
    }
}
