package ui;

import java.awt.*;
import javax.swing.*;
import model.Donor;

/**
 * Donor Profile Page with Animated Background.
 */
public class DonorProfilePage extends JFrame {
    private Donor currentDonor;

    public DonorProfilePage(Donor donor) {
        this.currentDonor = donor;
        setTitle("Donor Profile - " + donor.getName());
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1000, 600);

        // Header
        JLabel titleLabel = new JLabel("Donor Profile Dashboard", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(180, 0, 0));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(titleLabel, BorderLayout.NORTH);

        // Main Content
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 20));
        mainContent.setOpaque(false);

        // Left Side: Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("My Account Details"));

        detailsPanel.add(createDetailLabel("Name: " + donor.getName()));
        detailsPanel.add(createDetailLabel("Email: " + donor.getEmail()));
        detailsPanel.add(createDetailLabel("Blood Group: " + donor.getBloodGroup()));
        detailsPanel.add(createDetailLabel("Location: " + donor.getLocation() + ", " + donor.getState()));
        detailsPanel.add(createDetailLabel("Status: " + (donor.isAvailable() ? "Available to Donate" : "Currently Busy")));

        JButton searchBtn = new JButton("Search for Donors");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(Box.createVerticalStrut(30));
        detailsPanel.add(searchBtn);

        // Right Side: Requests
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setOpaque(false);
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Live Blood Requests"));
        JTextArea requestsArea = new JTextArea("No new requests found in your area.");
        requestsArea.setEditable(false);
        requestsPanel.add(new JScrollPane(requestsArea), BorderLayout.CENTER);

        mainContent.add(detailsPanel);
        mainContent.add(requestsPanel);
        card.add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton logoutBtn = new JButton("Logout");
        bottomPanel.add(logoutBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> {
            new UserSearchPage().setVisible(true);
            this.dispose();
        });

        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        bgPanel.add(card);
        add(bgPanel);
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
}
