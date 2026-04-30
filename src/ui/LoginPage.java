package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Admin;
import model.Donor;
import model.User;

/**
 * Unified Login Page for Admin and Users/Donors.
 */
public class LoginPage extends JFrame {
    private JTextField userEmailField, adminIdField;
    private JPasswordField userPasswordField, adminPasswordField;

    public LoginPage() {
        setTitle("Blood Donor Management System - Login");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Custom Gradient Background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());

        // Center Card
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(450, 550));
        card.setBackground(new Color(255, 255, 255, 240)); // Semi-transparent white
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel header = new JLabel("Blood Link", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 36));
        header.setForeground(new Color(180, 0, 0));
        card.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.addTab("User/Donor", createUserPanel());
        tabbedPane.addTab("Administrator", createAdminPanel());

        card.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(card);
        add(mainPanel);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        userEmailField = new JTextField(20);
        panel.add(userEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        userPasswordField = new JPasswordField(20);
        panel.add(userPasswordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(200, 0, 0));
        loginBtn.setForeground(Color.WHITE);
        panel.add(loginBtn, gbc);

        gbc.gridy = 3;
        JButton regBtn = new JButton("Register New Account");
        panel.add(regBtn, gbc);

        loginBtn.addActionListener(e -> handleUserLogin());
        regBtn.addActionListener(e -> {
            new RegistrationPage().setVisible(true);
            this.dispose();
        });

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Admin ID:"), gbc);
        gbc.gridx = 1;
        adminIdField = new JTextField(20);
        panel.add(adminIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        adminPasswordField = new JPasswordField(20);
        panel.add(adminPasswordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JButton loginBtn = new JButton("Admin Login");
        loginBtn.setBackground(new Color(50, 50, 50));
        loginBtn.setForeground(Color.WHITE);
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> handleAdminLogin());

        return panel;
    }

    private void handleUserLogin() {
        String email = userEmailField.getText();
        String password = new String(userPasswordField.getPassword());

        for (User user : DataStore.users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                if (user instanceof Donor) {
                    JOptionPane.showMessageDialog(this, "Welcome Donor: " + user.getName());
                    new DonorProfilePage((Donor)user).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Welcome User: " + user.getName());
                    new UserHomePage(user).setVisible(true);
                }
                this.dispose();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid Email or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleAdminLogin() {
        String id = adminIdField.getText();
        String password = new String(adminPasswordField.getPassword());

        for (Admin admin : DataStore.admins) {
            if (admin.getAdminId().equals(id) && admin.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(this, "Admin Login Successful");
                new AdminPage().setVisible(true);
                this.dispose();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid Admin Credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
