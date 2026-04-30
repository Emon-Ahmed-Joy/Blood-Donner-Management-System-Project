package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Donor;
import model.BloodRequest;

/**
 * Donor Profile Page with Interactive Request Management.
 */
public class DonorProfilePage extends JFrame {
    private Donor currentDonor;
    private JPanel requestsContainer;

    public DonorProfilePage(Donor donor) {
        this.currentDonor = donor;
        setTitle("Donor Profile - " + donor.getName());
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1050, 600);

        // Header
        JLabel titleLabel = new JLabel("Donor Dashboard", SwingConstants.CENTER);
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

        RoundedButton searchBtn = new RoundedButton("Search for Donors");
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(Box.createVerticalStrut(30));
        detailsPanel.add(searchBtn);

        // Right Side: Interactive Requests
        JPanel requestsPanel = new JPanel(new BorderLayout());
        requestsPanel.setOpaque(false);
        requestsPanel.setBorder(BorderFactory.createTitledBorder("Manage Incoming Requests"));

        requestsContainer = new JPanel();
        requestsContainer.setLayout(new BoxLayout(requestsContainer, BoxLayout.Y_AXIS));
        requestsContainer.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(requestsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        requestsPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(detailsPanel);
        mainContent.add(requestsPanel);
        card.add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton logoutBtn = new RoundedButton("Logout Account", new Color(50, 50, 50), new Color(80, 80, 80));
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
        
        refreshRequests();
    }

    private void refreshRequests() {
        requestsContainer.removeAll();
        boolean hasRequests = false;

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getDonorEmail().equals(currentDonor.getEmail())) {
                requestsContainer.add(createRequestRow(req));
                requestsContainer.add(Box.createVerticalStrut(10));
                hasRequests = true;
            }
        }

        if (!hasRequests) {
            JLabel emptyLabel = new JLabel("No requests found at the moment.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            requestsContainer.add(emptyLabel);
        }

        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private JPanel createRequestRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(500, 100));
        row.setPreferredSize(new Dimension(450, 100));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Info
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "black";
        String info = "<html><b>From: " + req.getRequesterName() + "</b><br>" +
                      "Status: <font color='" + statusColor + "'>" + req.getStatus() + "</font></html>";
        JLabel infoLabel = new JLabel(info);
        row.add(infoLabel, BorderLayout.CENTER);

        // Buttons Panel
        if (req.getStatus().equals("Pending")) {
            JPanel btnPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            btnPanel.setOpaque(false);
            
            RoundedButton detailsBtn = new RoundedButton("View Details", new Color(50, 50, 50), new Color(80, 80, 80));
            RoundedButton acceptBtn = new RoundedButton("Accept", new Color(40, 167, 69), new Color(33, 136, 56));
            RoundedButton declineBtn = new RoundedButton("Decline", new Color(220, 53, 69), new Color(200, 35, 51));
            
            detailsBtn.addActionListener(e -> showRequestDetails(req));

            acceptBtn.addActionListener(e -> {
                req.setStatus("Accepted");
                refreshRequests();
            });
            
            declineBtn.addActionListener(e -> {
                req.setStatus("Declined");
                refreshRequests();
            });
            
            btnPanel.add(detailsBtn);
            btnPanel.add(acceptBtn);
            btnPanel.add(declineBtn);
            row.add(btnPanel, BorderLayout.EAST);
        }

        return row;
    }

    private void showRequestDetails(BloodRequest req) {
        String msg = "Patient: " + req.getPatientName() + "\n" +
                     "Hospital: " + req.getHospitalName() + "\n" +
                     "Location: " + req.getLocation() + "\n" +
                     "Condition: " + req.getMedicalCondition();
        JOptionPane.showMessageDialog(this, msg, "Request Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
}
