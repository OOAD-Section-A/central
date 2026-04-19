package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.dao.BarcodeEventDao;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import com.jackfruit.scm.database.observer.StockChangeEvent;
import com.jackfruit.scm.database.observer.StockChangeSubject;
import com.jackfruit.scm.database.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class EventIngestionService {

    private final BarcodeEventDao barcodeEventDao;
    private final StockChangeSubject stockChangeSubject;

    public EventIngestionService(BarcodeEventDao barcodeEventDao, StockChangeSubject stockChangeSubject) {
        this.barcodeEventDao = barcodeEventDao;
        this.stockChangeSubject = stockChangeSubject;
    }

    public void ingestEvent(BarcodeRfidEvent event) {
        validateEvent(event);
        barcodeEventDao.save(event);
        stockChangeSubject.notifyListeners(new StockChangeEvent(event.getProductId(), event.getWarehouseId(), 1, event.getEventType()));
    }

    public void updateEvent(BarcodeRfidEvent event) {
        validateEvent(event);
        barcodeEventDao.update(event);
    }

    public Optional<BarcodeRfidEvent> getEvent(String eventId) {
        ValidationUtils.requireText(eventId, "eventId");
        return barcodeEventDao.findById(eventId);
    }

    public List<BarcodeRfidEvent> getEvents() {
        return barcodeEventDao.findAll();
    }

    private void validateEvent(BarcodeRfidEvent event) {
        ValidationUtils.requireText(event.getEventId(), "eventId");
        ValidationUtils.requireText(event.getProductId(), "productId");
        ValidationUtils.requireText(event.getEventType(), "eventType");
        ValidationUtils.requireText(event.getSourceDevice(), "sourceDevice");
        if (event.getEventTimestamp() == null) {
            throw new IllegalArgumentException("eventTimestamp cannot be null");
        }
    }
}
