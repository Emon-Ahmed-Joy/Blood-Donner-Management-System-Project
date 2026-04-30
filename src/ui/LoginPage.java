package ui;

import database.DataStore;
import model.Donor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Blood Donor Management - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginBtn = new JButton("Login as Donor");
        add(loginBtn);

        JButton userBtn = new JButton("User Search");
        add(userBtn);

        JButton adminBtn = new JButton("Admin Login");
        add(adminBtn);

        JButton registerBtn = new JButton("Register");
        add(registerBtn);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passwordField.getPassword());
            boolean found = false;
            for (Donor d : DataStore.donors) {
                if (d.getEmail().equals(email) && d.getPassword().equals(pass)) {
                    found = true;
                    JOptionPane.showMessageDialog(this, "Welcome " + d.getName());
                    // Open Donor Home Page
                    break;
                }
            }
            if (!found) JOptionPane.showMessageDialog(this, "Invalid Credentials");
        });

        registerBtn.addActionListener(e -> {
            new RegistrationPage().setVisible(true);
            this.dispose();
        });

        userBtn.addActionListener(e -> {
            new UserSearchPage().setVisible(true);
            this.dispose();
        });

        adminBtn.addActionListener(e -> {
            String pass = JOptionPane.showInputDialog("Enter Admin Password:");
            if ("admin123".equals(pass)) {
                new AdminPage().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong Password");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
