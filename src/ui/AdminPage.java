package ui;

import database.DataStore;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Admin Dashboard with Animated Background.
 */
public class AdminPage extends JFrame {
    private JTextArea donorStatsArea;
    private JTextArea requestsArea;

    public AdminPage() {
        setTitle("Admin Dashboard - Blood Donor Management");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Animated Background
        GradientPanel bgPanel = new GradientPanel();
        
        // Main Content Card
        JPanel card = GradientPanel.createCard(1100, 600);
        
        // Header
        JLabel headerLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        headerLabel.setForeground(new Color(180, 0, 0));
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(headerLabel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setOpaque(false);

        // Donor Stats Section
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Donor Status & Details"));
        donorStatsArea = new JTextArea();
        donorStatsArea.setEditable(false);
        donorStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        statsPanel.add(new JScrollPane(donorStatsArea), BorderLayout.CENTER);

        // Requests Section
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setOpaque(false);
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Active Blood Requests"));
        requestsArea = new JTextArea("No pending blood requests.");
        requestsArea.setEditable(false);
        requestsPanel.add(new JScrollPane(requestsArea), BorderLayout.CENTER);

        centerPanel.add(statsPanel);
        centerPanel.add(requestsPanel);
        card.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh Data");
        JButton logoutBtn = new JButton("Logout");
        controlPanel.add(refreshBtn);
        controlPanel.add(logoutBtn);
        card.add(controlPanel, BorderLayout.SOUTH);

        // Actions
        refreshBtn.addActionListener(e -> refreshData());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        bgPanel.add(card);
        add(bgPanel);
        refreshData();
    }

    private void refreshData() {
        donorStatsArea.setText("");
        List<Donor> donors = DataStore.donors;
        int activeCount = 0;
        int busyCount = 0;

        donorStatsArea.append("TOTAL DONORS: " + donors.size() + "\n");
        donorStatsArea.append("-----------------------------\n");
        for (Donor d : donors) {
            if (d.isAvailable()) activeCount++;
            else busyCount++;
            donorStatsArea.append(d.toString() + "\n");
        }
        donorStatsArea.append("\nSUMMARY:\n");
        donorStatsArea.append("Available: " + activeCount + "\n");
        donorStatsArea.append("Busy: " + busyCount + "\n");
    }
}
