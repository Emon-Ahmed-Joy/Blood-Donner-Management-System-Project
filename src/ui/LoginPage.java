package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Admin;
import model.Donor;
import model.User;

/**
 * Modernized Login Page with Rounded Components and Shadow Cards.
 * @author Emon Ahmed Joy
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

        GradientPanel mainPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(450, 580);

        JLabel header = new JLabel("🩸 Blood Link", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 42));
        header.setForeground(new Color(180, 0, 0));
        card.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.addTab("👤 User/Donor", createUserPanel());
        tabbedPane.addTab("🛡️ Administrator", createAdminPanel());

        card.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(card);
        add(mainPanel);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📧 Email Address:"), gbc);
        gbc.gridx = 1; userEmailField = new JTextField(15); panel.add(userEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("🔒 Password:"), gbc);
        gbc.gridx = 1; userPasswordField = new JPasswordField(15); panel.add(userPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        RoundedButton loginBtn = new RoundedButton("🔑 Login to Account");
        panel.add(loginBtn, gbc);

        gbc.gridy = 3;
        RoundedButton regBtn = new RoundedButton("➕ Create New Account", new Color(50, 50, 50), new Color(80, 80, 80));
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
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("🆔 Admin ID:"), gbc);
        gbc.gridx = 1; adminIdField = new JTextField(15); panel.add(adminIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("🔒 Password:"), gbc);
        gbc.gridx = 1; adminPasswordField = new JPasswordField(15); panel.add(adminPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        RoundedButton loginBtn = new RoundedButton("🛡️ System Login", new Color(30, 30, 30), new Color(60, 60, 60));
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> handleAdminLogin());
        return panel;
    }

    private void handleUserLogin() {
        String email = userEmailField.getText();
        String password = new String(userPasswordField.getPassword());
        for (User user : DataStore.users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                if (user.isBlocked()) {
                    JOptionPane.showMessageDialog(this, "Your account has been blocked by the Administrator.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                DataStore.currentUser = user; // Track current session
                if (user instanceof Donor) {
                    new DonorProfilePage((Donor)user).setVisible(true);
                } else {
                    new UserHomePage(user).setVisible(true);
                }
                this.dispose();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Access Denied: Invalid Credentials", "Auth Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleAdminLogin() {
        String id = adminIdField.getText();
        String password = new String(adminPasswordField.getPassword());
        for (Admin admin : DataStore.admins) {
            if (admin.getAdminId().equals(id) && admin.getPassword().equals(password)) {
                new AdminPage().setVisible(true);
                this.dispose();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Access Denied: Invalid Admin ID", "Auth Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
