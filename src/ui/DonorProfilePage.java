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
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font detailFont = new Font("Dialog", Font.PLAIN, 20);

    public DonorProfilePage(Donor donor) {
        this.currentDonor = donor;
        setTitle("Donor Profile - " + donor.getName());
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1100, 650);

        // Header
        JLabel titleLabel = new JLabel("Donor Dashboard", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(180, 0, 0));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        card.add(titleLabel, BorderLayout.NORTH);

        // Notification Check
        if (donor.hasUpdate()) {
            UIManager.put("OptionPane.messageFont", labelFont);
            JOptionPane.showMessageDialog(this, "(!) You have update in your requests!", "System Notification", JOptionPane.INFORMATION_MESSAGE);
            donor.setHasUpdate(false);
            DataStore.updateUser(donor); // Clear notification flag in DB
        }

        // Main Content
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 20));
        mainContent.setOpaque(false);

        // Left Side: Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(null, "My Account Details", 0, 0, labelFont));

        detailsPanel.add(createDetailLabel("Name: " + donor.getName()));
        detailsPanel.add(createDetailLabel("Email: " + donor.getEmail()));
        detailsPanel.add(createDetailLabel("<html><font color='red'>&hearts;</font> Blood Group: " + donor.getBloodGroup() + "</html>"));
        detailsPanel.add(createDetailLabel("Location: " + donor.getLocation() + ", " + donor.getState()));
        
        statusLabel = createDetailLabel("Status: " + (donor.isAvailable() ? "Available" : "Busy"));
        detailsPanel.add(statusLabel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Standard size for all navigation buttons
        Dimension btnSize = new Dimension(280, 55);
        Font btnFont = new Font("Dialog", Font.BOLD, 18);

        RoundedButton editProfileBtn = new RoundedButton("Edit My Profile", new Color(180, 0, 0), new Color(220, 20, 20));
        editProfileBtn.setIcon(new VectorIcon(VectorIcon.Type.EDIT, 22, Color.WHITE));
        editProfileBtn.setPreferredSize(btnSize);
        editProfileBtn.setMaximumSize(btnSize);
        editProfileBtn.setFont(btnFont);
        editProfileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(editProfileBtn);
        detailsPanel.add(Box.createVerticalStrut(15));

        RoundedButton changePassBtn = new RoundedButton("Change Password", new Color(180, 0, 0), new Color(220, 20, 20));
        changePassBtn.setIcon(new VectorIcon(VectorIcon.Type.LOCK, 22, Color.WHITE));
        changePassBtn.setPreferredSize(btnSize);
        changePassBtn.setMaximumSize(btnSize);
        changePassBtn.setFont(btnFont);
        changePassBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(changePassBtn);
        detailsPanel.add(Box.createVerticalStrut(15));

        RoundedButton searchBtn = new RoundedButton("Search for Donors", new Color(180, 0, 0), new Color(220, 20, 20));
        searchBtn.setIcon(new VectorIcon(VectorIcon.Type.SEARCH, 22, Color.WHITE));
        searchBtn.setPreferredSize(btnSize);
        searchBtn.setMaximumSize(btnSize);
        searchBtn.setFont(btnFont);
        searchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(searchBtn);

        // Right Side: Tabbed Requests
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Dialog", Font.BOLD, 18));

        // Incoming Tab
        incomingContainer = new JPanel();
        incomingContainer.setLayout(new BoxLayout(incomingContainer, BoxLayout.Y_AXIS));
        incomingContainer.setBackground(Color.WHITE);
        JScrollPane inScroll = new JScrollPane(incomingContainer);
        optimizeScroll(inScroll);
        tabs.addTab("Incoming Requests", inScroll);

        // Outgoing Tab
        sentContainer = new JPanel();
        sentContainer.setLayout(new BoxLayout(sentContainer, BoxLayout.Y_AXIS));
        sentContainer.setBackground(Color.WHITE);
        JScrollPane outScroll = new JScrollPane(sentContainer);
        optimizeScroll(outScroll);
        tabs.addTab("Outgoing Requests", outScroll);

        mainContent.add(detailsPanel);
        mainContent.add(tabs);
        card.add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton logoutBtn = new RoundedButton("Logout Account", new Color(50, 50, 50), new Color(80, 80, 80));
        logoutBtn.setPreferredSize(new Dimension(200, 45));
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

    private void optimizeScroll(JScrollPane sp) {
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(500, 450); // Optimized compact width
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPasswordField oldPassF = new JPasswordField(20);
        JPasswordField newPassF = new JPasswordField(20);
        JPasswordField confirmPassF = new JPasswordField(20);
        
        oldPassF.setFont(detailFont);
        newPassF.setFont(detailFont);
        confirmPassF.setFont(detailFont);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lbl1 = new JLabel("Current:"); lbl1.setFont(labelFont);
        dialog.add(lbl1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(oldPassF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lbl2 = new JLabel("New:"); lbl2.setFont(labelFont);
        dialog.add(lbl2, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(newPassF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lbl3 = new JLabel("Confirm:"); lbl3.setFont(labelFont);
        dialog.add(lbl3, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(confirmPassF, gbc); r++;

        RoundedButton updateBtn = new RoundedButton("Update Password");
        updateBtn.setPreferredSize(new Dimension(200, 50));
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2; gbc.weightx = 1.0;
        dialog.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            String oldPass = new String(oldPassF.getPassword()).trim();
            String newPass = new String(newPassF.getPassword()).trim();
            String confirmPass = new String(confirmPassF.getPassword()).trim();

            UIManager.put("OptionPane.messageFont", labelFont);
            if (!oldPass.equals(currentDonor.getPassword())) {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "New password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                currentDonor.setPassword(newPass);
                DataStore.updateUser(currentDonor);
                JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile Details", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(currentDonor.getName());
        JTextField stateF = new JTextField(currentDonor.getState());
        JTextField locF = new JTextField(currentDonor.getLocation());
        JTextField groupF = new JTextField(currentDonor.getBloodGroup());
        JTextField medicalF = new JTextField(currentDonor.getMedicalCondition());
        JCheckBox availCheck = new JCheckBox("Available for Donation", currentDonor.isAvailable());

        nameF.setFont(detailFont);
        stateF.setFont(detailFont);
        locF.setFont(detailFont);
        groupF.setFont(detailFont);
        medicalF.setFont(detailFont);
        availCheck.setFont(labelFont);

        int r = 0;
        JLabel l1 = new JLabel("Full Name:"); l1.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; dialog.add(l1, gbc);
        gbc.gridx = 1; dialog.add(nameF, gbc); r++;

        JLabel l2 = new JLabel("State:"); l2.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; dialog.add(l2, gbc);
        gbc.gridx = 1; dialog.add(stateF, gbc); r++;

        JLabel l3 = new JLabel("City/Location:"); l3.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; dialog.add(l3, gbc);
        gbc.gridx = 1; dialog.add(locF, gbc); r++;

        JLabel l4 = new JLabel("Blood Group:"); l4.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; dialog.add(l4, gbc);
        gbc.gridx = 1; dialog.add(groupF, gbc); r++;

        JLabel l5 = new JLabel("Medical Condition:"); l5.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; dialog.add(l5, gbc);
        gbc.gridx = 1; dialog.add(medicalF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        dialog.add(availCheck, gbc); r++;

        RoundedButton saveBtn = new RoundedButton("Update My Info");
        saveBtn.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = r;
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            currentDonor.setName(nameF.getText().trim());
            currentDonor.setState(stateF.getText().trim());
            currentDonor.setLocation(locF.getText().trim());
            currentDonor.setBloodGroup(groupF.getText().trim());
            currentDonor.setMedicalCondition(medicalF.getText().trim());
            currentDonor.setAvailable(availCheck.isSelected());
            
            DataStore.updateUser(currentDonor);
            
            // Refresh main view
            this.dispose();
            new DonorProfilePage(currentDonor).setVisible(true);
            UIManager.put("OptionPane.messageFont", labelFont);
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
                incomingContainer.add(Box.createVerticalStrut(15));
                hasRequests = true;
            }
        }

        if (!hasRequests) {
            JLabel emptyLabel = new JLabel("No incoming requests.", SwingConstants.CENTER);
            emptyLabel.setFont(labelFont);
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
                sentContainer.add(Box.createVerticalStrut(15));
                hasRequests = true;
            }
        }

        if (!hasRequests) {
            JLabel emptyLabel = new JLabel("No sent requests yet.", SwingConstants.CENTER);
            emptyLabel.setFont(labelFont);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sentContainer.add(emptyLabel);
        }

        sentContainer.revalidate();
        sentContainer.repaint();
    }

    private JPanel createIncomingRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(550, 140));
        row.setPreferredSize(new Dimension(500, 140));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Info
        String statusIcon = req.getStatus().equals("Accepted") ? "V" : req.getStatus().equals("Declined") ? "X" : "?";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "black";
        String info = "<html><font size='5'><b>From: " + req.getRequesterName() + "</b><br>" +
                      "Status: " + statusIcon + " <font color='" + statusColor + "'>" + req.getStatus() + "</font></font></html>";
        JLabel infoLabel = new JLabel(info);
        row.add(infoLabel, BorderLayout.CENTER);

        // Buttons Panel
        if (req.getStatus().equals("Pending")) {
            JPanel btnPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            btnPanel.setOpaque(false);
            
            RoundedButton detailsBtn = new RoundedButton("Details", new Color(50, 50, 50), new Color(80, 80, 80));
            RoundedButton acceptBtn = new RoundedButton("Accept", new Color(40, 167, 69), new Color(33, 136, 56));
            RoundedButton declineBtn = new RoundedButton("Decline", new Color(220, 53, 69), new Color(200, 35, 51));
            
            Dimension sBtnSize = new Dimension(110, 35);
            detailsBtn.setPreferredSize(sBtnSize);
            acceptBtn.setPreferredSize(sBtnSize);
            declineBtn.setPreferredSize(sBtnSize);

            detailsBtn.addActionListener(e -> showRequestDetails(req));

            acceptBtn.addActionListener(e -> {
                database.DataStore.updateRequestStatus(req, "Accepted");
                refreshAllRequests();
                UIManager.put("OptionPane.messageFont", labelFont);
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
        row.setMaximumSize(new Dimension(550, 90));
        row.setPreferredSize(new Dimension(500, 90));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String statusIcon = req.getStatus().equals("Accepted") ? "V" : req.getStatus().equals("Declined") ? "X" : "?";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "blue";
        String info = "<html><font size='5'>" + statusIcon + " Request to: " + req.getDonorEmail() + "<br>Status: <b><font color='" + statusColor + "'>" + req.getStatus() + "</font></b></font></html>";
        
        row.add(new JLabel(info), BorderLayout.CENTER);
        return row;
    }

    private void showRequestDetails(BloodRequest req) {
        String msg = "Hospital: " + req.getHospitalName() + "\n" +
                     "Patient: " + req.getPatientName() + "\n" +
                     "Location: " + req.getLocation() + "\n" +
                     "Condition: " + req.getMedicalCondition();
        UIManager.put("OptionPane.messageFont", labelFont);
        JOptionPane.showMessageDialog(this, msg, "Request Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.PLAIN, 20));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
}
