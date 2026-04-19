package com.jackfruit.scm.database.facade;

import com.jackfruit.scm.database.config.DatabaseConnectionManager;
import com.jackfruit.scm.database.dao.DAOFactory;
import com.jackfruit.scm.database.facade.subsystem.BarcodeSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.CommissionSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.DeliveryMonitoringSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.DeliveryOrdersSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.DemandForecastingSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.ExceptionHandlingSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.InventorySubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.LogisticsSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.OrderFulfillmentSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.OrdersSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.PackagingSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.PricingSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.ReportingSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.ReturnsSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.StockLedgerSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.UiSubsystemFacade;
import com.jackfruit.scm.database.facade.subsystem.WarehouseSubsystemFacade;
import com.jackfruit.scm.database.model.BarcodeRfidEvent;
import com.jackfruit.scm.database.model.DemandForecast;
import com.jackfruit.scm.database.model.Order;
import com.jackfruit.scm.database.model.OrderItem;
import com.jackfruit.scm.database.model.PriceList;
import com.jackfruit.scm.database.model.Shipment;
import com.jackfruit.scm.database.model.SubsystemException;
import com.jackfruit.scm.database.model.Warehouse;
import com.jackfruit.scm.database.observer.ForecastingListener;
import com.jackfruit.scm.database.observer.ShipmentAlertSubject;
import com.jackfruit.scm.database.observer.StockChangeSubject;
import com.jackfruit.scm.database.observer.UiExceptionListener;
import com.jackfruit.scm.database.service.EventIngestionService;
import com.jackfruit.scm.database.service.ExceptionService;
import com.jackfruit.scm.database.service.ForecastService;
import com.jackfruit.scm.database.service.JdbcOperations;
import com.jackfruit.scm.database.service.OrderService;
import com.jackfruit.scm.database.service.PricingService;
import com.jackfruit.scm.database.service.ShipmentService;
import com.jackfruit.scm.database.service.WarehouseService;
import java.util.List;
import java.util.Optional;

public class SupplyChainDatabaseFacade implements AutoCloseable {

    private final WarehouseService warehouseService;
    private final PricingService pricingService;
    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final ForecastService forecastService;
    private final EventIngestionService eventIngestionService;
    private final ExceptionService exceptionService;
    private final PricingSubsystemFacade pricingSubsystemFacade;
    private final WarehouseSubsystemFacade warehouseSubsystemFacade;
    private final ReportingSubsystemFacade reportingSubsystemFacade;
    private final UiSubsystemFacade uiSubsystemFacade;
    private final StockLedgerSubsystemFacade stockLedgerSubsystemFacade;
    private final InventorySubsystemFacade inventorySubsystemFacade;
    private final OrderFulfillmentSubsystemFacade orderFulfillmentSubsystemFacade;
    private final CommissionSubsystemFacade commissionSubsystemFacade;
    private final OrdersSubsystemFacade ordersSubsystemFacade;
    private final DeliveryOrdersSubsystemFacade deliveryOrdersSubsystemFacade;
    private final DeliveryMonitoringSubsystemFacade deliveryMonitoringSubsystemFacade;
    private final DemandForecastingSubsystemFacade demandForecastingSubsystemFacade;
    private final LogisticsSubsystemFacade logisticsSubsystemFacade;
    private final ExceptionHandlingSubsystemFacade exceptionHandlingSubsystemFacade;
    private final PackagingSubsystemFacade packagingSubsystemFacade;
    private final ReturnsSubsystemFacade returnsSubsystemFacade;
    private final BarcodeSubsystemFacade barcodeSubsystemFacade;

