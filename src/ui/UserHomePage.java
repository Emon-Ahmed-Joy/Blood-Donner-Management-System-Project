package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.User;
import model.BloodRequest;

/**
 * User Homepage for tracking requests.
 * @author Emon Ahmed Joy
 */
public class UserHomePage extends JFrame {
    private User currentUser;
    private JPanel requestsContainer;
    private JLabel detailsLabel;

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
        welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        card.add(welcomeLabel, BorderLayout.NORTH);

        // Notification Check
        if (user.hasUpdate()) {
            JOptionPane.showMessageDialog(this, "(!) One of your blood requests has been updated!", "Request Update", JOptionPane.INFORMATION_MESSAGE);
            user.setHasUpdate(false);
        }

        // Main Content Area
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setOpaque(false);

        // Left Side: Navigation Buttons & Profile
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        detailsLabel = new JLabel("<html><b>Location:</b> " + user.getLocation() + ", " + user.getState() + "</html>");
        detailsLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 0; navPanel.add(detailsLabel, gbc);

        // Standard size for all navigation buttons
        Dimension btnSize = new Dimension(280, 50);
        Font btnFont = new Font("Dialog", Font.BOLD, 14);

        RoundedButton editProfileBtn = new RoundedButton("✏️ Edit My Profile", new Color(180, 0, 0), new Color(220, 20, 20));
        editProfileBtn.setPreferredSize(btnSize);
        editProfileBtn.setFont(btnFont);
        gbc.gridy = 1; navPanel.add(editProfileBtn, gbc);

        RoundedButton changePassBtn = new RoundedButton("🔒 Change Password", new Color(180, 0, 0), new Color(220, 20, 20));
        changePassBtn.setPreferredSize(btnSize);
        changePassBtn.setFont(btnFont);
        gbc.gridy = 2; navPanel.add(changePassBtn, gbc);

        JButton registerBtn = new RoundedButton("<html><font color='red'>&hearts;</font> Register as a Donor</html>", new Color(180, 0, 0), new Color(220, 20, 20));
        registerBtn.setPreferredSize(btnSize);
        registerBtn.setFont(btnFont);
        gbc.gridy = 3; navPanel.add(registerBtn, gbc);
        
        JButton searchBtn = new RoundedButton("🔍 Search for Blood", new Color(180, 0, 0), new Color(220, 20, 20));
        searchBtn.setPreferredSize(btnSize);
        searchBtn.setFont(btnFont);
        gbc.gridy = 4; navPanel.add(searchBtn, gbc);

        // Right Side: My Requests Tracking
        JPanel trackingPanel = new JPanel(new BorderLayout());
        trackingPanel.setOpaque(false);
        JLabel trackingHeader = new JLabel("My Sent Requests Status", SwingConstants.CENTER);
        trackingHeader.setFont(new Font("Dialog", Font.BOLD, 18));
        trackingPanel.add(trackingHeader, BorderLayout.NORTH);

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
        editProfileBtn.addActionListener(e -> showEditProfileDialog());
        changePassBtn.addActionListener(e -> showChangePasswordDialog());

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

            if (!oldPass.equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(dialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "New password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                currentUser.setPassword(newPass);
                DataStore.updateUserPassword(currentUser); // ✅ DB te save
                JOptionPane.showMessageDialog(dialog, "Password updated successfully!");
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(this, "Edit Profile Details", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(currentUser.getName());
        JTextField stateF = new JTextField(currentUser.getState());
        JTextField locF = new JTextField(currentUser.getLocation());

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; dialog.add(stateF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r; dialog.add(new JLabel("City/Location:"), gbc);
        gbc.gridx = 1; dialog.add(locF, gbc); r++;

        RoundedButton saveBtn = new RoundedButton("Update My Info");
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            currentUser.setName(nameF.getText());
            currentUser.setState(stateF.getText());
            currentUser.setLocation(locF.getText());
            DataStore.updateUserProfile(currentUser); // DB te save
            detailsLabel.setText("<html><b>Location:</b> " + currentUser.getLocation() + ", " + currentUser.getState() + "</html>");
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
            dialog.dispose();
        });

        dialog.setVisible(true);
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
            JLabel emptyLabel = new JLabel("No requests sent yet.", SwingConstants.CENTER);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            requestsContainer.add(emptyLabel);
        }

        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private JPanel createTrackingRow(BloodRequest req) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(500, 60));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String statusIcon = req.getStatus().equals("Accepted") ? "V" : req.getStatus().equals("Declined") ? "X" : "?";
        String statusColor = req.getStatus().equals("Accepted") ? "green" : req.getStatus().equals("Declined") ? "red" : "blue";
        String info = "<html>" + statusIcon + " Request to: " + req.getDonorEmail() + "<br>Status: <b><font color='" + statusColor + "'>" + req.getStatus() + "</font></b></html>";
        
        row.add(new JLabel(info), BorderLayout.CENTER);
        return row;
    }
}
