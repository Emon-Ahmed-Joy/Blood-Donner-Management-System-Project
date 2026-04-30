package ui;

import database.DataStore;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Admin Dashboard to manage donors and view requests.
 */
public class AdminPage extends JFrame {
    private JTextArea donorStatsArea;
    private JTextArea requestsArea;

    public AdminPage() {
        setTitle("Admin Dashboard - Blood Donor Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(50, 50, 50));
        JLabel headerLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Content: Stats and Requests
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Donor Stats Section
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Donor Status & Details"));
        donorStatsArea = new JTextArea();
        donorStatsArea.setEditable(false);
        statsPanel.add(new JScrollPane(donorStatsArea), BorderLayout.CENTER);

        // Requests Section
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Active Blood Requests"));
        requestsArea = new JTextArea("No pending blood requests.");
        requestsArea.setEditable(false);
        requestsPanel.add(new JScrollPane(requestsArea), BorderLayout.CENTER);

        centerPanel.add(statsPanel);
        centerPanel.add(requestsPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom: Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Data");
        JButton logoutBtn = new JButton("Logout");
        controlPanel.add(refreshBtn);
        controlPanel.add(logoutBtn);
        add(controlPanel, BorderLayout.SOUTH);

        // Actions
        refreshBtn.addActionListener(e -> refreshData());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

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
