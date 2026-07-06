package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
        if (donor == null) {
            JOptionPane.showMessageDialog(null, "Access Denied: Please login first.", "Unauthorized", JOptionPane.ERROR_MESSAGE);
            new LoginPage().setVisible(true);
            this.dispose();
            return;
        }
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

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                if (donor.hasUpdate()) {
                    UIManager.put("OptionPane.messageFont", labelFont);
                    JOptionPane.showMessageDialog(DonorProfilePage.this, "(!) You have an update in your requests!", "System Notification", JOptionPane.INFORMATION_MESSAGE);
                    donor.setHasUpdate(false);
                    DataStore.updateUser(donor); // Clear notification flag in DB
                }
            }
        });

        // Main Content
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 20));
        mainContent.setOpaque(false);

        // Left Side: Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(null, "My Account Details", 0, 0, labelFont));

        detailsPanel.add(createDetailLabel("Name: " + DataStore.escapeHtml(donor.getName())));
        detailsPanel.add(createDetailLabel("Email: " + DataStore.escapeHtml(donor.getEmail())));
        detailsPanel.add(createDetailLabel("<html><font color='red'>&hearts;</font> Blood Group: " + DataStore.escapeHtml(DataStore.safe(donor.getBloodGroup())) + "</html>"));
        detailsPanel.add(createDetailLabel("<html>Location: " + DataStore.escapeHtml(DataStore.safe(donor.getLocation())) + ", " + DataStore.escapeHtml(DataStore.safe(donor.getState())) + "</html>"));
        
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
            String oldPass = new String(oldPassF.getPassword());
            String newPass = new String(newPassF.getPassword());
            String confirmPass = new String(confirmPassF.getPassword());

            UIManager.put("OptionPane.messageFont", labelFont);
            if (!DataStore.checkPassword(oldPass, currentDonor.getPassword())) {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "New password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                currentDonor.setPassword(DataStore.hashPassword(newPass));
                DataStore.updateUser(currentDonor);
                JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile Details", true);
        dialog.setSize(600, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(currentDonor.getName());
        JTextField stateF = new JTextField(currentDonor.getState());
        JTextField locF = new JTextField(currentDonor.getLocation());
        
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        String[] commonConditions = {"None", "Anemia", "Asthma", "Diabetes", "Hypertension", "Hepatitis", "Heart Disease", "Severe Allergy"};
        
        JComboBox<String> groupF = new JComboBox<>(bloodGroups);
        groupF.setEditable(true);
        groupF.setSelectedItem(currentDonor.getBloodGroup());
        
        // Medical Condition Component Redesign
        JPanel medicalPanel = new JPanel(new BorderLayout(5, 0));
        medicalPanel.setOpaque(false);
        
        JTextArea medicalA = new JTextArea(3, 20);
        medicalA.setText(currentDonor.getMedicalCondition());
        medicalA.setFont(detailFont);
        medicalA.setLineWrap(true);
        medicalA.setWrapStyleWord(true);
        JScrollPane medicalScroll = new JScrollPane(medicalA);
        medicalScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        medicalScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Hide scrollbar arrows
        medicalPanel.add(medicalScroll, BorderLayout.CENTER);

        JButton mlBtn = new JButton("▼");
        mlBtn.setPreferredSize(new Dimension(30, 30));
        mlBtn.setFont(new Font("Arial", Font.BOLD, 12));
        medicalPanel.add(mlBtn, BorderLayout.EAST);

        JPopupMenu conditionsMenu = new JPopupMenu();
        for (String condition : commonConditions) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(condition);
            item.setFont(new Font("Dialog", Font.PLAIN, 16));
            item.addActionListener(ev -> {
                String currentText = medicalA.getText().trim();
                List<String> items = new ArrayList<>(Arrays.asList(currentText.split(",\\s*")));
                items.removeIf(String::isEmpty);
                
                if (item.isSelected()) {
                    if (!items.contains(condition)) items.add(condition);
                    item.setForeground(new Color(0, 128, 0));
                } else {
                    items.remove(condition);
                    item.setForeground(Color.BLACK);
                }
                medicalA.setText(items.stream().collect(Collectors.joining(", ")));
            });
            conditionsMenu.add(item);
        }
        mlBtn.addActionListener(ev -> {
            String currentText = medicalA.getText().trim().toLowerCase();
            for (int i = 0; i < conditionsMenu.getComponentCount(); i++) {
                if (conditionsMenu.getComponent(i) instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem) conditionsMenu.getComponent(i);
                    boolean isSelected = Arrays.stream(currentText.split(",\\s*"))
                                              .anyMatch(s -> s.equalsIgnoreCase(item.getText()));
                    item.setSelected(isSelected);
                    item.setForeground(isSelected ? new Color(0, 128, 0) : Color.BLACK);
                }
            }
            conditionsMenu.show(mlBtn, -200, mlBtn.getHeight());
        });

        JCheckBox availCheck = new JCheckBox("Available for Donation", currentDonor.isAvailable());

        nameF.setFont(detailFont);
        stateF.setFont(detailFont);
        locF.setFont(detailFont);
        groupF.setFont(detailFont);
        availCheck.setFont(labelFont);

        int r = 0;
        JLabel l1 = new JLabel("Full Name:"); l1.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3; dialog.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; dialog.add(nameF, gbc); r++;

        JLabel l2 = new JLabel("State:"); l2.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3; dialog.add(l2, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; dialog.add(stateF, gbc); r++;

        JLabel l3 = new JLabel("City/Location:"); l3.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3; dialog.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; dialog.add(locF, gbc); r++;

        JLabel l4 = new JLabel("Blood Group:"); l4.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3; dialog.add(l4, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; dialog.add(groupF, gbc); r++;

        JLabel l5 = new JLabel("Medical Condition:"); l5.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3; dialog.add(l5, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; dialog.add(medicalPanel, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2; gbc.weightx = 1.0;
        dialog.add(availCheck, gbc); r++;

        RoundedButton saveBtn = new RoundedButton("Update My Info");
        saveBtn.setPreferredSize(new Dimension(200, 50));
        gbc.gridy = r;
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty()) {
                UIManager.put("OptionPane.messageFont", labelFont);
                JOptionPane.showMessageDialog(dialog, "Name cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentDonor.setName(nameF.getText().trim());
            currentDonor.setState(stateF.getText().trim());
            currentDonor.setLocation(locF.getText().trim());
            
            String selectedGroup = (groupF.getEditor().getItem() != null) ? groupF.getEditor().getItem().toString().trim() : "";
            currentDonor.setBloodGroup(selectedGroup);
            
            currentDonor.setMedicalCondition(medicalA.getText().trim());
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
        
        List<BloodRequest> pending = new ArrayList<>();
        List<BloodRequest> ongoing = new ArrayList<>();
        List<BloodRequest> completed = new ArrayList<>();

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getDonorEmail().equals(currentDonor.getEmail())) {
                if (req.getStatus().equalsIgnoreCase("Pending")) pending.add(req);
                else if (req.getStatus().equalsIgnoreCase("Accepted")) ongoing.add(req);
                else completed.add(req);
            }
        }

        if (pending.isEmpty() && ongoing.isEmpty() && completed.isEmpty()) {
            JLabel emptyLabel = new JLabel("No incoming requests.", SwingConstants.CENTER);
            emptyLabel.setFont(labelFont);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            incomingContainer.add(emptyLabel);
        } else {
            if (!pending.isEmpty()) {
                incomingContainer.add(createHeaderLabel("New Requests (" + pending.size() + ")", new Color(0, 102, 204)));
                for (BloodRequest req : pending) {
                    incomingContainer.add(createIncomingRow(req));
                    incomingContainer.add(Box.createVerticalStrut(10));
                }
            }
            if (!ongoing.isEmpty()) {
                incomingContainer.add(Box.createVerticalStrut(15));
                incomingContainer.add(createHeaderLabel("In-Progress (" + ongoing.size() + ")", new Color(255, 153, 0)));
                for (BloodRequest req : ongoing) {
                    incomingContainer.add(createIncomingRow(req));
                    incomingContainer.add(Box.createVerticalStrut(10));
                }
            }
            if (!completed.isEmpty()) {
                incomingContainer.add(Box.createVerticalStrut(15));
                incomingContainer.add(createHeaderLabel("Donation History (" + completed.size() + ")", new Color(0, 153, 51)));
                for (BloodRequest req : completed) {
                    incomingContainer.add(createIncomingRow(req));
                    incomingContainer.add(Box.createVerticalStrut(10));
                }
            }
        }

        incomingContainer.revalidate();
        incomingContainer.repaint();
    }

    private void refreshSentRequests() {
        sentContainer.removeAll();
        
        List<BloodRequest> active = new ArrayList<>();
        List<BloodRequest> finalized = new ArrayList<>();

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getRequesterEmail().equals(currentDonor.getEmail())) {
                if (req.getStatus().equalsIgnoreCase("Pending")) active.add(req);
                else finalized.add(req);
            }
        }

        if (active.isEmpty() && finalized.isEmpty()) {
            JLabel emptyLabel = new JLabel("No sent requests yet.", SwingConstants.CENTER);
            emptyLabel.setFont(labelFont);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sentContainer.add(emptyLabel);
        } else {
            if (!active.isEmpty()) {
                sentContainer.add(createHeaderLabel("Active Requests (" + active.size() + ")", new Color(0, 102, 204)));
                for (BloodRequest req : active) {
                    sentContainer.add(createSentRow(req));
                    sentContainer.add(Box.createVerticalStrut(10));
                }
            }
            if (!finalized.isEmpty()) {
                if (!active.isEmpty()) sentContainer.add(Box.createVerticalStrut(15));
                sentContainer.add(createHeaderLabel("Finalized History (" + finalized.size() + ")", new Color(0, 153, 51)));
                for (BloodRequest req : finalized) {
                    sentContainer.add(createSentRow(req));
                    sentContainer.add(Box.createVerticalStrut(10));
                }
            }
        }

        sentContainer.revalidate();
        sentContainer.repaint();
    }

    private JLabel createHeaderLabel(String text, Color color) {
        JLabel label = new JLabel(" " + text);
        label.setFont(new Font("Dialog", Font.BOLD, 16));
        label.setForeground(color);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, color));
        label.setMaximumSize(new Dimension(600, 35));
        return label;
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

        boolean isPending = req.getStatus().equalsIgnoreCase("Pending");
        boolean isAccepted = req.getStatus().equalsIgnoreCase("Accepted");
        
        String statusIcon = req.getStatus().equals("Accepted") ? "✓" : req.getStatus().equals("Declined") ? "✕" : req.getStatus().equals("Completed") ? "★" : "⌛";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : req.getStatus().equals("Completed") ? "#D4AF37" : "black";
        
        String urgencyTag = "";
        if (req.getUrgency().equalsIgnoreCase("Emergency")) urgencyTag = " <font color='red'>[EMERGENCY]</font>";
        else if (req.getUrgency().equalsIgnoreCase("Urgent")) urgencyTag = " <font color='orange'>[URGENT]</font>";

        String info = "<html><font size='5'><b>From: " + req.getRequesterName() + "</b>" + urgencyTag + "<br>" +
                      "Status: " + statusIcon + " <font color='" + statusColor + "'>" + req.getStatus() + "</font></font></html>";
        JLabel infoLabel = new JLabel(info);
        row.add(infoLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        
        if (isPending) {
            btnPanel.setLayout(new GridLayout(3, 1, 5, 5));
            RoundedButton detailsBtn = new RoundedButton("Details", new Color(50, 50, 50), new Color(80, 80, 80));
            RoundedButton acceptBtn = new RoundedButton("Accept", new Color(40, 167, 69), new Color(33, 136, 56));
            RoundedButton declineBtn = new RoundedButton("Decline", new Color(220, 53, 69), new Color(200, 35, 51));
            
            Dimension sBtnSize = new Dimension(110, 35);
            detailsBtn.setPreferredSize(sBtnSize); acceptBtn.setPreferredSize(sBtnSize); declineBtn.setPreferredSize(sBtnSize);

            detailsBtn.addActionListener(e -> showRequestDetails(req));
            acceptBtn.addActionListener(e -> {
                DataStore.updateRequestStatus(req, "Accepted");
                refreshAllRequests();
                JOptionPane.showMessageDialog(this, "Request Accepted!");
            });
            declineBtn.addActionListener(e -> {
                DataStore.updateRequestStatus(req, "Declined");
                refreshAllRequests();
            });
            
            btnPanel.add(detailsBtn); btnPanel.add(acceptBtn); btnPanel.add(declineBtn);
        } else if (isAccepted) {
            btnPanel.setLayout(new GridLayout(2, 1, 5, 5));
            RoundedButton detailsBtn = new RoundedButton("Details", new Color(50, 50, 50), new Color(80, 80, 80));
            RoundedButton completeBtn = new RoundedButton("Done", new Color(0, 153, 51), new Color(0, 102, 34));
            
            Dimension sBtnSize = new Dimension(110, 40);
            detailsBtn.setPreferredSize(sBtnSize); completeBtn.setPreferredSize(sBtnSize);

            detailsBtn.addActionListener(e -> showRequestDetails(req));
            completeBtn.addActionListener(e -> {
                DataStore.updateRequestStatus(req, "Completed");
                refreshAllRequests();
                JOptionPane.showMessageDialog(this, "Life Saved! Thank you for your contribution.");
            });
            
            btnPanel.add(detailsBtn); btnPanel.add(completeBtn);
        } else {
            btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            RoundedButton deleteBtn = new RoundedButton("Clear", new Color(70, 70, 70), new Color(100, 100, 100));
            deleteBtn.setPreferredSize(new Dimension(100, 40));
            deleteBtn.addActionListener(e -> {
                DataStore.deleteBloodRequest(req);
                refreshIncomingRequests();
            });
            btnPanel.add(deleteBtn);
        }
        
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }

    private JPanel createSentRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(550, 100));
        row.setPreferredSize(new Dimension(500, 100));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        boolean isPending = req.getStatus().equalsIgnoreCase("Pending");
        String statusIcon = req.getStatus().equals("Accepted") ? "✓" : req.getStatus().equals("Declined") ? "✕" : "⏳";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "blue";
        
        String urgencyTag = "";
        if (req.getUrgency().equalsIgnoreCase("Emergency")) urgencyTag = " <font color='red'>[EMERGENCY]</font>";
        else if (req.getUrgency().equalsIgnoreCase("Urgent")) urgencyTag = " <font color='orange'>[URGENT]</font>";

        String info = "<html><font size='5'>" + statusIcon + " Request to: " + req.getDonorEmail() + urgencyTag + "<br>Status: <b><font color='" + statusColor + "'>" + req.getStatus() + "</font></b></font></html>";
        row.add(new JLabel(info), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        if (isPending) {
            RoundedButton cancelBtn = new RoundedButton("Cancel", new Color(200, 0, 0), new Color(255, 50, 50));
            cancelBtn.setPreferredSize(new Dimension(100, 40));
            cancelBtn.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Cancel this request?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DataStore.deleteBloodRequest(req);
                    refreshSentRequests();
                }
            });
            btnPanel.add(cancelBtn);
        } else {
            RoundedButton deleteBtn = new RoundedButton("Clear", new Color(70, 70, 70), new Color(100, 100, 100));
            deleteBtn.setPreferredSize(new Dimension(100, 40));
            deleteBtn.addActionListener(e -> {
                DataStore.deleteBloodRequest(req);
                refreshSentRequests();
            });
            btnPanel.add(deleteBtn);
        }

        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }

    private void showRequestDetails(BloodRequest req) {
        String msg = "Hospital: " + DataStore.safe(req.getHospitalName()) + "\n" +
                     "Patient: " + DataStore.safe(req.getPatientName()) + "\n" +
                     "Location: " + DataStore.safe(req.getLocation()) + "\n" +
                     "Condition: " + DataStore.safe(req.getMedicalCondition());
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
