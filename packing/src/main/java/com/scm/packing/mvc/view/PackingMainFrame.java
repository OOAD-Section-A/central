package com.scm.packing.mvc.view;

import com.scm.packing.mvc.controller.PackingController;
import com.scm.packing.mvc.model.*;
import com.scm.packing.observer.PackingEventType;
import com.scm.packing.observer.PackingObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Main application window for the Packing subsystem — fully featured
 * dashboard with order selection, job tracking, barcode display, and
 * pallet management.
 *
 * <p><b>MVC role — View:</b> Renders model state and forwards user
 * actions to the {@link PackingController}.  Never modifies the model
 * directly.</p>
 *
 * <p><b>Design Pattern – Observer (Behavioral):</b> Registered as a
 * {@link PackingObserver} on the {@link PackingModel}.</p>
 *
 * <p><b>Multithreading / EDT safety:</b> All UI mutations from observer
 * callbacks are wrapped in {@link SwingUtilities#invokeLater}.</p>
 */
public class PackingMainFrame extends JFrame implements PackingObserver {

    // ---------------------------------------------------------------
    // Colour palette (modern dark theme)
    // ---------------------------------------------------------------
    private static final Color BG_DARK       = new Color(30, 30, 46);
    private static final Color BG_SURFACE    = new Color(40, 42, 58);
    private static final Color BG_CARD       = new Color(50, 52, 70);
    private static final Color ACCENT_BLUE   = new Color(100, 149, 237);
    private static final Color ACCENT_GREEN  = new Color(80, 200, 120);
    private static final Color ACCENT_RED    = new Color(230, 90, 90);
    private static final Color ACCENT_ORANGE = new Color(255, 165, 80);
    private static final Color ACCENT_PURPLE = new Color(180, 130, 255);
    private static final Color TEXT_PRIMARY   = new Color(220, 220, 235);
    private static final Color TEXT_SECONDARY = new Color(160, 160, 180);

    // ---------------------------------------------------------------
    // MVC references
    // ---------------------------------------------------------------
    private final PackingModel model;
    private final PackingController controller;

    // ---------------------------------------------------------------
    // Order table
    // ---------------------------------------------------------------
    private DefaultTableModel orderTableModel;
    private JTable orderTable;

    // ---------------------------------------------------------------
    // Job table
    // ---------------------------------------------------------------
    private DefaultTableModel jobTableModel;
    private JTable jobTable;

    // ---------------------------------------------------------------
    // Pallet table
    // ---------------------------------------------------------------
    private DefaultTableModel palletTableModel;
    private JTable palletTable;

    // ---------------------------------------------------------------
    // Log, status, KPIs
    // ---------------------------------------------------------------
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel jobCountLabel;
    private JLabel packedCountLabel;
    private JLabel palletCountLabel;

    // ---------------------------------------------------------------
    // Tabbed pane for right panel
    // ---------------------------------------------------------------
    private JTabbedPane rightTabs;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public PackingMainFrame(PackingModel model, PackingController controller) {
        this.model = model;
        this.controller = controller;
        model.addObserver(this);
        initUI();
        refreshOrderTable();
        refreshJobTable();
        refreshPalletTable();
        updateKpis();
    }

    // ---------------------------------------------------------------
    // UI initialisation
    // ---------------------------------------------------------------

    private void initUI() {
        setTitle("SCM Packing Subsystem — Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1320, 820);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 650));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.getDatabaseLayer().clearAll();
                dispose();
            }
        });

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.add(createHeaderPanel(), BorderLayout.NORTH);
        root.add(createMainPanel(),   BorderLayout.CENTER);
        root.add(createStatusBar(),   BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ---------------------------------------------------------------
    // Header with KPIs
    // ---------------------------------------------------------------

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_SURFACE);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("📦  Packing Station Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel kpiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        kpiPanel.setOpaque(false);
        jobCountLabel    = createKpiBadge("Total Jobs", "0", ACCENT_BLUE);
        packedCountLabel = createKpiBadge("Packed", "0", ACCENT_GREEN);
        palletCountLabel = createKpiBadge("Pallets", "0", ACCENT_PURPLE);
        kpiPanel.add(jobCountLabel);
        kpiPanel.add(packedCountLabel);
        kpiPanel.add(palletCountLabel);
        header.add(kpiPanel, BorderLayout.EAST);
        return header;
    }

    private JLabel createKpiBadge(String label, String value, Color color) {
        JLabel badge = new JLabel(buildKpiHtml(label, value, color));
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        badge.setForeground(TEXT_PRIMARY);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                new EmptyBorder(5, 12, 5, 12)));
        badge.setOpaque(true);
        badge.setBackground(BG_CARD);
        return badge;
    }

    // ---------------------------------------------------------------
    // Main panel — left (orders) + right (tabbed: jobs+pallets + log)
    // ---------------------------------------------------------------

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG_DARK);
        main.setBorder(new EmptyBorder(8, 12, 0, 12));

        JPanel leftPanel = createOrderPanel();
        leftPanel.setPreferredSize(new Dimension(480, 0));

        JPanel rightPanel = createRightPanel();

        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, rightPanel);
        hSplit.setResizeWeight(0.38);
        hSplit.setDividerSize(5);
        hSplit.setDividerLocation(480);
        hSplit.setBorder(null);
        hSplit.setBackground(BG_DARK);

        main.add(hSplit, BorderLayout.CENTER);
        return main;
    }

    // ---------------------------------------------------------------
    // LEFT: Order selection panel
    // ---------------------------------------------------------------

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        // Title + buttons
        JPanel orderToolbar = new JPanel(new BorderLayout());
        orderToolbar.setOpaque(false);

        JLabel orderTitle = new JLabel("  📋 Orders Available for Packing");
        orderTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderTitle.setForeground(TEXT_PRIMARY);
        orderToolbar.add(orderTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnPack = styledButton("▶ Pack Selected", ACCENT_GREEN);
        btnPack.addActionListener(e -> packSelectedOrders());
        JButton btnRefresh = styledButton("↻ Refresh", ACCENT_BLUE);
        btnRefresh.addActionListener(e -> refreshOrderTable());

        JLabel hint = new JLabel("<html><i style='color:#888;font-size:10px;'>Same-customer orders are merged into one package</i></html>");
        btnPanel.add(hint);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnPack);
        orderToolbar.add(btnPanel, BorderLayout.EAST);
        panel.add(orderToolbar, BorderLayout.NORTH);

        // Order table with checkboxes
        String[] orderCols = {"Select", "Order ID", "Customer", "Customer ID", "Items", "Status"};
        orderTableModel = new DefaultTableModel(orderCols, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };

        orderTable = new JTable(orderTableModel);
        styleTable(orderTable);
        orderTable.getColumnModel().getColumn(0).setMaxWidth(50);
        orderTable.getColumnModel().getColumn(0).setMinWidth(50);
        orderTable.getColumnModel().getColumn(4).setMaxWidth(55);

        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createLineBorder(BG_SURFACE, 1, true));
        orderScroll.getViewport().setBackground(BG_CARD);
        panel.add(orderScroll, BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------------
    // RIGHT: Tabbed pane (Jobs tab + Pallets tab) + log
    // ---------------------------------------------------------------

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        // --- Tabbed pane: Jobs + Pallets ---
        rightTabs = new JTabbedPane();
        rightTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        rightTabs.addTab("📦 Packing Jobs", createJobsTab());
        rightTabs.addTab("🔲 Pallets", createPalletsTab());

        // --- Split: tabs (top) + log (bottom) ---
        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                rightTabs, createLogPanel());
        vSplit.setResizeWeight(0.55);
        vSplit.setDividerSize(5);
        vSplit.setBorder(null);
        vSplit.setBackground(BG_DARK);

        panel.add(vSplit, BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------------
    // Jobs tab
    // ---------------------------------------------------------------

    private JPanel createJobsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        // Action buttons
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionBar.setOpaque(false);

        JButton btnBarcode = styledButton("🏷 View Barcode", ACCENT_BLUE);
        btnBarcode.addActionListener(e -> showSelectedBarcode());

        JButton btnPalletize = styledButton("📦 Add to Pallet", ACCENT_PURPLE);
        btnPalletize.addActionListener(e -> palletizeSelected());

        JButton btnUnpack = styledButton("↩ Unpack Selected", ACCENT_ORANGE);
        btnUnpack.addActionListener(e -> unpackSelectedJobs());

        JLabel jobHint = new JLabel("<html><i style='color:#888;font-size:10px;'>Select packed jobs (Ctrl+click) to palletize or view barcodes</i></html>");

        actionBar.add(btnBarcode);
        actionBar.add(btnPalletize);
        actionBar.add(btnUnpack);
        actionBar.add(jobHint);
        panel.add(actionBar, BorderLayout.NORTH);

        // Job table
        String[] columns = {"Job ID", "Order(s)", "Items", "Strategy", "Status", "Progress"};
        jobTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        jobTable = new JTable(jobTableModel);
        styleTable(jobTable);
        jobTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        jobTable.getColumnModel().getColumn(5).setCellRenderer(new ProgressCellRenderer());
        jobTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        jobTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        jobTable.getColumnModel().getColumn(2).setPreferredWidth(45);
        jobTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        jobTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        jobTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Enable multi-row selection
        jobTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(jobTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BG_SURFACE, 1, true));
        scrollPane.getViewport().setBackground(BG_CARD);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------------
    // Pallets tab (dedicated management view)
    // ---------------------------------------------------------------

    private JPanel createPalletsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);

        // Toolbar with action buttons
        JPanel palletToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        palletToolbar.setOpaque(false);

        JButton btnAddJobs = styledButton("+ Add Jobs to Pallet", ACCENT_PURPLE);
        btnAddJobs.addActionListener(e -> addJobsToExistingPallet());

        JButton btnRemoveJob = styledButton("− Remove Job", ACCENT_RED);
        btnRemoveJob.addActionListener(e -> removeJobFromPallet());

        JLabel palletHint = new JLabel("<html><i style='color:#888;font-size:10px;'>Select a pallet row, then use the buttons above to modify it</i></html>");

        palletToolbar.add(btnAddJobs);
        palletToolbar.add(btnRemoveJob);
        palletToolbar.add(palletHint);
        panel.add(palletToolbar, BorderLayout.NORTH);

        // Pallet table
        String[] palletCols = {"Pallet ID", "Jobs", "Job IDs", "Total Weight (kg)", "Capacity"};
        palletTableModel = new DefaultTableModel(palletCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        palletTable = new JTable(palletTableModel);
        styleTable(palletTable);
        palletTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        palletTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        palletTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        palletTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        palletTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        palletTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(palletTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BG_SURFACE, 1, true));
        scrollPane.getViewport().setBackground(BG_CARD);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ---------------------------------------------------------------
    // Log panel
    // ---------------------------------------------------------------

    private JScrollPane createLogPanel() {
        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBackground(new Color(25, 25, 35));
        logArea.setForeground(ACCENT_GREEN);
        logArea.setCaretColor(ACCENT_GREEN);
        logArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(createCardBorder("Activity Log"));
        scrollPane.getViewport().setBackground(new Color(25, 25, 35));
        return scrollPane;
    }

    // ---------------------------------------------------------------
    // Status bar
    // ---------------------------------------------------------------

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_SURFACE);
        bar.setBorder(new EmptyBorder(5, 16, 5, 16));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);

        JLabel versionLabel = new JLabel("SCM Packing v1.0  |  Licensed ✓");
        versionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        versionLabel.setForeground(TEXT_SECONDARY);

        bar.add(statusLabel,  BorderLayout.WEST);
        bar.add(versionLabel, BorderLayout.EAST);
        return bar;
    }

    // ---------------------------------------------------------------
    // Action: Pack selected orders
    // ---------------------------------------------------------------

    private void packSelectedOrders() {
        List<String> selectedIds = new ArrayList<>();
        for (int i = 0; i < orderTableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) orderTableModel.getValueAt(i, 0);
            if (Boolean.TRUE.equals(selected)) {
                selectedIds.add((String) orderTableModel.getValueAt(i, 1));
            }
        }
        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one order to pack.",
                    "No Orders Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int submitted = controller.packSelectedOrders(selectedIds);
        if (submitted > 0) {
            SwingUtilities.invokeLater(this::refreshOrderTable);
        }
    }

    // ---------------------------------------------------------------
    // Action: Palletize selected packed jobs
    // ---------------------------------------------------------------

    private void palletizeSelected() {
        List<String> selectedJobIds = getSelectedJobIds();
        if (selectedJobIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select packed jobs from the table above (Ctrl+click for multiple),\n"
                    + "then click 'Add to Pallet'.",
                    "No Jobs Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ask user: create a new pallet OR add to an existing one?
        List<PackingUnit> existingPallets = model.getAllUnits();
        if (!existingPallets.isEmpty()) {
            String[] options = {"New Pallet", "Add to Existing", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Create a new pallet or add to an existing one?",
                    "Palletize Jobs", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (choice == 1) {
                // Add to existing pallet
                addToExistingPalletDialog(selectedJobIds);
                return;
            } else if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                return; // cancelled
            }
            // choice == 0 falls through to create new pallet
        }

        PackingUnit pallet = controller.createPallet(selectedJobIds);
        if (pallet != null) {
            refreshPalletTable();
            updateKpis();
            rightTabs.setSelectedIndex(1);
            JOptionPane.showMessageDialog(this,
                    "Created Pallet: " + pallet.getUnitId()
                    + "\nPacked jobs: " + pallet.getCurrentSize()
                    + "\nTotal weight: " + String.format("%.2f kg", pallet.getTotalWeightKg()),
                    "Pallet Created", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Shows a dialog to choose an existing pallet and add selected jobs to it.
     */
    private void addToExistingPalletDialog(List<String> jobIds) {
        List<PackingUnit> pallets = model.getAllUnits();
        String[] palletNames = pallets.stream()
                .map(p -> p.getUnitId() + " (" + p.getCurrentSize() + "/" + p.getMaxCapacity() + " jobs)")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a pallet to add jobs to:",
                "Add to Existing Pallet",
                JOptionPane.PLAIN_MESSAGE, null, palletNames,
                palletNames.length > 0 ? palletNames[0] : null);

        if (selected == null) return;

        // Extract pallet ID from the display string
        String palletId = selected.split(" ")[0];
        int added = controller.addToPallet(palletId, jobIds);
        if (added > 0) {
            refreshPalletTable();
            updateKpis();
            rightTabs.setSelectedIndex(1);
        }
    }

    /**
     * Pallets tab action: add packed jobs to a selected pallet.
     * Opens a multi-select dialog showing all packed (non-palletized) jobs.
     */
    private void addJobsToExistingPallet() {
        int selectedRow = palletTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a pallet row first.",
                    "No Pallet Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String palletId = (String) palletTableModel.getValueAt(selectedRow, 0);
        PackingUnit pallet = model.getUnit(palletId);
        if (pallet == null) return;

        // Build list of packed jobs not already on this pallet
        List<PackingJob> candidates = new ArrayList<>();
        for (PackingJob job : model.getAllJobs()) {
            if (job.getStatus() == PackingJobStatus.PACKED && !pallet.containsJob(job.getJobId())) {
                candidates.add(job);
            }
        }

        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No packed jobs available to add to this pallet.",
                    "No Jobs Available", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show a checkbox list of candidates
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (PackingJob job : candidates) {
            JCheckBox cb = new JCheckBox(job.getJobId() + "  (" + job.getOrderId() + ")");
            checkBoxes.add(cb);
            listPanel.add(cb);
        }

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setPreferredSize(new Dimension(350, 200));

        int result = JOptionPane.showConfirmDialog(this, sp,
                "Add jobs to " + palletId, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        List<String> jobIds = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                jobIds.add(candidates.get(i).getJobId());
            }
        }

        if (!jobIds.isEmpty()) {
            int added = controller.addToPallet(palletId, jobIds);
            if (added > 0) {
                refreshPalletTable();
                updateKpis();
            }
        }
    }

    /**
     * Pallets tab action: remove a specific job from the selected pallet.
     */
    private void removeJobFromPallet() {
        int selectedRow = palletTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a pallet row first.",
                    "No Pallet Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String palletId = (String) palletTableModel.getValueAt(selectedRow, 0);
        PackingUnit pallet = model.getUnit(palletId);
        if (pallet == null) return;

        if (pallet.getCurrentSize() == 0) {
            JOptionPane.showMessageDialog(this,
                    "This pallet has no jobs to remove.",
                    "Empty Pallet", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show list of jobs on this pallet
        String[] jobNames = pallet.getJobs().stream()
                .map(j -> j.getJobId() + "  (" + j.getOrderId() + ")")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a job to remove from " + palletId + ":",
                "Remove Job from Pallet",
                JOptionPane.PLAIN_MESSAGE, null, jobNames,
                jobNames.length > 0 ? jobNames[0] : null);

        if (selected == null) return;
        String jobId = selected.split(" ")[0];
        boolean removed = controller.removeFromPallet(palletId, jobId);
        if (removed) {
            refreshPalletTable();
            updateKpis();
        }
    }

    // ---------------------------------------------------------------
    // Action: View barcode
    // ---------------------------------------------------------------

    private void showSelectedBarcode() {
        List<String> selectedJobIds = getSelectedJobIds();
        if (selectedJobIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a packed job to view its barcode.",
                    "No Job Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String jobId = selectedJobIds.get(0);
        BarcodeLabel label = model.getBarcode(jobId);
        if (label == null) {
            JOptionPane.showMessageDialog(this,
                    "No barcode available for job " + jobId + ".\n"
                    + "Only packed jobs have barcodes.",
                    "No Barcode", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showBarcodeDialog(label);
    }

    private void unpackSelectedJobs() {
        List<String> selectedJobIds = getSelectedJobIds();
        if (selectedJobIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select at least one packed job to unpack.",
                    "No Job Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Unpack selected job(s)? This removes their persisted packaging rows\n"
                        + "and makes the source orders packable again.",
                "Confirm Unpack", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        int unpacked = controller.unpackSelectedJobs(selectedJobIds);
        if (unpacked > 0) {
            refreshOrderTable();
            refreshPalletTable();
            updateKpis();
            JOptionPane.showMessageDialog(this,
                    "Unpacked " + unpacked + " job(s).",
                    "Unpack Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private List<String> getSelectedJobIds() {
        List<String> ids = new ArrayList<>();
        int[] rows = jobTable.getSelectedRows();
        for (int row : rows) {
            ids.add((String) jobTableModel.getValueAt(row, 0));
        }
        return ids;
    }

    // ---------------------------------------------------------------
    // Barcode rendering dialog
    // ---------------------------------------------------------------

    private void showBarcodeDialog(BarcodeLabel label) {
        JDialog dialog = new JDialog(this, "Barcode — " + label.getJobId(), true);
        dialog.setSize(520, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel barcodePanel = new BarcodeRenderPanel(label.getBarcodeString());
        barcodePanel.setPreferredSize(new Dimension(460, 120));
        content.add(barcodePanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        infoPanel.setOpaque(false);

        JLabel lbl1 = new JLabel("Job: " + label.getJobId() + "  |  Order(s): " + label.getOrderId());
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lbl2 = new JLabel("Barcode: " + label.getBarcodeString());
        lbl2.setFont(new Font("Consolas", Font.PLAIN, 11));
        lbl2.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lbl3 = new JLabel("Generated: " + label.getGeneratedAt().toString());
        lbl3.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl3.setForeground(Color.GRAY);
        lbl3.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(lbl1);
        infoPanel.add(lbl2);
        infoPanel.add(lbl3);
        content.add(infoPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    // ---------------------------------------------------------------
    // Table refresh helpers
    // ---------------------------------------------------------------

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        List<Order> allOrders = new ArrayList<>(model.getAllOrders());
        allOrders.sort(Comparator.comparing(Order::getCustomerName)
                .thenComparing(Order::getOrderId));

        for (Order order : allOrders) {
            orderTableModel.addRow(new Object[]{
                    false,
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getCustomerId(),
                    order.getItems().size(),
                    order.isPacked() ? "✓ Packed" : "Pending"
            });
        }
    }

    private void refreshJobTable() {
        jobTableModel.setRowCount(0);
        List<PackingJob> allJobs = new ArrayList<>(model.getAllJobs());
        allJobs.sort(Comparator.comparing(PackingJob::getJobId));
        for (PackingJob job : allJobs) {
            addJobRow(job);
        }
    }

    private void refreshPalletTable() {
        palletTableModel.setRowCount(0);
        for (PackingUnit pallet : model.getAllUnits()) {
            palletTableModel.addRow(new Object[]{
                    pallet.getUnitId(),
                    pallet.getCurrentSize(),
                    pallet.getJobIdsSummary(),
                    String.format("%.2f", pallet.getTotalWeightKg()),
                    pallet.getCurrentSize() + "/" + pallet.getMaxCapacity()
            });
        }
    }

    // ---------------------------------------------------------------
    // Observer callback — OBSERVER PATTERN
    // ---------------------------------------------------------------

    @Override
    public void onPackingEvent(PackingEventType eventType, PackingJob job, String message) {
        SwingUtilities.invokeLater(() -> {
            switch (eventType) {
                case JOB_ADDED:
                    addJobRow(job);
                    updateKpis();
                    break;

                case JOB_UPDATED:
                    refreshJobRow(job);
                    updateKpis();
                    if (job.getStatus() == PackingJobStatus.PACKED
                            || job.getStatus() == PackingJobStatus.FAILED) {
                        refreshOrderTable();
                    }
                    break;

                case STATUS_MESSAGE:
                    appendLog(message);
                    if (statusLabel != null) statusLabel.setText(message);
                    break;

                case JOB_REMOVED:
                    removeJobRow(job);
                    refreshOrderTable();
                    refreshPalletTable();
                    updateKpis();
                    break;
            }
        });
    }

    // ---------------------------------------------------------------
    // Job table helpers
    // ---------------------------------------------------------------

    private void addJobRow(PackingJob job) {
        String strategyLabel = deriveStrategyLabel(job);
        jobTableModel.addRow(new Object[]{
                job.getJobId(),
                job.getOrderId(),
                job.getItems().size(),
            strategyLabel,
                job.getStatus().getDisplayLabel(),
                job.getProgress()
        });
    }

    private void refreshJobRow(PackingJob job) {
        for (int i = 0; i < jobTableModel.getRowCount(); i++) {
            if (job.getJobId().equals(jobTableModel.getValueAt(i, 0))) {
                jobTableModel.setValueAt(deriveStrategyLabel(job), i, 3);
                jobTableModel.setValueAt(job.getStatus().getDisplayLabel(), i, 4);
                jobTableModel.setValueAt(job.getProgress(), i, 5);
                return;
            }
        }
    }

    private String deriveStrategyLabel(PackingJob job) {
        boolean hasFragile = false;
        boolean hasStandard = false;

        for (PackingItem item : job.getItems()) {
            if (item.isFragile()) {
                hasFragile = true;
            } else {
                hasStandard = true;
            }
            if (hasFragile && hasStandard) {
                return "Mixed";
            }
        }

        return hasFragile ? "Fragile" : "Standard";
    }

    private void removeJobRow(PackingJob job) {
        if (job == null) return;
        for (int i = 0; i < jobTableModel.getRowCount(); i++) {
            if (job.getJobId().equals(jobTableModel.getValueAt(i, 0))) {
                jobTableModel.removeRow(i);
                return;
            }
        }
    }

    private void updateKpis() {
        List<PackingJob> all = model.getAllJobs();
        long packed = all.stream()
                .filter(j -> j.getStatus() == PackingJobStatus.PACKED).count();

        jobCountLabel.setText(buildKpiHtml("Total Jobs", String.valueOf(all.size()), ACCENT_BLUE));
        packedCountLabel.setText(buildKpiHtml("Packed", String.valueOf(packed), ACCENT_GREEN));
        palletCountLabel.setText(buildKpiHtml("Pallets", String.valueOf(model.getAllUnits().size()), ACCENT_PURPLE));
    }

    // ---------------------------------------------------------------
    // Log helper
    // ---------------------------------------------------------------

    private void appendLog(String message) {
        if (message == null) return;
        String ts = new SimpleDateFormat("HH:mm:ss").format(new Date());
        logArea.append("[" + ts + "]  " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ---------------------------------------------------------------
    // Shared style helpers
    // ---------------------------------------------------------------

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BG_SURFACE);
        table.setSelectionBackground(ACCENT_BLUE.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(BG_SURFACE);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
    }

    private JButton styledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        Color hoverColor = accent.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverColor);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(accent);
            }
        });
        return btn;
    }

    private TitledBorder createCardBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BG_SURFACE, 1, true),
                title, 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), TEXT_SECONDARY);
    }

    private static String buildKpiHtml(String label, String value, Color color) {
        return "<html><span style='color:#aaa;font-size:10px;'>" + label
                + "</span><br><b style='font-size:16px;color:" + toHex(color)
                + ";'>" + value + "</b></html>";
    }

    private static String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ---------------------------------------------------------------
    // Custom cell renderers
    // ---------------------------------------------------------------

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            if (!isSelected) {
                label.setBackground(BG_CARD);
                String status = value != null ? value.toString() : "";
                switch (status) {
                    case "Pending":   label.setForeground(TEXT_SECONDARY); break;
                    case "Packing…":  label.setForeground(ACCENT_BLUE);   break;
                    case "Packed":    label.setForeground(ACCENT_GREEN);   break;
                    case "Failed":    label.setForeground(ACCENT_RED);     break;
                    default:          label.setForeground(TEXT_PRIMARY);   break;
                }
            }
            return label;
        }
    }

    private class ProgressCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private int progress = 0;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            progress = value instanceof Integer ? (int) value : 0;
            setBackground(isSelected ? ACCENT_BLUE.darker() : BG_CARD);
            setToolTipText(progress + "%");
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();
            g.setColor(BG_SURFACE);
            g.fillRoundRect(4, h / 2 - 5, w - 8, 10, 6, 6);
            if (progress > 0) {
                int fillW = (int) ((w - 8) * (progress / 100.0));
                g.setColor(progress >= 100 ? ACCENT_GREEN : ACCENT_BLUE);
                g.fillRoundRect(4, h / 2 - 5, fillW, 10, 6, 6);
            }
            g.setColor(TEXT_PRIMARY);
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String pctText = progress + "%";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(pctText, (w - fm.stringWidth(pctText)) / 2, h / 2 + 4);
        }
    }

    // ---------------------------------------------------------------
    // Barcode rendering panel (Java2D)
    // ---------------------------------------------------------------

    private static class BarcodeRenderPanel extends JPanel {
        private final String barcodeStr;

        BarcodeRenderPanel(String barcodeStr) {
            this.barcodeStr = barcodeStr;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int barAreaH = h - 25;
            byte[] bytes = barcodeStr.getBytes();
            int totalBars = bytes.length * 8;
            double barW = (double) (w - 20) / totalBars;
            double x = 10;

            for (byte b : bytes) {
                for (int bit = 7; bit >= 0; bit--) {
                    g2.setColor(((b >> bit) & 1) == 1 ? Color.BLACK : Color.WHITE);
                    g2.fillRect((int) x, 5, Math.max(1, (int) barW), barAreaH);
                    x += barW;
                }
            }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Consolas", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(barcodeStr, (w - fm.stringWidth(barcodeStr)) / 2, h - 5);
        }
    }
}
