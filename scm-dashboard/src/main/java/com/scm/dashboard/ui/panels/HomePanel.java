package com.scm.dashboard.ui.panels;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    
    public HomePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        JLabel welcomeMsg = new JLabel("Welcome to the SCM Hub");
        welcomeMsg.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeMsg.setForeground(Color.WHITE);
        welcomeMsg.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel instruct = new JLabel("Use the sidebar to launch integrated subsystems");
        instruct.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        instruct.setForeground(Color.LIGHT_GRAY);
        instruct.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        welcomeMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruct.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(welcomeMsg);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(instruct);
        centerPanel.add(Box.createVerticalGlue());
        
        add(centerPanel, BorderLayout.CENTER);
    }
}
