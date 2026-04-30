package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Donor;
import model.User;

/**
 * Registration Page with Animated Background.
 */
public class RegistrationPage extends JFrame {
    private JTextField nameF, emailF, groupF, stateF, locF;
    private JPasswordField passF;
    private JCheckBox isDonorCheck;

    public RegistrationPage() {
        setTitle("Create New Account");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(600, 650);

        // Header
        JLabel title = new JLabel("Join Our Community", SwingConstants.CENTER);
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; nameF = new JTextField(20); formPanel.add(nameF, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; emailF = new JTextField(20); formPanel.add(emailF, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; passF = new JPasswordField(20); formPanel.add(passF, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        isDonorCheck = new JCheckBox("Register as a Blood Donor?");
        isDonorCheck.setOpaque(false);
        isDonorCheck.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(isDonorCheck, gbc);

        // Donor specific fields
        gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1; groupF = new JTextField(20); formPanel.add(groupF, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; stateF = new JTextField(20); formPanel.add(stateF, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; locF = new JTextField(20); formPanel.add(locF, gbc);

        card.add(formPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton regBtn = new JButton("Register Now");
        JButton backBtn = new JButton("Back to Login");
        btnPanel.add(regBtn);
        btnPanel.add(backBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        // Actions
        regBtn.addActionListener(e -> handleRegistration());
        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        // Toggle donor fields visibility
        groupF.setEnabled(false);
        stateF.setEnabled(false);
        locF.setEnabled(false);
        isDonorCheck.addActionListener(e -> {
            boolean selected = isDonorCheck.isSelected();
            groupF.setEnabled(selected);
            stateF.setEnabled(selected);
            locF.setEnabled(selected);
        });

        bgPanel.add(card);
        add(bgPanel);
    }

    private void handleRegistration() {
        String name = nameF.getText();
        String email = emailF.getText();
        String password = new String(passF.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill basic details.");
            return;
        }

        if (isDonorCheck.isSelected()) {
            Donor newDonor = new Donor(name, email, password, groupF.getText(), stateF.getText(), locF.getText());
            DataStore.users.add(newDonor);
            DataStore.donors.add(newDonor);
        } else {
            User newUser = new User(name, email, password, false);
            DataStore.users.add(newUser);
        }

        JOptionPane.showMessageDialog(this, "Registration Successful!");
        new LoginPage().setVisible(true);
        this.dispose();
    }
}
