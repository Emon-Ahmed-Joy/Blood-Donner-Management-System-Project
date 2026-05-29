package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.BloodRequest;
import model.Donor;
import model.User;

/**
 * User Homepage for tracking requests.
 * @author Emon Ahmed Joy
 */
public class UserHomePage extends JFrame {
    private User currentUser;
    private JPanel requestsContainer;
    private JLabel detailsLabel;
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font detailFont = new Font("Dialog", Font.PLAIN, 20);

    public UserHomePage(User user) {
        this.currentUser = user;
        setTitle("Blood Donor Management - Home");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1100, 650);

        // Header
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(180, 0, 0));
        welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 36));
        card.add(welcomeLabel, BorderLayout.NORTH);

        // Notification Check
        if (user.hasUpdate()) {
            UIManager.put("OptionPane.messageFont", labelFont);
            JOptionPane.showMessageDialog(this, "(!) One of your blood requests has been updated!", "Request Update", JOptionPane.INFORMATION_MESSAGE);
            user.setHasUpdate(false);
            DataStore.updateUser(user); // Clear notification flag in DB
        }

        // Main Content Area
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setOpaque(false);

        // Left Side: Navigation Buttons & Profile
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        detailsLabel = new JLabel("<html><b>Location:</b> " + user.getLocation() + ", " + user.getState() + "</html>");
        detailsLabel.setFont(detailFont);
        gbc.gridx = 0; gbc.gridy = 0; navPanel.add(detailsLabel, gbc);

        // Standard size for all navigation buttons
        Dimension btnSize = new Dimension(320, 55);
        Font btnFont = new Font("Dialog", Font.BOLD, 18);

        RoundedButton editProfileBtn = new RoundedButton("Edit My Profile", new Color(180, 0, 0), new Color(220, 20, 20));
        editProfileBtn.setIcon(new VectorIcon(VectorIcon.Type.EDIT, 22, Color.WHITE));
        editProfileBtn.setPreferredSize(btnSize);
        editProfileBtn.setFont(btnFont);
        gbc.gridy = 1; navPanel.add(editProfileBtn, gbc);

        RoundedButton changePassBtn = new RoundedButton("Change Password", new Color(180, 0, 0), new Color(220, 20, 20));
        changePassBtn.setIcon(new VectorIcon(VectorIcon.Type.LOCK, 22, Color.WHITE));
        changePassBtn.setPreferredSize(btnSize);
        changePassBtn.setFont(btnFont);
        gbc.gridy = 2; navPanel.add(changePassBtn, gbc);

        JButton registerBtn = new RoundedButton("Register as a Donor", new Color(180, 0, 0), new Color(220, 20, 20));
        registerBtn.setIcon(new VectorIcon(VectorIcon.Type.HEART, 22, Color.WHITE));
        registerBtn.setPreferredSize(btnSize);
        registerBtn.setFont(btnFont);
        gbc.gridy = 3; navPanel.add(registerBtn, gbc);
        
        JButton searchBtn = new RoundedButton("Search for Blood", new Color(180, 0, 0), new Color(220, 20, 20));
        searchBtn.setIcon(new VectorIcon(VectorIcon.Type.SEARCH, 22, Color.WHITE));
        searchBtn.setPreferredSize(btnSize);
        searchBtn.setFont(btnFont);
        gbc.gridy = 4; navPanel.add(searchBtn, gbc);

        // Right Side: My Requests Tracking
        JPanel trackingPanel = new JPanel(new BorderLayout());
        trackingPanel.setOpaque(false);
        JLabel trackingHeader = new JLabel("My Sent Requests Status", SwingConstants.CENTER);
        trackingHeader.setFont(new Font("Dialog", Font.BOLD, 22));
        trackingPanel.add(trackingHeader, BorderLayout.NORTH);

        requestsContainer = new JPanel();
        requestsContainer.setLayout(new BoxLayout(requestsContainer, BoxLayout.Y_AXIS));
        requestsContainer.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(requestsContainer);
        optimizeScroll(scrollPane);
        trackingPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(navPanel);
        mainContent.add(trackingPanel);
        card.add(mainContent, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton logoutBtn = new RoundedButton("Logout Account", new Color(50, 50, 50), new Color(80, 80, 80));
        logoutBtn.setPreferredSize(new Dimension(220, 45));
        bottomPanel.add(logoutBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        editProfileBtn.addActionListener(e -> showEditProfileDialog());
        changePassBtn.addActionListener(e -> showChangePasswordDialog());

        registerBtn.addActionListener(e -> showDonorUpgradeDialog());

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
            if (!oldPass.equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "New password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                currentUser.setPassword(newPass);
                DataStore.updateUser(currentUser);
                DataStore.updateUserPassword(currentUser); // ✅ DB te save
                JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile Details", true);
        dialog.setSize(500, 550); // Optimized compact width
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(currentUser.getName(), 20);
        JTextField stateF = new JTextField(currentUser.getState(), 20);
        JTextField locF = new JTextField(currentUser.getLocation(), 20);
        
        nameF.setFont(detailFont);
        stateF.setFont(detailFont);
        locF.setFont(detailFont);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lbl1 = new JLabel("Name:"); lbl1.setFont(labelFont);
        dialog.add(lbl1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(nameF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel lbl2 = new JLabel("State:"); lbl2.setFont(labelFont);
        dialog.add(lbl2, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(stateF, gbc); r++;

        JLabel lbl3 = new JLabel("City:"); lbl3.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        dialog.add(lbl3, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(locF, gbc); r++;

        RoundedButton saveBtn = new RoundedButton("Update My Info");
        saveBtn.setPreferredSize(new Dimension(200, 50));
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2; gbc.weightx = 1.0;
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            currentUser.setName(nameF.getText().trim());
            currentUser.setState(stateF.getText().trim());
            currentUser.setLocation(locF.getText().trim());
            DataStore.updateUser(currentUser);
            currentUser.setName(nameF.getText());
            currentUser.setState(stateF.getText());
            currentUser.setLocation(locF.getText());
            DataStore.updateUserProfile(currentUser); // DB te save
            detailsLabel.setText("<html><b>Location:</b> " + currentUser.getLocation() + ", " + currentUser.getState() + "</html>");
            UIManager.put("OptionPane.messageFont", labelFont);
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void showDonorUpgradeDialog() {
        JDialog dialog = new JDialog(this, "Become a Blood Donor", true);
        dialog.setSize(600, 700); // Slightly larger for the new component
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        String[] commonConditions = {"None", "Anemia", "Asthma", "Diabetes", "Hypertension", "Hepatitis", "Heart Disease", "Severe Allergy"};

        JComboBox<String> groupF = new JComboBox<>(bloodGroups);
        groupF.setEditable(true);
        groupF.setFont(detailFont);

        JTextField stateF = new JTextField(currentUser.getState(), 20); stateF.setFont(detailFont);
        JTextField locF = new JTextField(currentUser.getLocation(), 20); locF.setFont(detailFont);
        
        // Medical Condition Component
        JPanel medicalPanel = new JPanel(new BorderLayout(5, 0));
        medicalPanel.setOpaque(false);
        
        JTextArea medicalA = new JTextArea(3, 20);
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
                java.util.List<String> items = new java.util.ArrayList<>(java.util.Arrays.asList(currentText.split(",\\s*")));
                items.removeIf(String::isEmpty);
                
                if (item.isSelected()) {
                    if (!items.contains(condition)) items.add(condition);
                    item.setForeground(new Color(0, 128, 0));
                } else {
                    items.remove(condition);
                    item.setForeground(Color.BLACK);
                }
                medicalA.setText(items.stream().collect(java.util.stream.Collectors.joining(", ")));
            });
            conditionsMenu.add(item);
        }
        mlBtn.addActionListener(ev -> {
            String currentText = medicalA.getText().trim().toLowerCase();
            for (int i = 0; i < conditionsMenu.getComponentCount(); i++) {
                if (conditionsMenu.getComponent(i) instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem) conditionsMenu.getComponent(i);
                    boolean isSelected = java.util.Arrays.stream(currentText.split(",\\s*"))
                                              .anyMatch(s -> s.equalsIgnoreCase(item.getText()));
                    item.setSelected(isSelected);
                    item.setForeground(isSelected ? new Color(0, 128, 0) : Color.BLACK);
                }
            }
            conditionsMenu.show(mlBtn, -200, mlBtn.getHeight());
        });

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JLabel header = new JLabel("<html><center><font color='#B40000' size='6'><b>Complete Your Donor Profile</b></font><br><br>Please provide accurate details to help save lives.<br><hr></center></html>", SwingConstants.CENTER);
        dialog.add(header, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel l1 = new JLabel("Blood Group:"); l1.setFont(labelFont);
        dialog.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(groupF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel l2 = new JLabel("State:"); l2.setFont(labelFont);
        dialog.add(l2, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(stateF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel l3 = new JLabel("Location:"); l3.setFont(labelFont);
        dialog.add(l3, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(locF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.3;
        JLabel l4 = new JLabel("Medical Conditions:"); l4.setFont(labelFont);
        dialog.add(l4, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        dialog.add(medicalPanel, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2; gbc.weightx = 1.0;
        dialog.add(Box.createVerticalStrut(10), gbc); r++;

        RoundedButton submitBtn = new RoundedButton("Submit & Become Donor", new Color(180, 0, 0), new Color(220, 20, 20));
        submitBtn.setPreferredSize(new Dimension(250, 55));
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        dialog.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            String selectedGroup = (groupF.getEditor().getItem() != null) ? groupF.getEditor().getItem().toString().trim() : "";
            if (selectedGroup.isEmpty()) {
                UIManager.put("OptionPane.messageFont", labelFont);
                JOptionPane.showMessageDialog(dialog, "Please enter your blood group.");
                return;
            }

            Donor newDonor = new Donor(currentUser.getName().trim(), currentUser.getEmail().trim(), currentUser.getPassword().trim(),
                                     selectedGroup, stateF.getText().trim(), locF.getText().trim(), medicalA.getText().trim());
            
            // Transfer logic: Update database and local lists
            DataStore.updateUser(newDonor); // This will update the 'is_donor' and other fields in DB
            DataStore.loadDataFromDatabase(); // Refresh local cache to reflect transfer
            
            UIManager.put("OptionPane.messageFont", labelFont);
            JOptionPane.showMessageDialog(null, "Congratulations! You are now registered as a Donor.\nPlease login again to access your dashboard.");
            dialog.dispose();
            new LoginPage().setVisible(true);
            this.dispose();
        });

        dialog.setVisible(true);
    }

    private void refreshMyRequests() {
        requestsContainer.removeAll();
        
        java.util.List<BloodRequest> pending = new java.util.ArrayList<>();
        java.util.List<BloodRequest> finalized = new java.util.ArrayList<>();

        for (BloodRequest req : DataStore.bloodRequests) {
            if (req.getRequesterEmail().equals(currentUser.getEmail())) {
                if (req.getStatus().equalsIgnoreCase("Pending")) pending.add(req);
                else finalized.add(req);
            }
        }

        if (pending.isEmpty() && finalized.isEmpty()) {
            JLabel emptyLabel = new JLabel("No requests sent yet.", SwingConstants.CENTER);
            emptyLabel.setFont(labelFont);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            requestsContainer.add(emptyLabel);
        } else {
            if (!pending.isEmpty()) {
                requestsContainer.add(createHeaderLabel("Pending Requests (" + pending.size() + ")", new Color(0, 102, 204)));
                for (BloodRequest req : pending) {
                    requestsContainer.add(createTrackingRow(req));
                    requestsContainer.add(Box.createVerticalStrut(10));
                }
            }
            
            if (!finalized.isEmpty()) {
                requestsContainer.add(Box.createVerticalStrut(15));
                requestsContainer.add(createHeaderLabel("Finalized Records (" + finalized.size() + ")", new Color(0, 153, 51)));
                for (BloodRequest req : finalized) {
                    requestsContainer.add(createTrackingRow(req));
                    requestsContainer.add(Box.createVerticalStrut(10));
                }
            }
        }

        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private JLabel createHeaderLabel(String text, Color color) {
        JLabel label = new JLabel(" " + text);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        label.setForeground(color);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, color));
        label.setMaximumSize(new Dimension(600, 35));
        return label;
    }

    private JPanel createTrackingRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(550, 110));
        row.setPreferredSize(new Dimension(500, 110));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        boolean isPending = req.getStatus().equalsIgnoreCase("Pending");
        String statusIcon = req.getStatus().equals("Accepted") ? "✓" : req.getStatus().equals("Declined") ? "✕" : "⏳";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "blue";
        
        String urgencyTag = "";
        if (req.getUrgency().equalsIgnoreCase("Emergency")) urgencyTag = " <font color='red'>[EMERGENCY]</font>";
        else if (req.getUrgency().equalsIgnoreCase("Urgent")) urgencyTag = " <font color='orange'>[URGENT]</font>";

        String info = "<html><font size='5'>" + statusIcon + " Request to: " + req.getDonorEmail() + urgencyTag + "<br>" +
                     "Status: <b><font color='" + statusColor + "'>" + req.getStatus() + "</font></b></font></html>";
        row.add(new JLabel(info), BorderLayout.CENTER);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        if (isPending) {
            RoundedButton cancelBtn = new RoundedButton("Cancel", new Color(200, 0, 0), new Color(255, 50, 50));
            cancelBtn.setPreferredSize(new Dimension(100, 40));
            cancelBtn.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Cancel this request?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    DataStore.deleteBloodRequest(req);
                    refreshMyRequests();
                }
            });
            btnPanel.add(cancelBtn);
        } else {
            RoundedButton deleteBtn = new RoundedButton("Clear", new Color(70, 70, 70), new Color(100, 100, 100));
            deleteBtn.setPreferredSize(new Dimension(100, 40));
            deleteBtn.addActionListener(e -> {
                DataStore.deleteBloodRequest(req);
                refreshMyRequests();
            });
            btnPanel.add(deleteBtn);
        }
        
        row.add(btnPanel, BorderLayout.EAST);
        return row;
    }
}
