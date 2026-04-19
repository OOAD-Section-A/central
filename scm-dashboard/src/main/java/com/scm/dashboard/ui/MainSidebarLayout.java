package com.scm.dashboard.ui;

import com.scm.dashboard.ui.panels.HomePanel;
import com.scm.dashboard.ui.panels.PackingPanel;
import com.scm.dashboard.ui.panels.WarehousePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainSidebarLayout extends JFrame {

    private static final Color SIDEBAR_BG = new Color(24, 24, 36);
    private static final Color CONTENT_BG = new Color(30, 30, 46);
    private static final Color ACCENT = new Color(100, 149, 237);
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Map<String, JButton> navButtons = new HashMap<>();

    public MainSidebarLayout() {
        setTitle("SCM Application Hub");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.add(createSidebar(), BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);
        
        contentPanel.add(new HomePanel(), "Home");
        contentPanel.add(new PackingPanel(), "Packing");
        contentPanel.add(new WarehousePanel(), "Warehouse");
        
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
        
        selectNav("Home");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("SCM Unified");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Application Hub");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitle.setForeground(new Color(160, 160, 180));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(title);
        sidebar.add(subtitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        addNavButton(sidebar, "Home", "🏠 Home");
        addNavButton(sidebar, "Packing", "📦 Packing & Receipts");
        addNavButton(sidebar, "Warehouse", "🏭 Warehouse Management");
        
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String cardName, String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 40));
        
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            selectNav(cardName);
        });

        navButtons.put(cardName, btn);
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void selectNav(String cardName) {
        navButtons.values().forEach(b -> {
            b.setForeground(Color.LIGHT_GRAY);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        });
        JButton selected = navButtons.get(cardName);
        if (selected != null) {
            selected.setForeground(ACCENT);
            selected.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
    }
}
