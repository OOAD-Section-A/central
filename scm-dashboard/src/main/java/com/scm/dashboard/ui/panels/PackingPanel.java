package com.scm.dashboard.ui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PackingPanel extends JPanel {

    private static final Color CARD_BG = new Color(50, 52, 70);
    private static final Color ACCENT = new Color(80, 200, 120);

    public PackingPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Packing & Receipt Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea info = new JTextArea(
                "The Packing Subsystem operates as an independent MVC Swing application.\n\n" +
                "Click the button below to launch the dedicated Packing Dashboard.\n" +
                "All actions will synchronize through the shared SCM facade."
        );
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        info.setForeground(Color.LIGHT_GRAY);
        info.setOpaque(false);
        info.setEditable(false);
        info.setWrapStyleWord(true);
        info.setLineWrap(true);
        
        JButton launchBtn = new JButton("Launch Packing Subsystem");
        launchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        launchBtn.setBackground(ACCENT);
        launchBtn.setForeground(Color.BLACK);
        launchBtn.setFocusPainted(false);
        
        launchBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    com.scm.packing.Main.main(new String[0]);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to launch Packing: " + ex.getMessage());
                }
            }).start();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(launchBtn);

        card.add(title, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.NORTH);
    }
}
