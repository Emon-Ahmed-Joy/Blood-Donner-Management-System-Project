package ui;

import database.DataStore;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Donor;
import model.User;
import model.BloodRequest;

/**
 * Admin Dashboard for managing users and requests.
 * @author Emon Ahmed Joy
 */
public class AdminPage extends JFrame {
    private JPanel usersContainer;
    private JPanel donorsContainer;
    private JPanel requestsContainer;
    private JTextField searchField;

    public AdminPage() {
        setTitle("Admin Command Center - Blood Link");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1200, 650);
        
        // Header Panel with Title and Search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("<html><font color='#B40000'>&#9733;</font> System Administrator Control Panel</html>", SwingConstants.CENTER);
        headerLabel.setForeground(new Color(180, 0, 0));
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 28));
        topPanel.add(headerLabel, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Find User (Email):"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        RoundedButton goBtn = new RoundedButton("Search", new Color(70, 70, 70), new Color(100, 100, 100));
        goBtn.setPreferredSize(new Dimension(80, 30));
        searchPanel.add(goBtn);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Dialog", Font.BOLD, 14));

        // Donors Tab
        donorsContainer = new JPanel();
        donorsContainer.setLayout(new BoxLayout(donorsContainer, BoxLayout.Y_AXIS));
        tabs.addTab("<html><font color='red'>&hearts;</font> Manage Donors</html>", new JScrollPane(donorsContainer));

        // General Users Tab
        usersContainer = new JPanel();
        usersContainer.setLayout(new BoxLayout(usersContainer, BoxLayout.Y_AXIS));
        tabs.addTab("Manage General Users", new JScrollPane(usersContainer));

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

        // Listeners
        goBtn.addActionListener(e -> refreshAllData());
        searchField.addActionListener(e -> refreshAllData());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            refreshAllData();
        });
        
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        bgPanel.add(card);
        add(bgPanel);
        refreshAllData();
    }

    private void refreshAllData() {
        String filter = searchField.getText().trim().toLowerCase();
        refreshDonors(filter);
        refreshUsers(filter);
        refreshRequests();
    }

    private void refreshDonors(String filter) {
        donorsContainer.removeAll();
        for (User u : DataStore.users) {
            if (u instanceof Donor) {
                if (filter.isEmpty() || u.getEmail().toLowerCase().contains(filter)) {
                    donorsContainer.add(createUserRow(u, true));
                    donorsContainer.add(Box.createVerticalStrut(10));
                }
            }
        }
        donorsContainer.revalidate();
        donorsContainer.repaint();
    }

    private void refreshUsers(String filter) {
        usersContainer.removeAll();
        for (User u : DataStore.users) {
            if (!(u instanceof Donor)) {
                if (filter.isEmpty() || u.getEmail().toLowerCase().contains(filter)) {
                    usersContainer.add(createUserRow(u, false));
                    usersContainer.add(Box.createVerticalStrut(10));
                }
            }
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

    private JPanel createUserRow(User user, boolean isDonor) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(1100, 75));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        String role = isDonor ? "<font color='red'>&hearts;</font> [DONOR]" : "[USER]";
        String status = user.isBlocked() ? "<font color='red'>X BLOCKED</font>" : "<font color='green'>V ACTIVE</font>";
        JLabel info = new JLabel("<html><b>" + role + " " + user.getName() + "</b><br>" + user.getEmail() + " | Status: " + status + "</html>");
        row.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        
        RoundedButton detailsBtn = new RoundedButton("Details", new Color(70, 70, 70), new Color(100, 100, 100));
        RoundedButton blockBtn = new RoundedButton(user.isBlocked() ? "Unblock" : "Block", 
                                                user.isBlocked() ? new Color(40, 167, 69) : new Color(220, 53, 69), 
                                                Color.GRAY);

        detailsBtn.addActionListener(e -> showUserDetails(user));
        blockBtn.addActionListener(e -> {
            user.setBlocked(!user.isBlocked());
            DataStore.updateUserBlockStatus(user); // ✅ DB তে save করো
            refreshAllData();
        });

        btnPanel.add(detailsBtn);
        btnPanel.add(blockBtn);
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }

    private JPanel createRequestRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(1100, 75));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        String statusIcon = req.getStatus().equals("Accepted") ? "V" : req.getStatus().equals("Declined") ? "X" : "?";
        JLabel info = new JLabel("<html>" + statusIcon + " <b>" + req.getRequesterName() + " -> " + req.getDonorEmail() + "</b><br>" +
                               "Group: " + req.getBloodGroup() + " | Status: " + req.getStatus() + "</html>");
        row.add(info, BorderLayout.CENTER);

        RoundedButton detailsBtn = new RoundedButton("View Request", new Color(70, 70, 70), new Color(100, 100, 100));
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
        JOptionPane.showMessageDialog(this, details, "Blood Request Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
