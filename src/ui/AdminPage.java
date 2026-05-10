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
    private JPanel usersContainer, donorsContainer, requestsContainer;
    private JLabel donorStatsLbl, userStatsLbl, requestStatsLbl;
    private JTextField searchField;
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font fieldFont = new Font("Dialog", Font.PLAIN, 18);

    public AdminPage() {
        setTitle("Admin Command Center - Blood Link");
        setSize(1280, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1200, 680);
        
        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("System Administrator Control Panel", SwingConstants.CENTER);
        headerLabel.setIcon(new VectorIcon(VectorIcon.Type.SHIELD, 40));
        headerLabel.setForeground(new Color(180, 0, 0));
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        topPanel.add(headerLabel, BorderLayout.CENTER);

        // Stats Dashboard
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        donorStatsLbl = createStatBox("Donor Statistics", statsPanel);
        userStatsLbl = createStatBox("User Statistics", statsPanel);
        requestStatsLbl = createStatBox("Request Statistics", statsPanel);
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);

        // Tabs and Search (Middle)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchBar.setOpaque(false);
        JLabel findLbl = new JLabel("Find User:"); findLbl.setFont(labelFont);
        searchBar.add(findLbl);
        searchField = new JTextField(20); searchField.setFont(fieldFont);
        searchBar.add(searchField);
        RoundedButton goBtn = new RoundedButton("Search", new Color(70, 70, 70), new Color(100, 100, 100));
        goBtn.setPreferredSize(new Dimension(100, 40));
        searchBar.add(goBtn);
        centerPanel.add(searchBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Dialog", Font.BOLD, 18));

        donorsContainer = createTab(tabs, "Manage Donors", VectorIcon.Type.HEART);
        usersContainer = createTab(tabs, "Manage Users", VectorIcon.Type.USER);
        requestsContainer = createTab(tabs, "System Requests", VectorIcon.Type.EMAIL);

        centerPanel.add(tabs, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);

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
        refreshBtn.addActionListener(e -> {
            DataStore.loadDataFromDatabase();
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

    private JLabel createStatBox(String title, JPanel parent) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(255, 255, 255, 200));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 0, 0), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Dialog", Font.BOLD, 16));
        box.add(titleLbl, BorderLayout.NORTH);

        JLabel contentLbl = new JLabel("Loading...");
        contentLbl.setFont(new Font("Dialog", Font.PLAIN, 15));
        box.add(contentLbl, BorderLayout.CENTER);
        
        parent.add(box);
        return contentLbl;
    }

    private JPanel createTab(JTabbedPane tabs, String title, VectorIcon.Type iconType) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JScrollPane sp = new JScrollPane(container);
        optimizeScroll(sp);
        tabs.addTab(title, new VectorIcon(iconType, 20), sp);
        return container;
    }

    private void optimizeScroll(JScrollPane sp) {
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
    }

    private void updateStats() {
        int totalD = 0, activeD = 0, busyD = 0;
        for (Donor d : DataStore.donors) {
            totalD++;
            if (d.isAvailable()) activeD++; else busyD++;
        }
        donorStatsLbl.setText("<html>Total: <b>" + totalD + "</b> | Active: <font color='green'>" + activeD + "</font> | Busy: <font color='orange'>" + busyD + "</font></html>");

        int totalU = 0, activeU = 0, blockedU = 0;
        for (User u : DataStore.users) {
            if (!(u instanceof Donor)) {
                totalU++;
                if (u.isBlocked()) blockedU++; else activeU++;
            }
        }
        userStatsLbl.setText("<html>Total: <b>" + totalU + "</b> | Active: <font color='green'>" + activeU + "</font> | Blocked: <font color='red'>" + blockedU + "</font></html>");

        int totalR = 0, pendingR = 0, acceptedR = 0;
        for (BloodRequest r : DataStore.bloodRequests) {
            totalR++;
            if (r.getStatus().equals("Pending")) pendingR++;
            else if (r.getStatus().equals("Accepted")) acceptedR++;
        }
        requestStatsLbl.setText("<html>Total: <b>" + totalR + "</b> | Pending: <font color='blue'>" + pendingR + "</font> | Accepted: <font color='green'>" + acceptedR + "</font></html>");
    }

    private void refreshAllData() {
        String filter = searchField.getText().trim().toLowerCase();
        refreshDonors(filter);
        refreshUsers(filter);
        refreshRequests();
        updateStats();
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
        donorsContainer.revalidate(); donorsContainer.repaint();
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
        usersContainer.revalidate(); usersContainer.repaint();
    }

    private void refreshRequests() {
        requestsContainer.removeAll();
        for (BloodRequest req : DataStore.bloodRequests) {
            requestsContainer.add(createRequestRow(req));
            requestsContainer.add(Box.createVerticalStrut(10));
        }
        requestsContainer.revalidate(); requestsContainer.repaint();
    }

    private JPanel createUserRow(User user, boolean isDonor) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(1100, 100));
        row.setPreferredSize(new Dimension(1100, 100));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        String role = isDonor ? "[DONOR]" : "[USER]";
        String status = user.isBlocked() ? "<font color='red'>BLOCKED</font>" : "<font color='green'>ACTIVE</font>";
        JLabel info = new JLabel("<html><font size='5'><b>" + role + " " + user.getName() + "</b><br>" + user.getEmail() + " | " + status + "</font></html>");
        row.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        
        RoundedButton detailsBtn = new RoundedButton("Details", new Color(70, 70, 70), new Color(100, 100, 100));
        RoundedButton blockBtn = new RoundedButton(user.isBlocked() ? "Unblock" : "Block", 
                                                user.isBlocked() ? new Color(40, 167, 69) : new Color(220, 53, 69), 
                                                Color.GRAY);
        RoundedButton deleteBtn = new RoundedButton("Delete", new Color(200, 0, 0), new Color(255, 50, 50));

        Dimension smallBtnSize = new Dimension(110, 45);
        detailsBtn.setPreferredSize(smallBtnSize); blockBtn.setPreferredSize(smallBtnSize); deleteBtn.setPreferredSize(smallBtnSize);

        detailsBtn.addActionListener(e -> showUserDetails(user));
        blockBtn.addActionListener(e -> {
            user.setBlocked(!user.isBlocked());
            DataStore.updateUser(user);
            refreshAllData();
        });
        deleteBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Delete " + user.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                DataStore.deleteUser(user);
                refreshAllData();
            }
        });

        btnPanel.add(detailsBtn); btnPanel.add(blockBtn); btnPanel.add(deleteBtn);
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }

    private JPanel createRequestRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(1100, 100));
        row.setPreferredSize(new Dimension(1100, 100));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel info = new JLabel("<html><font size='5'><b>" + req.getRequesterName() + " -> " + req.getDonorEmail() + "</b><br>" +
                               "Group: " + req.getBloodGroup() + " | Status: " + req.getStatus() + "</font></html>");
        row.add(info, BorderLayout.CENTER);

        RoundedButton detailsBtn = new RoundedButton("View Request", new Color(70, 70, 70), new Color(100, 100, 100));
        detailsBtn.setPreferredSize(new Dimension(160, 45));
        detailsBtn.addActionListener(e -> showRequestDetails(req));
        row.add(detailsBtn, BorderLayout.EAST);
        return row;
    }

    private void showUserDetails(User user) {
        String details = "Name: " + user.getName() + "\nEmail: " + user.getEmail() + "\nRole: " + (user instanceof Donor ? "Donor" : "User") + "\nStatus: " + (user.isBlocked() ? "Blocked" : "Active");
        if (user instanceof Donor) {
            Donor d = (Donor) user;
            details += "\nDonor: " + (d.isAvailable() ? "Available" : "Busy") + "\nGroup: " + d.getBloodGroup();
        }
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.PLAIN, 18));
        JOptionPane.showMessageDialog(this, details, "User Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRequestDetails(BloodRequest req) {
        String details = "From: " + req.getRequesterName() + "\nTo: " + req.getDonorEmail() + "\nGroup: " + req.getBloodGroup() + "\nHospital: " + req.getHospitalName() + "\nStatus: " + req.getStatus();
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.PLAIN, 18));
        JOptionPane.showMessageDialog(this, details, "Request Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
