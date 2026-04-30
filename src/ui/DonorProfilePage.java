package ui;

import java.awt.*;
import javax.swing.*;
import model.Donor;

/**
 * Profile page for donors.
 */
public class DonorProfilePage extends JFrame {
    private Donor currentDonor;

    public DonorProfilePage(Donor donor) {
        this.currentDonor = donor;
        setTitle("Donor Profile - " + donor.getName());
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(200, 0, 0));
        JLabel titleLabel = new JLabel("Donor Profile", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content
        JPanel mainContent = new JPanel(new GridLayout(1, 2));

        // Left Side: Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("My Details"));
        detailsPanel.setBackground(Color.WHITE);

        detailsPanel.add(createDetailLabel("Name: " + donor.getName()));
        detailsPanel.add(createDetailLabel("Email: " + donor.getEmail()));
        detailsPanel.add(createDetailLabel("Blood Group: " + donor.getBloodGroup()));
        detailsPanel.add(createDetailLabel("Location: " + donor.getLocation() + ", " + donor.getState()));
        detailsPanel.add(createDetailLabel("Status: " + (donor.isAvailable() ? "Available" : "Busy")));

        JButton searchBtn = new JButton("Search for Blood");
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(searchBtn);

        // Right Side: Requests
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Blood Requests"));
        JTextArea requestsArea = new JTextArea("No new requests at the moment.");
        requestsArea.setEditable(false);
        requestsPanel.add(new JScrollPane(requestsArea), BorderLayout.CENTER);

        mainContent.add(detailsPanel);
        mainContent.add(requestsPanel);
        add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JButton logoutBtn = new JButton("Logout");
        add(logoutBtn, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> {
            new UserSearchPage().setVisible(true);
            this.dispose();
        });

        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }
}
