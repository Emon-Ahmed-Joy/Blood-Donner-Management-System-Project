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
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font fieldFont = new Font("Dialog", Font.PLAIN, 20);

    public RegistrationPage() {
        this.isUpgradeMode = (DataStore.currentUser != null);
        
        setTitle(isUpgradeMode ? "Upgrade to Donor Account" : "Create New Account");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(800, 750);

        // Header
        JLabel title = new JLabel(isUpgradeMode ? "Become a Life Saver" : "Join Our Community", SwingConstants.CENTER);
        title.setIcon(new VectorIcon(VectorIcon.Type.HEART, 40, new Color(180, 0, 0)));
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("Dialog", Font.BOLD, 32));
        card.add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        if (!isUpgradeMode) {
            gbc.gridx = 0; gbc.gridy = r;
            JLabel nl = new JLabel("Full Name:");
            nl.setIcon(new VectorIcon(VectorIcon.Type.USER, 22));
            nl.setFont(labelFont);
            formPanel.add(nl, gbc);
            gbc.gridx = 1; nameF = new JTextField(25); nameF.setFont(fieldFont); formPanel.add(nameF, gbc); r++;

            gbc.gridx = 0; gbc.gridy = r;
            JLabel el = new JLabel("Email:");
            el.setIcon(new VectorIcon(VectorIcon.Type.EMAIL, 22));
            el.setFont(labelFont);
            formPanel.add(el, gbc);
            gbc.gridx = 1; emailF = new JTextField(25); emailF.setFont(fieldFont); formPanel.add(emailF, gbc); r++;
        }

        gbc.gridx = 0; gbc.gridy = r;
        JLabel pl = new JLabel(isUpgradeMode ? "Confirm Password:" : "Password:");
        pl.setIcon(new VectorIcon(VectorIcon.Type.LOCK, 22));
        pl.setFont(labelFont);
        formPanel.add(pl, gbc);
        gbc.gridx = 1; passF = new JPasswordField(25); passF.setFont(fieldFont); formPanel.add(passF, gbc); r++;

        if (!isUpgradeMode) {
            gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
            isDonorCheck = new JCheckBox("Register as a Blood Donor?");
            isDonorCheck.setOpaque(false);
            isDonorCheck.setFont(new Font("Dialog", Font.BOLD, 18));
            formPanel.add(isDonorCheck, gbc); r++;
            gbc.gridwidth = 1;
        }

        // Donor specific fields
        gbc.gridx = 0; gbc.gridy = r;
        JLabel gl = new JLabel("Blood Group:");
        gl.setIcon(new VectorIcon(VectorIcon.Type.HEART, 22, Color.RED));
        gl.setFont(labelFont);
        formPanel.add(gl, gbc);
        gbc.gridx = 1; groupF = new JTextField(25); groupF.setFont(fieldFont); formPanel.add(groupF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        JLabel ml = new JLabel("Medical Condition:");
        ml.setFont(labelFont);
        formPanel.add(ml, gbc);
        gbc.gridx = 1; medicalF = new JTextField(25); medicalF.setFont(fieldFont); formPanel.add(medicalF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        JLabel sl = new JLabel("State:");
        sl.setFont(labelFont);
        formPanel.add(sl, gbc);
        gbc.gridx = 1; stateF = new JTextField(25); stateF.setFont(fieldFont);
        if (isUpgradeMode) stateF.setText(DataStore.currentUser.getState());
        formPanel.add(stateF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        JLabel ll = new JLabel("Location:");
        ll.setFont(labelFont);
        formPanel.add(ll, gbc);
        gbc.gridx = 1; locF = new JTextField(25); locF.setFont(fieldFont);
        if (isUpgradeMode) locF.setText(DataStore.currentUser.getLocation());
        formPanel.add(locF, gbc); r++;

        card.add(formPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        RoundedButton regBtn = new RoundedButton(isUpgradeMode ? "Confirm Upgrade" : "Register Now");
        regBtn.setPreferredSize(new Dimension(220, 50));
        
        String backText = isUpgradeMode ? "Back to Home" : "Back to Login";
        RoundedButton backBtn = new RoundedButton(backText, new Color(50, 50, 50), new Color(80, 80, 80));
        backBtn.setPreferredSize(new Dimension(220, 50));
        
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
        String password = new String(passF.getPassword()).trim();

        if (isUpgradeMode) {
            if (!password.equals(DataStore.currentUser.getPassword())) {
                JOptionPane.showMessageDialog(this, "Incorrect password confirmation!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (groupF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your blood group.");
                return;
            }

            // Upgrade Logic
            User oldUser = DataStore.currentUser;
            Donor newDonor = new Donor(oldUser.getName().trim(), oldUser.getEmail().trim(), oldUser.getPassword().trim(), 
                                     groupF.getText().trim(), stateF.getText().trim(), locF.getText().trim(), medicalF.getText().trim());
            
            // Persist changes
            DataStore.deleteUser(oldUser);
            DataStore.addUser(newDonor);
            DataStore.currentUser = null; // Clear session for fresh login

            JOptionPane.showMessageDialog(this, "Account Upgraded to Donor! Please login again.");
            new LoginPage().setVisible(true);
            this.dispose();

        } else {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill basic details.");
                return;
            }

            if (isDonorCheck.isSelected()) {
                Donor newDonor = new Donor(name, email, password, groupF.getText().trim(), stateF.getText().trim(), locF.getText().trim(), medicalF.getText().trim());
                DataStore.addUser(newDonor);
            } else {
                User newUser = new User(name, email, password, stateF.getText().trim(), locF.getText().trim(), false);
                DataStore.addUser(newUser);
            }

            JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
            new LoginPage().setVisible(true);
            this.dispose();
        }
    }
}
