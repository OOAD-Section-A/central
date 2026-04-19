package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.ForecastTimeseries;
import com.jackfruit.scm.database.model.UiModels.UiAuditLog;
import com.jackfruit.scm.database.model.UiModels.UiNotification;
import com.jackfruit.scm.database.model.UiModels.UiPanelState;
import com.jackfruit.scm.database.model.UiModels.UiSession;
import com.jackfruit.scm.database.model.UiModels.UiUser;
import java.util.List;

public class UiAdapter {

    private final SupplyChainDatabaseFacade facade;

    public UiAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void createUser(UiUser user) {
        facade.ui().createUser(user);
    }

    public void createSession(UiSession session) {
        facade.ui().createSession(session);
    }

    public void createNotification(UiNotification notification) {
        facade.ui().createNotification(notification);
    }

    public void createAuditLog(UiAuditLog auditLog) {
        facade.ui().createAuditLog(auditLog);
    }

    public void createPanelState(UiPanelState panelState) {
        facade.ui().createPanelState(panelState);
    }

    public List<ForecastTimeseries> listForecastTimeseriesForForecast(String forecastId) {
        return facade.ui().listForecastTimeseriesForForecast(forecastId);
    }

    public ForecastTimeseries getForecastTimeseries(String timeseriesId) {
        return facade.ui().getForecastTimeseries(timeseriesId);
    }

    public List<ForecastTimeseries> listAllForecastTimeseries() {
        return facade.ui().listAllForecastTimeseries();
    }
}
