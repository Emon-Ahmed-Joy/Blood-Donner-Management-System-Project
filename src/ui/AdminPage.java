package ui;

import database.DataStore;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Donor;
import model.User;
import model.BloodRequest;

/**
 * Enhanced Admin Dashboard with Moderation Power.
 */
public class AdminPage extends JFrame {
    private JPanel usersContainer;
    private JPanel requestsContainer;

    public AdminPage() {
        setTitle("Admin Command Center - Blood Link");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1200, 650);
        
        JLabel headerLabel = new JLabel("System Administrator Control Panel", SwingConstants.CENTER);
        headerLabel.setForeground(new Color(180, 0, 0));
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(headerLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Users & Donors Tab
        usersContainer = new JPanel();
        usersContainer.setLayout(new BoxLayout(usersContainer, BoxLayout.Y_AXIS));
        tabs.addTab("Manage Users & Donors", new JScrollPane(usersContainer));

        // Blood Requests Tab
        requestsContainer = new JPanel();
        requestsContainer.setLayout(new BoxLayout(requestsContainer, BoxLayout.Y_AXIS));
        tabs.addTab("System Blood Requests", new JScrollPane(requestsContainer));

        card.add(tabs, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        RoundedButton refreshBtn = new RoundedButton("Sync Data");
        RoundedButton logoutBtn = new RoundedButton("Exit System", new Color(50, 50, 50), new Color(80, 80, 80));
        bottom.add(refreshBtn);
        bottom.add(logoutBtn);
        card.add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshAllData());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        bgPanel.add(card);
        add(bgPanel);
        refreshAllData();
    }

    private void refreshAllData() {
        refreshUsers();
        refreshRequests();
    }

    private void refreshUsers() {
        usersContainer.removeAll();
        for (User u : DataStore.users) {
            usersContainer.add(createUserRow(u));
            usersContainer.add(Box.createVerticalStrut(10));
        }
        usersContainer.revalidate();
        usersContainer.repaint();
    }

    private void refreshRequests() {
        requestsContainer.removeAll();
        for (BloodRequest req : DataStore.bloodRequests) {
            requestsContainer.add(createRequestRow(req));
            requestsContainer.add(Box.createVerticalStrut(10));
        }
        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private JPanel createUserRow(User user) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(1100, 70));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        String role = user instanceof Donor ? "[DONOR]" : "[USER]";
        String status = user.isBlocked() ? "<font color='red'>BLOCKED</font>" : "<font color='green'>ACTIVE</font>";
        JLabel info = new JLabel("<html><b>" + role + " " + user.getName() + "</b> (" + user.getEmail() + ") | Status: " + status + "</html>");
        row.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        
        RoundedButton detailsBtn = new RoundedButton("Full Details", new Color(70, 70, 70), new Color(100, 100, 100));
        RoundedButton blockBtn = new RoundedButton(user.isBlocked() ? "Unblock" : "Block User", 
                                                user.isBlocked() ? new Color(40, 167, 69) : new Color(220, 53, 69), 
                                                Color.GRAY);

        detailsBtn.addActionListener(e -> showUserDetails(user));
        blockBtn.addActionListener(e -> {
            user.setBlocked(!user.isBlocked());
            refreshUsers();
        });

        btnPanel.add(detailsBtn);
        btnPanel.add(blockBtn);
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }

    private JPanel createRequestRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(1100, 70));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel info = new JLabel("<html><b>" + req.getRequesterName() + " -> " + req.getDonorEmail() + "</b> (" + req.getBloodGroup() + ") | Status: " + req.getStatus() + "</html>");
        row.add(info, BorderLayout.CENTER);

        RoundedButton detailsBtn = new RoundedButton("View Request Details", new Color(70, 70, 70), new Color(100, 100, 100));
        detailsBtn.addActionListener(e -> showRequestDetails(req));
        row.add(detailsBtn, BorderLayout.EAST);
        return row;
    }

    private void showUserDetails(User user) {
        String details = "Name: " + user.getName() + "\n" +
                         "Email: " + user.getEmail() + "\n" +
                         "Role: " + (user instanceof Donor ? "Donor" : "General User") + "\n";
        if (user instanceof Donor) {
            Donor d = (Donor) user;
            details += "Blood Group: " + d.getBloodGroup() + "\n" +
                       "Location: " + d.getLocation() + ", " + d.getState() + "\n";
        }
        JOptionPane.showMessageDialog(this, details, "User Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRequestDetails(BloodRequest req) {
        String details = "REQUESTER: " + req.getRequesterName() + " (" + req.getRequesterEmail() + ")\n" +
                         "DONOR EMAIL: " + req.getDonorEmail() + "\n" +
                         "BLOOD GROUP: " + req.getBloodGroup() + "\n\n" +
                         "PATIENT: " + req.getPatientName() + "\n" +
                         "HOSPITAL: " + req.getHospitalName() + " (" + req.getLocation() + ")\n" +
                         "CONDITION: " + req.getMedicalCondition();
        JOptionPane.showMessageDialog(this, details, "Blood Request Deep Dive", JOptionPane.INFORMATION_MESSAGE);
    }
}
