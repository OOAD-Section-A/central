package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.SubsystemException;
import com.jackfruit.scm.database.service.ExceptionService;
import java.util.List;

public class ExceptionHandlingSubsystemFacade {

    private final ExceptionService exceptionService;

    public ExceptionHandlingSubsystemFacade(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    public void logException(SubsystemException subsystemException) {
        exceptionService.logException(subsystemException);
    }

    public List<SubsystemException> listExceptions() {
        return exceptionService.getExceptions();
    }
}
