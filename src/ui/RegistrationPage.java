package ui;

import database.DataStore;
import java.awt.*;
import javax.swing.*;
import model.Donor;
import model.User;

/**
 * Registration Page for new accounts.
 * @author Emon Ahmed Joy
 */
public class RegistrationPage extends JFrame {
    private JTextField nameF, emailF, groupF, stateF, locF, medicalF;
    private JPasswordField passF;
    private JCheckBox isDonorCheck;
    private boolean isUpgradeMode = false;

    public RegistrationPage() {
        this.isUpgradeMode = (DataStore.currentUser != null);
        
        setTitle(isUpgradeMode ? "Upgrade to Donor Account" : "Create New Account");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(600, 680);

        // Header
        JLabel title = new JLabel(isUpgradeMode ? "Become a Life Saver" : "Join Our Community", SwingConstants.CENTER);
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("Dialog", Font.BOLD, 28));
        card.add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        if (!isUpgradeMode) {
            gbc.gridx = 0; gbc.gridy = r;
            formPanel.add(new JLabel("Full Name:"), gbc);
            gbc.gridx = 1; nameF = new JTextField(20); formPanel.add(nameF, gbc); r++;

            gbc.gridx = 0; gbc.gridy = r;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; emailF = new JTextField(20); formPanel.add(emailF, gbc); r++;
        }

        gbc.gridx = 0; gbc.gridy = r;
        formPanel.add(new JLabel(isUpgradeMode ? "Confirm Password:" : "Password:"), gbc);
        gbc.gridx = 1; passF = new JPasswordField(20); formPanel.add(passF, gbc); r++;

        if (!isUpgradeMode) {
            gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
            isDonorCheck = new JCheckBox("Register as a Blood Donor?");
            isDonorCheck.setOpaque(false);
            isDonorCheck.setFont(new Font("Dialog", Font.BOLD, 14));
            formPanel.add(isDonorCheck, gbc); r++;
            gbc.gridwidth = 1;
        }

        // Donor specific fields
        gbc.gridx = 0; gbc.gridy = r;
        formPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1; groupF = new JTextField(20); formPanel.add(groupF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        formPanel.add(new JLabel("Medical Condition:"), gbc);
        gbc.gridx = 1; medicalF = new JTextField(20); formPanel.add(medicalF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        formPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; stateF = new JTextField(20); 
        if (isUpgradeMode) stateF.setText(DataStore.currentUser.getState());
        formPanel.add(stateF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; locF = new JTextField(20); 
        if (isUpgradeMode) locF.setText(DataStore.currentUser.getLocation());
        formPanel.add(locF, gbc); r++;

        card.add(formPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton regBtn = new RoundedButton(isUpgradeMode ? "Confirm Upgrade" : "Register Now");
        
        String backText = isUpgradeMode ? "Back to Home" : "Back to Login";
        JButton backBtn = new RoundedButton(backText, new Color(50, 50, 50), new Color(80, 80, 80));
        
        btnPanel.add(regBtn);
        btnPanel.add(backBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        // Actions
        regBtn.addActionListener(e -> handleRegistration());
        backBtn.addActionListener(e -> {
            if (isUpgradeMode) {
                new UserHomePage(DataStore.currentUser).setVisible(true);
            } else {
                new LoginPage().setVisible(true);
            }
            this.dispose();
        });

        // Toggle donor specific fields
        if (!isUpgradeMode) {
            groupF.setEnabled(false);
            medicalF.setEnabled(false);
            isDonorCheck.addActionListener(e -> {
                boolean selected = isDonorCheck.isSelected();
                groupF.setEnabled(selected);
                medicalF.setEnabled(selected);
            });
        }

        bgPanel.add(card);
        add(bgPanel);

        // Animation
        bgPanel.fadeIn();
    }
    private void handleRegistration() {
        String password = new String(passF.getPassword());

        if (isUpgradeMode) {
            if (!password.equals(DataStore.currentUser.getPassword())) {
                JOptionPane.showMessageDialog(this, "Incorrect password confirmation!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (groupF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your blood group.");
                return;
            }

            User oldUser = DataStore.currentUser;
            Donor newDonor = new Donor(oldUser.getName(), oldUser.getEmail(), oldUser.getPassword(),
                    groupF.getText(), stateF.getText(), locF.getText(), medicalF.getText());

            DataStore.upgradeUserToDonor(oldUser, newDonor); // DB te save
            DataStore.users.remove(oldUser);
            DataStore.users.add(newDonor);
            DataStore.donors.add(newDonor);
            DataStore.currentUser = null;

            JOptionPane.showMessageDialog(this, "Account Upgraded to Donor! Please login again.");
            new LoginPage().setVisible(true);
            this.dispose();

        } else {
            String name = nameF.getText();
            String email = emailF.getText();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill basic details.");
                return;
            }

            if (isDonorCheck.isSelected()) {
                Donor newDonor = new Donor(name, email, password, groupF.getText(), stateF.getText(), locF.getText(), medicalF.getText());
                DataStore.saveUser(newDonor); // DB  te save
                DataStore.users.add(newDonor);
                DataStore.donors.add(newDonor);
            } else {
                User newUser = new User(name, email, password, stateF.getText(), locF.getText(), false);
                DataStore.saveUser(newUser); //DB te save
                DataStore.users.add(newUser);
            }

            JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
            new LoginPage().setVisible(true);
            this.dispose();
        }
    }
}
