package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Admin;
import model.Donor;
import model.User;

/**
 * Login Page for the application.
 * @author Emon Ahmed Joy
 */
public class LoginPage extends JFrame {
    private JTextField userEmailField, adminIdField;
    private JPasswordField userPasswordField, adminPasswordField;
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font fieldFont = new Font("Dialog", Font.PLAIN, 20);

    public LoginPage() {
        setTitle("Blood Donor Management System - Login");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GradientPanel mainPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(600, 650); // Balanced width for 17 chars + labels

        JLabel header = new JLabel("Blood Link", SwingConstants.CENTER);
        header.setIcon(new VectorIcon(VectorIcon.Type.HEART, 48, new Color(180, 0, 0)));
        header.setFont(new Font("Dialog", Font.BOLD, 48));
        card.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 18));
        tabbedPane.addTab("User/Donor", new VectorIcon(VectorIcon.Type.USER, 22), createUserPanel());
        tabbedPane.addTab("Administrator", new VectorIcon(VectorIcon.Type.SHIELD, 22, new Color(50, 50, 50)), createAdminPanel());

        card.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(card);
        add(mainPanel);

        // Animation
        mainPanel.fadeIn();
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLbl = new JLabel("Email Address:");
        emailLbl.setIcon(new VectorIcon(VectorIcon.Type.EMAIL, 24));
        emailLbl.setFont(labelFont);
        panel.add(emailLbl, gbc);
        gbc.gridx = 1; userEmailField = new JTextField(17); userEmailField.setFont(fieldFont); panel.add(userEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setIcon(new VectorIcon(VectorIcon.Type.LOCK, 24));
        passLbl.setFont(labelFont);
        panel.add(passLbl, gbc);
        gbc.gridx = 1; userPasswordField = new JPasswordField(17); userPasswordField.setFont(fieldFont); panel.add(userPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        RoundedButton loginBtn = new RoundedButton("Login to Account");
        loginBtn.setIcon(new VectorIcon(VectorIcon.Type.KEY, 24, Color.WHITE));
        panel.add(loginBtn, gbc);

        gbc.gridy = 3;
        RoundedButton regBtn = new RoundedButton("Create New Account", new Color(50, 50, 50), new Color(80, 80, 80));
        regBtn.setIcon(new VectorIcon(VectorIcon.Type.PLUS, 24, Color.WHITE));
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
        JLabel idLbl = new JLabel("Admin ID:");
        idLbl.setIcon(new VectorIcon(VectorIcon.Type.ADMIN, 24, new Color(50, 50, 50)));
        idLbl.setFont(labelFont);
        panel.add(idLbl, gbc);
        gbc.gridx = 1; adminIdField = new JTextField(12); adminIdField.setFont(fieldFont); panel.add(adminIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel aPassLbl = new JLabel("Password:");
        aPassLbl.setIcon(new VectorIcon(VectorIcon.Type.LOCK, 24, new Color(50, 50, 50)));
        aPassLbl.setFont(labelFont);
        panel.add(aPassLbl, gbc);
        gbc.gridx = 1; adminPasswordField = new JPasswordField(12); adminPasswordField.setFont(fieldFont); panel.add(adminPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        RoundedButton loginBtn = new RoundedButton("System Login", new Color(30, 30, 30), new Color(60, 60, 60));
        loginBtn.setIcon(new VectorIcon(VectorIcon.Type.SHIELD, 24, Color.WHITE));
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> handleAdminLogin());
        return panel;
    }

    private void handleUserLogin() {
        String email = userEmailField.getText().trim();
        String password = new String(userPasswordField.getPassword()).trim();
        for (User user : DataStore.users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
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
        String id = adminIdField.getText().trim();
        String password = new String(adminPasswordField.getPassword()).trim();
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
