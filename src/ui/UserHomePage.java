package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.User;
import model.BloodRequest;

/**
 * User Homepage with Request Tracking.
 */
public class UserHomePage extends JFrame {
    private User currentUser;
    private JPanel requestsContainer;

    public UserHomePage(User user) {
        this.currentUser = user;
        setTitle("Blood Donor Management - Home");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1100, 600);

        // Header
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(180, 0, 0));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        card.add(welcomeLabel, BorderLayout.NORTH);

        // Main Content Area
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setOpaque(false);

        // Left Side: Navigation Buttons
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton registerBtn = new RoundedButton("Register as a Donor");
        registerBtn.setPreferredSize(new Dimension(250, 100));
        
        JButton searchBtn = new RoundedButton("Search for Blood", new Color(50, 50, 50), new Color(80, 80, 80));
        searchBtn.setPreferredSize(new Dimension(250, 100));

        gbc.gridx = 0; gbc.gridy = 0; navPanel.add(registerBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 1; navPanel.add(searchBtn, gbc);

        // Right Side: My Requests Tracking
        JPanel trackingPanel = new JPanel(new BorderLayout());
        trackingPanel.setOpaque(false);
        trackingPanel.setBorder(BorderFactory.createTitledBorder("My Sent Requests Status"));

        requestsContainer = new JPanel();
        requestsContainer.setLayout(new BoxLayout(requestsContainer, BoxLayout.Y_AXIS));
        requestsContainer.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(requestsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        trackingPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(navPanel);
        mainContent.add(trackingPanel);
        card.add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton logoutBtn = new RoundedButton("Logout Account", new Color(50, 50, 50), new Color(80, 80, 80));
        bottomPanel.add(logoutBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        registerBtn.addActionListener(e -> {
            new RegistrationPage().setVisible(true);
            this.dispose();
        });

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
        
        refreshMyRequests();
    }

    private void refreshMyRequests() {
        requestsContainer.removeAll();
        boolean hasRequests = false;

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getRequesterEmail().equals(currentUser.getEmail())) {
                requestsContainer.add(createTrackingRow(req));
                requestsContainer.add(Box.createVerticalStrut(10));
                hasRequests = true;
            }
        }

        if (!hasRequests) {
            requestsContainer.add(new JLabel("You haven't made any requests yet."));
        }

        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private JPanel createTrackingRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(500, 60));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "blue";
        String info = "<html>Request to: " + req.getDonorEmail() + "<br>Status: <b><font color='" + statusColor + "'>" + req.getStatus() + "</font></b></html>";
        
        row.add(new JLabel(info), BorderLayout.CENTER);
        return row;
    }
}