    public SupplyChainDatabaseFacade() {
        DAOFactory daoFactory = new DAOFactory();
        StockChangeSubject stockChangeSubject = new StockChangeSubject();
        ShipmentAlertSubject shipmentAlertSubject = new ShipmentAlertSubject();
        JdbcOperations jdbcOperations = new JdbcOperations();

        stockChangeSubject.subscribe(new ForecastingListener());
        shipmentAlertSubject.subscribe(new UiExceptionListener());

        this.warehouseService = new WarehouseService(daoFactory.createWarehouseDao(), stockChangeSubject);
        this.pricingService = new PricingService(daoFactory.createPriceListDao());
        this.orderService = new OrderService(daoFactory.createOrderDao(), daoFactory.createOrderItemDao());
        this.shipmentService = new ShipmentService(daoFactory.createShipmentDao(), shipmentAlertSubject);
        this.forecastService = new ForecastService(daoFactory.createDemandForecastDao());
        this.eventIngestionService = new EventIngestionService(daoFactory.createBarcodeEventDao(), stockChangeSubject);
        this.exceptionService = new ExceptionService(daoFactory.createExceptionLogDao());
        this.pricingSubsystemFacade = new PricingSubsystemFacade(pricingService, jdbcOperations);
        this.warehouseSubsystemFacade = new WarehouseSubsystemFacade(warehouseService, jdbcOperations);
        this.reportingSubsystemFacade = new ReportingSubsystemFacade(jdbcOperations);
        this.uiSubsystemFacade = new UiSubsystemFacade(jdbcOperations, daoFactory.createForecastTimeseriesDao());
        this.stockLedgerSubsystemFacade = new StockLedgerSubsystemFacade(jdbcOperations);
        this.inventorySubsystemFacade = new InventorySubsystemFacade(jdbcOperations);
        this.orderFulfillmentSubsystemFacade = new OrderFulfillmentSubsystemFacade(jdbcOperations);
        this.commissionSubsystemFacade = new CommissionSubsystemFacade(jdbcOperations);
        this.ordersSubsystemFacade = new OrdersSubsystemFacade(orderService);
        this.deliveryOrdersSubsystemFacade = new DeliveryOrdersSubsystemFacade(shipmentService);
        this.deliveryMonitoringSubsystemFacade = new DeliveryMonitoringSubsystemFacade(jdbcOperations);
        this.demandForecastingSubsystemFacade = new DemandForecastingSubsystemFacade(forecastService, jdbcOperations, daoFactory.createForecastTimeseriesDao());
        this.logisticsSubsystemFacade = new LogisticsSubsystemFacade(jdbcOperations);
        this.exceptionHandlingSubsystemFacade = new ExceptionHandlingSubsystemFacade(exceptionService);
        this.packagingSubsystemFacade = new PackagingSubsystemFacade(jdbcOperations);
        this.returnsSubsystemFacade = new ReturnsSubsystemFacade(jdbcOperations);
        this.barcodeSubsystemFacade = new BarcodeSubsystemFacade(eventIngestionService);
    }

    public PricingSubsystemFacade pricing() {
        return pricingSubsystemFacade;
    }

    public WarehouseSubsystemFacade warehouse() {
        return warehouseSubsystemFacade;
    }

    public ReportingSubsystemFacade reporting() {
        return reportingSubsystemFacade;
    }

    public UiSubsystemFacade ui() {
        return uiSubsystemFacade;
    }

    public StockLedgerSubsystemFacade stockLedger() {
        return stockLedgerSubsystemFacade;
    }

    public InventorySubsystemFacade inventory() {
        return inventorySubsystemFacade;
    }

    public OrderFulfillmentSubsystemFacade orderFulfillment() {
        return orderFulfillmentSubsystemFacade;
    }

    public CommissionSubsystemFacade commissions() {
        return commissionSubsystemFacade;
    }

    public OrdersSubsystemFacade orders() {
        return ordersSubsystemFacade;
    }

    public DeliveryOrdersSubsystemFacade deliveryOrders() {
        return deliveryOrdersSubsystemFacade;
    }

    public DeliveryMonitoringSubsystemFacade deliveryMonitoring() {
        return deliveryMonitoringSubsystemFacade;
    }

    public DemandForecastingSubsystemFacade demandForecasting() {
        return demandForecastingSubsystemFacade;
    }

    public LogisticsSubsystemFacade logistics() {
        return logisticsSubsystemFacade;
    }

    public ExceptionHandlingSubsystemFacade exceptions() {
        return exceptionHandlingSubsystemFacade;
    }

    public PackagingSubsystemFacade packaging() {
        return packagingSubsystemFacade;
    }

    public ReturnsSubsystemFacade returns() {
        return returnsSubsystemFacade;
    }

    public BarcodeSubsystemFacade barcode() {
        return barcodeSubsystemFacade;
    }

    public void registerWarehouse(Warehouse warehouse) {
        warehouseService.createWarehouse(warehouse);
    }

    public Optional<Warehouse> getWarehouse(String warehouseId) {
        return warehouseService.getWarehouse(warehouseId);
    }

    public List<Warehouse> getWarehouses() {
        return warehouseService.getWarehouses();
    }

    public void publishPrice(PriceList priceList) {
        pricingService.createPrice(priceList);
    }

    public Optional<PriceList> getPrice(String priceId) {
        return pricingService.getPrice(priceId);
    }

    public void createOrder(Order order) {
        orderService.createOrder(order);
    }

    public void addOrderItem(OrderItem item) {
        orderService.addOrderItem(item);
    }

    public Optional<Order> getOrder(String orderId) {
        return orderService.getOrder(orderId);
    }

    public List<OrderItem> getOrderItems(String orderId) {
        return orderService.getOrderItems(orderId);
    }

    public void createShipment(Shipment shipment) {
        shipmentService.createShipment(shipment);
    }

    public void updateShipment(Shipment shipment) {
        shipmentService.updateShipment(shipment);
    }

    public Optional<Shipment> getShipment(String shipmentId) {
        return shipmentService.getShipment(shipmentId);
    }

    public void recordDemandForecast(DemandForecast forecast) {
        forecastService.createForecast(forecast);
    }

    public void ingestBarcodeEvent(BarcodeRfidEvent event) {
        eventIngestionService.ingestEvent(event);
    }

    public void logSubsystemException(SubsystemException subsystemException) {
        exceptionService.logException(subsystemException);
    }

    @Override
    public void close() {
        DatabaseConnectionManager.getInstance().shutdown();
    }
}
