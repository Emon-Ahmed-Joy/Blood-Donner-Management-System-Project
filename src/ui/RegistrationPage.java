package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import model.Donor;
import model.User;

/**
 * Registration Page for new accounts.
 * @author Emon Ahmed Joy
 */
public class RegistrationPage extends JFrame {
    private JTextField nameF, emailF, stateF, locF;
    private JTextArea medicalF;
    private JComboBox<String> groupF;
    private JPasswordField passF;
    private JCheckBox isDonorCheck;
    private boolean isUpgradeMode = false;
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font fieldFont = new Font("Dialog", Font.PLAIN, 20);
    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private final String[] commonConditions = {"None", "Anemia", "Asthma", "Diabetes", "Hypertension", "Hepatitis", "Heart Disease", "Severe Allergy"};

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
        gbc.gridx = 1; 
        groupF = new JComboBox<>(bloodGroups);
        groupF.setEditable(true);
        groupF.setFont(fieldFont);
        formPanel.add(groupF, gbc); r++;

        gbc.gridx = 0; gbc.gridy = r;
        JPanel mlLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mlLabelPanel.setOpaque(false);
        JLabel ml = new JLabel("Medical Condition:");
        ml.setFont(labelFont);
        mlLabelPanel.add(ml);
        formPanel.add(mlLabelPanel, gbc);
        
        gbc.gridx = 1; 
        JPanel medicalPanel = new JPanel(new BorderLayout(5, 0));
        medicalPanel.setOpaque(false);
        
        medicalF = new JTextArea(3, 20);
        medicalF.setFont(fieldFont);
        medicalF.setLineWrap(true);
        medicalF.setWrapStyleWord(true);
        JScrollPane medicalScroll = new JScrollPane(medicalF);
        medicalScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        medicalScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Hide scrollbar arrows/track
        medicalPanel.add(medicalScroll, BorderLayout.CENTER);

        JButton mlBtn = new JButton("▼");
        mlBtn.setPreferredSize(new Dimension(30, 30));
        mlBtn.setFont(new Font("Arial", Font.BOLD, 12));
        mlBtn.setFocusPainted(false);
        medicalPanel.add(mlBtn, BorderLayout.EAST);
        
        formPanel.add(medicalPanel, gbc); r++;

        JPopupMenu conditionsMenu = new JPopupMenu();
        for (String condition : commonConditions) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(condition);
            item.setFont(new Font("Dialog", Font.PLAIN, 16));
            item.addActionListener(ev -> {
                String currentText = medicalF.getText().trim();
                List<String> items = new ArrayList<>(Arrays.asList(currentText.split(",\\s*")));
                items.removeIf(String::isEmpty);
                
                if (item.isSelected()) {
                    if (!items.contains(condition)) items.add(condition);
                    item.setForeground(new Color(0, 128, 0)); // Green text for selected
                } else {
                    items.remove(condition);
                    item.setForeground(Color.BLACK);
                }
                medicalF.setText(items.stream().collect(Collectors.joining(", ")));
            });
            conditionsMenu.add(item);
        }
        mlBtn.addActionListener(ev -> {
            // Update menu checkmarks based on current text
            String currentText = medicalF.getText().trim().toLowerCase();
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
            medicalScroll.setEnabled(false);
            mlBtn.setEnabled(false);
            isDonorCheck.addActionListener(e -> {
                boolean selected = isDonorCheck.isSelected();
                groupF.setEnabled(selected);
                medicalF.setEnabled(selected);
                medicalScroll.setEnabled(selected);
                mlBtn.setEnabled(selected);
            });
        }

        bgPanel.add(card);
        add(bgPanel);

        // Animation
        bgPanel.fadeIn();
    }
    private void handleRegistration() {
        String name = nameF == null ? "" : nameF.getText().trim();
        String email = emailF == null ? "" : emailF.getText().trim();
        String password = new String(passF.getPassword());
        String selectedGroup = (groupF.getEditor().getItem() != null) ? groupF.getEditor().getItem().toString().trim() : "";
        String medicalInfo = medicalF.getText().trim();

        // Common Validation
        if (!isUpgradeMode && (name.isEmpty() || email.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Please fill in all basic details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isUpgradeMode) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (User u : DataStore.users) {
                if (u.getEmail().equalsIgnoreCase(email)) {
                    JOptionPane.showMessageDialog(this, "This email is already registered!", "Conflict", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        if (isUpgradeMode) {
            if (!DataStore.checkPassword(password, DataStore.currentUser.getPassword())) {
                JOptionPane.showMessageDialog(this, "Incorrect password confirmation!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedGroup.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your blood group.");
                return;
            }

            User oldUser = DataStore.currentUser;
            Donor newDonor = new Donor(oldUser.getName().trim(), oldUser.getEmail().trim(), oldUser.getPassword().trim(), 
                                     selectedGroup, stateF.getText().trim(), locF.getText().trim(), medicalInfo);
            
            // Persist changes in-place
            DataStore.updateUser(newDonor);
            DataStore.loadDataFromDatabase();
            DataStore.currentUser = null; // Clear session for fresh login
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
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill basic details.");
                return;
            }

            String hashedPass = DataStore.hashPassword(password);
            if (isDonorCheck.isSelected()) {
                Donor newDonor = new Donor(name, email, hashedPass, selectedGroup, stateF.getText().trim(), locF.getText().trim(), medicalInfo);
                DataStore.addUser(newDonor);
            } else {
                User newUser = new User(name, email, hashedPass, stateF.getText().trim(), locF.getText().trim(), false);
                DataStore.addUser(newUser);
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
