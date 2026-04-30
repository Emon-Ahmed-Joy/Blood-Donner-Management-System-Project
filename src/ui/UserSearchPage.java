package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Search Page with Animated Background.
 */
public class UserSearchPage extends JFrame {
    private JTextField bloodGroupField, stateField, locationField;
    private JTextArea resultsArea;

    public UserSearchPage() {
        setTitle("Search Blood Donors");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1100, 600);

        // Header
        JLabel title = new JLabel("Find a Life Saver", SwingConstants.CENTER);
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(title, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);

        // Search Form
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1; bloodGroupField = new JTextField(15); searchPanel.add(bloodGroupField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; stateField = new JTextField(15); searchPanel.add(stateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; locationField = new JTextField(15); searchPanel.add(locationField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton searchBtn = new JButton("Search Donors");
        searchPanel.add(searchBtn, gbc);

        mainPanel.add(searchPanel, BorderLayout.WEST);

        // Results
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setOpaque(false);
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultsPanel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        card.add(mainPanel, BorderLayout.CENTER);

        // Bottom: Back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton backBtn = new JButton("Back to Dashboard");
        bottomPanel.add(backBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> performSearch());
        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        bgPanel.add(card);
        add(bgPanel);
    }

    private void performSearch() {
        String bg = bloodGroupField.getText().trim().toLowerCase();
        String st = stateField.getText().trim().toLowerCase();
        String loc = locationField.getText().trim().toLowerCase();

        resultsArea.setText("Searching Database...\n");
        List<Donor> filtered = new ArrayList<>();
        for (Donor d : DataStore.donors) {
            if ((bg.isEmpty() || d.getBloodGroup().toLowerCase().contains(bg)) &&
                (st.isEmpty() || d.getState().toLowerCase().contains(st)) &&
                (loc.isEmpty() || d.getLocation().toLowerCase().contains(loc)) &&
                d.isAvailable()) {
                filtered.add(d);
            }
        }

        resultsArea.setText("");
        if (filtered.isEmpty()) {
            resultsArea.append("No donors found matching these criteria.");
        } else {
            for (Donor d : filtered) {
                resultsArea.append(d.toString() + "\nContact: " + d.getEmail() + "\n\n");
            }
        }
    }
}
