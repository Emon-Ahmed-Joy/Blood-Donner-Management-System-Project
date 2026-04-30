package ui;

import java.awt.*;
import javax.swing.*;
import model.User;

/**
 * User Homepage with Animated Background.
 */
public class UserHomePage extends JFrame {
    private User currentUser;

    public UserHomePage(User user) {
        this.currentUser = user;
        setTitle("Blood Donor Management - Home");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(800, 500);

        // Header
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(180, 0, 0));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        card.add(welcomeLabel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JButton registerBtn = new JButton("Register as a Donor");
        registerBtn.setPreferredSize(new Dimension(300, 150));
        registerBtn.setBackground(new Color(180, 0, 0));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton searchBtn = new JButton("Search for Blood");
        searchBtn.setPreferredSize(new Dimension(300, 150));
        searchBtn.setBackground(new Color(50, 50, 50));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("SansSerif", Font.BOLD, 20));

        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(registerBtn, gbc);
        gbc.gridx = 1;
        contentPanel.add(searchBtn, gbc);

        card.add(contentPanel, BorderLayout.CENTER);

        // Bottom: Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton logoutBtn = new JButton("Logout Account");
        bottomPanel.add(logoutBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
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
    }
}
