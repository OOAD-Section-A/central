package com.scm.dashboard;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.scm.dashboard.ui.MainSidebarLayout;
import javax.swing.SwingUtilities;

/**
 * Entry point for the SCM Unified Dashboard.
 */
public class DashboardApp {

    public static void main(String[] args) {
        // Apply premium FlatLaf Mac Dark theme
        FlatMacDarkLaf.setup();
        
        SwingUtilities.invokeLater(() -> {
            MainSidebarLayout layout = new MainSidebarLayout();
            layout.setVisible(true);
        });
    }
}
