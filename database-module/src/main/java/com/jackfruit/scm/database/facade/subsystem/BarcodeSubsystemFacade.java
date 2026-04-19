package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import com.jackfruit.scm.database.service.EventIngestionService;
import java.util.List;

public class BarcodeSubsystemFacade {

    private final EventIngestionService eventIngestionService;

    public BarcodeSubsystemFacade(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
    }

    public void recordBarcodeEvent(BarcodeRfidEvent event) {
        eventIngestionService.ingestEvent(event);
    }

    public List<BarcodeRfidEvent> listBarcodeEvents() {
        return eventIngestionService.getEvents();
    }
}
