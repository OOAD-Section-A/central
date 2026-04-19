package com.scm.dashboard.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import wms.Main;
import wms.contracts.IWMSRepository;
import wms.integration.database.IWMSDatabaseLayer;
import wms.integration.database.WMSDatabaseLayerFactory;
import wms.services.WarehouseFacade;
import wms.services.VendorSelectionEngine;
import wms.models.*;
import wms.commands.*;
import java.util.Arrays;

public class WarehousePanel extends JPanel {

    private static final Color CARD_BG = new Color(50, 52, 70);
    private static final Color ACCENT = new Color(180, 130, 255);
    private JTextArea consoleArea;

    public WarehousePanel() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topCard = new JPanel(new BorderLayout());
        topCard.setBackground(CARD_BG);
        topCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Warehouse Management Engine");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        
        JPanel actionPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        actionPanel.setOpaque(false);

        JButton btnStock = new JButton("1. Stock Inventory (Beans & Milk)");
        JButton btnTasks = new JButton("2. Schedule Interleaved Tasks");
        JButton btnASN = new JButton("3. Process Advanced ASN (3-Way Match)");
        JButton btnVendor = new JButton("4. Execute Vendor Replenishment");

        styleButton(btnStock);
        styleButton(btnTasks);
        styleButton(btnASN);
        styleButton(btnVendor);

        actionPanel.add(btnStock);
        actionPanel.add(btnTasks);
        actionPanel.add(btnASN);
        actionPanel.add(btnVendor);

        topCard.add(title, BorderLayout.WEST);
        topCard.add(actionPanel, BorderLayout.EAST);
        add(topCard, BorderLayout.NORTH);

        consoleArea = new JTextArea();
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        consoleArea.setBackground(new Color(20, 20, 30));
        consoleArea.setForeground(new Color(200, 200, 220));
        consoleArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        redirectSystemStreams();

        // -------------------------------------------------------------
        // WMS Logic Initialization
        // -------------------------------------------------------------
        IWMSRepository mockDbRepo = new IWMSRepository() {
            public boolean validatePurchaseOrder(String po) { return true; }
            public void recordStockMovement(String sku, String from, String to, int qty) {}
            public boolean isBinAvailable(String binId) { return true; }
        };
        IWMSDatabaseLayer dbLayer = WMSDatabaseLayerFactory.create(mockDbRepo);
        WarehouseFacade wmsFacade = new WarehouseFacade(dbLayer);
        
        Product cannedBeans = new Product("SKU-CANNED-99", "Baked Beans", ProductCategory.DRY_GOODS);
        Product milk = new Product("SKU-DAIRY-1", "Organic Milk", ProductCategory.PERISHABLE_COLD);

        // -------------------------------------------------------------
        // Action Listeners for Interactivity
        // -------------------------------------------------------------
        btnStock.addActionListener(e -> new Thread(() -> {
            System.out.println("\n--- 1. Stocking Inventory ---");
            wmsFacade.receiveAndStoreProduct(cannedBeans, 500);
        }).start());

        btnTasks.addActionListener(e -> new Thread(() -> {
            System.out.println("\n--- 2. Scheduling & Executing Worker Tasks ---");
            wmsFacade.getTaskEngine().scheduleTask(new CycleCountTask(cannedBeans.getSku(), "ZONE-DRY-BIN-99", wmsFacade.getInventoryManager()));
            wmsFacade.getTaskEngine().scheduleTask(new InterleavedTask("Worker-JohnDoe", "Aisle 4, Rack B", "Aisle 4, Rack A"));
            wmsFacade.getTaskEngine().executeAllPendingTasks();
        }).start());

        btnASN.addActionListener(e -> new Thread(() -> {
            System.out.println("\n--- 3. Upgraded Procurement: 3-Way Match ---");
            Supplier dairyFarm = new Supplier("SUP-001", "Green Valley Farms", 5, 0.95);
            PurchaseOrder po = new PurchaseOrder("PO-10023", dairyFarm);
            po.addExpectedItem(milk.getSku(), 50, 2.50); 
            AdvanceShipmentNotice asn = new AdvanceShipmentNotice("ASN-7788", po.getPoNumber(), dairyFarm, "2026-04-20");
            asn.addExpectedItem(milk.getSku(), 50);
            
            wms.controllers.InboundReceivingController dockController = new wms.controllers.InboundReceivingController(wmsFacade);
            dockController.registerASN(asn);
            GRN generatedGrn = dockController.processArrivalWithQC(po, asn, milk, 50, 5);

            SupplierInvoice badInvoice = new SupplierInvoice("INV-99221", po.getPoNumber());
            badInvoice.addItem(milk.getSku(), 50, 3.00); 
            new wms.services.ProcurementService().execute3WayMatch(po, generatedGrn, badInvoice);
        }).start());

        btnVendor.addActionListener(e -> new Thread(() -> {
            System.out.println("\n--- 4. Vendor Selection & Replenishment ---");
            System.out.println("Inventory Alert: Stock is low! Initiating Vendor Selection...");

            Supplier vendorA = new Supplier("SUP-001", "Green Valley Farms", 5, 0.95);
            Supplier vendorB = new Supplier("SUP-002", "National Dairy Corp", 2, 0.80);
            Supplier vendorC = new Supplier("SUP-003", "Local Artisan Milks", 8, 0.99);

            VendorSelectionEngine.SupplierMetrics metricsA = new VendorSelectionEngine.SupplierMetrics(vendorA, 90, 85, 80, 95);
            VendorSelectionEngine.SupplierMetrics metricsB = new VendorSelectionEngine.SupplierMetrics(vendorB, 75, 95, 90, 70);
            VendorSelectionEngine.SupplierMetrics metricsC = new VendorSelectionEngine.SupplierMetrics(vendorC, 98, 60, 70, 90);

            VendorSelectionEngine selectionEngine = new VendorSelectionEngine();
            Supplier winningVendor = selectionEngine.selectBestVendor(Arrays.asList(metricsA, metricsB, metricsC));
            
            System.out.println("Action: Auto-generating Replenishment Purchase Order with " + winningVendor.getName());
        }).start());
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(280, 25));
    }

    private void updateConsole(final String text) {
        SwingUtilities.invokeLater(() -> {
            consoleArea.append(text);
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        });
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                updateConsole(String.valueOf((char) b));
            }
            @Override
            public void write(byte[] b, int off, int len) {
                updateConsole(new String(b, off, len));
            }
        };
        // PrintStream overrides System.out temporarily for display
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true)); // Redirect errors as well
    }
}
