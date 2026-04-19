package com.jackfruit.scm.database.adapter;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.jackfruit.scm.database.model.SubsystemException;

public class ExceptionHandlingAdapter {

    private final SupplyChainDatabaseFacade facade;

    public ExceptionHandlingAdapter(SupplyChainDatabaseFacade facade) {
        this.facade = facade;
    }

    public void logException(SubsystemException subsystemException) {
        facade.exceptions().logException(subsystemException);
    }
}
