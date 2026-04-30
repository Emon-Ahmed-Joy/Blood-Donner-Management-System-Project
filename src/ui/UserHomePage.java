package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * Homepage for non-donor users.
 */
public class UserHomePage extends JFrame {
    private User currentUser;

    public UserHomePage(User user) {
        this.currentUser = user;
        setTitle("Blood Donor Management - Home");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(200, 0, 0));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JButton registerBtn = new JButton("Register as a Donor");
        registerBtn.setPreferredSize(new Dimension(250, 100));
        registerBtn.setBackground(new Color(200, 0, 0));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton searchBtn = new JButton("Search for Blood");
        searchBtn.setPreferredSize(new Dimension(250, 100));
        searchBtn.setBackground(new Color(50, 50, 50));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(registerBtn, gbc);
        gbc.gridx = 1;
        contentPanel.add(searchBtn, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Actions
        registerBtn.addActionListener(e -> {
            new RegistrationPage().setVisible(true);
            this.dispose();
        });

        searchBtn.addActionListener(e -> {
            new UserSearchPage().setVisible(true);
            this.dispose();
        });

        JButton logoutBtn = new JButton("Logout");
        add(logoutBtn, BorderLayout.SOUTH);
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
    }
}
