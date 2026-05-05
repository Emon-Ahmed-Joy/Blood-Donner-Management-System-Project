package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Donor;
import model.BloodRequest;

/**
 * Donor Profile Page for managing requests.
 * @author Emon Ahmed Joy
 */
public class DonorProfilePage extends JFrame {
    private Donor currentDonor;
    private JPanel incomingContainer;
    private JPanel sentContainer;
    private JLabel statusLabel;

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
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 28));
        card.add(titleLabel, BorderLayout.NORTH);

        // Notification Check
        if (donor.hasUpdate()) {
            JOptionPane.showMessageDialog(this, "(!) You have update in your requests!", "System Notification", JOptionPane.INFORMATION_MESSAGE);
            donor.setHasUpdate(false);
        }

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
        detailsPanel.add(createDetailLabel("<html><font color='red'>&hearts;</font> Blood Group: " + donor.getBloodGroup() + "</html>"));
        detailsPanel.add(createDetailLabel("Location: " + donor.getLocation() + ", " + donor.getState()));
        
        statusLabel = createDetailLabel("Status: " + (donor.isAvailable() ? "Available" : "Busy"));
        detailsPanel.add(statusLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Standard size for all navigation buttons
        Dimension btnSize = new Dimension(220, 45);
        Font btnFont = new Font("Dialog", Font.BOLD, 14);

        RoundedButton editProfileBtn = new RoundedButton("✏️ Edit My Profile", new Color(180, 0, 0), new Color(220, 20, 20));
        editProfileBtn.setPreferredSize(btnSize);
        editProfileBtn.setMaximumSize(btnSize);
        editProfileBtn.setFont(btnFont);
        editProfileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(editProfileBtn);
        detailsPanel.add(Box.createVerticalStrut(10));

        RoundedButton changePassBtn = new RoundedButton("🔒 Change Password", new Color(180, 0, 0), new Color(220, 20, 20));
        changePassBtn.setPreferredSize(btnSize);
        changePassBtn.setMaximumSize(btnSize);
        changePassBtn.setFont(btnFont);
        changePassBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(changePassBtn);
        detailsPanel.add(Box.createVerticalStrut(10));

        RoundedButton searchBtn = new RoundedButton("🔍 Search for Donors", new Color(180, 0, 0), new Color(220, 20, 20));
        searchBtn.setPreferredSize(btnSize);
        searchBtn.setMaximumSize(btnSize);
        searchBtn.setFont(btnFont);
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(searchBtn);

        // Right Side: Tabbed Requests
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Dialog", Font.BOLD, 14));

        // Incoming Tab
        incomingContainer = new JPanel();
        incomingContainer.setLayout(new BoxLayout(incomingContainer, BoxLayout.Y_AXIS));
        incomingContainer.setBackground(Color.WHITE);
        tabs.addTab("Incoming Requests", new JScrollPane(incomingContainer));

        // Outgoing Tab
        sentContainer = new JPanel();
        sentContainer.setLayout(new BoxLayout(sentContainer, BoxLayout.Y_AXIS));
        sentContainer.setBackground(Color.WHITE);
        tabs.addTab("Outgoing Requests", new JScrollPane(sentContainer));

        mainContent.add(detailsPanel);
        mainContent.add(tabs);
        card.add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton logoutBtn = new RoundedButton("Logout Account", new Color(50, 50, 50), new Color(80, 80, 80));
        bottomPanel.add(logoutBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        editProfileBtn.addActionListener(e -> showEditProfileDialog());
        changePassBtn.addActionListener(e -> showChangePasswordDialog());

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
        
        refreshAllRequests();

        // Animation
        bgPanel.fadeIn();
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPasswordField oldPassF = new JPasswordField(20);
        JPasswordField newPassF = new JPasswordField(20);
        JPasswordField confirmPassF = new JPasswordField(20);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1; dialog.add(oldPassF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1; dialog.add(newPassF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; dialog.add(confirmPassF, gbc); r++;

        RoundedButton updateBtn = new RoundedButton("Update Password");
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        dialog.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            String oldPass = new String(oldPassF.getPassword());
            String newPass = new String(newPassF.getPassword());
            String confirmPass = new String(confirmPassF.getPassword());

            if (!oldPass.equals(currentDonor.getPassword())) {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "New password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                currentDonor.setPassword(newPass);
                JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile Details", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(currentDonor.getName());
        JTextField stateF = new JTextField(currentDonor.getState());
        JTextField locF = new JTextField(currentDonor.getLocation());
        JTextField groupF = new JTextField(currentDonor.getBloodGroup());
        JTextField medicalF = new JTextField(currentDonor.getMedicalCondition());
        JCheckBox availCheck = new JCheckBox("Available for Donation", currentDonor.isAvailable());

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; dialog.add(stateF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("City/Location:"), gbc);
        gbc.gridx = 1; dialog.add(locF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1; dialog.add(groupF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("Medical Condition:"), gbc);
        gbc.gridx = 1; dialog.add(medicalF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        dialog.add(availCheck, gbc); r++;

        RoundedButton saveBtn = new RoundedButton("Update My Info");
        gbc.gridy = r;
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            currentDonor.setName(nameF.getText());
            currentDonor.setState(stateF.getText());
            currentDonor.setLocation(locF.getText());
            currentDonor.setBloodGroup(groupF.getText());
            currentDonor.setMedicalCondition(medicalF.getText());
            currentDonor.setAvailable(availCheck.isSelected());
            
            // Refresh main view
            this.dispose();
            new DonorProfilePage(currentDonor).setVisible(true);
            JOptionPane.showMessageDialog(null, "Profile updated successfully!");
        });

        dialog.setVisible(true);
    }

    private void refreshAllRequests() {
        refreshIncomingRequests();
        refreshSentRequests();
    }

    private void refreshIncomingRequests() {
        incomingContainer.removeAll();
        boolean hasRequests = false;

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getDonorEmail().equals(currentDonor.getEmail())) {
                incomingContainer.add(createIncomingRow(req));
                incomingContainer.add(Box.createVerticalStrut(10));
                hasRequests = true;
            }
        }

        if (!hasRequests) {
            JLabel emptyLabel = new JLabel("No incoming requests.", SwingConstants.CENTER);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            incomingContainer.add(emptyLabel);
        }

        incomingContainer.revalidate();
        incomingContainer.repaint();
    }

    private void refreshSentRequests() {
        sentContainer.removeAll();
        boolean hasRequests = false;

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getRequesterEmail().equals(currentDonor.getEmail())) {
                sentContainer.add(createSentRow(req));
                sentContainer.add(Box.createVerticalStrut(10));
                hasRequests = true;
            }
        }

        if (!hasRequests) {
            JLabel emptyLabel = new JLabel("No sent requests yet.", SwingConstants.CENTER);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sentContainer.add(emptyLabel);
        }

        sentContainer.revalidate();
        sentContainer.repaint();
    }

    private JPanel createIncomingRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(500, 110));
        row.setPreferredSize(new Dimension(450, 110));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Info
        String statusIcon = req.getStatus().equals("Accepted") ? "V" : req.getStatus().equals("Declined") ? "X" : "?";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "black";
        String info = "<html><b>From: " + req.getRequesterName() + "</b><br>" +
                      "Status: " + statusIcon + " <font color='" + statusColor + "'>" + req.getStatus() + "</font></html>";
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
                database.DataStore.updateRequestStatus(req, "Accepted");
                refreshAllRequests();
                JOptionPane.showMessageDialog(this, "Success: You have accepted the request.");
            });
            
            declineBtn.addActionListener(e -> {
                database.DataStore.updateRequestStatus(req, "Declined");
                refreshAllRequests();
            });
            
            btnPanel.add(detailsBtn);
            btnPanel.add(acceptBtn);
            btnPanel.add(declineBtn);
            row.add(btnPanel, BorderLayout.EAST);
        }

        return row;
    }

    private JPanel createSentRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(500, 70));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String statusIcon = req.getStatus().equals("Accepted") ? "V" : req.getStatus().equals("Declined") ? "X" : "?";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "blue";
        String info = "<html>" + statusIcon + " Request to: " + req.getDonorEmail() + "<br>Status: <b><font color='" + statusColor + "'>" + req.getStatus() + "</font></b></html>";
        
        row.add(new JLabel(info), BorderLayout.CENTER);
        return row;
    }

    private void showRequestDetails(BloodRequest req) {
        String msg = "Hospital: " + req.getHospitalName() + "\n" +
                     "Patient: " + req.getPatientName() + "\n" +
                     "Location: " + req.getLocation() + "\n" +
                     "Condition: " + req.getMedicalCondition();
        JOptionPane.showMessageDialog(this, msg, "Request Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.PLAIN, 18));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
}
