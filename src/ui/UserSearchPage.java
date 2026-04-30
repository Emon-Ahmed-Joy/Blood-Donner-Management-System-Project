package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Page to search for blood donors.
 */
public class UserSearchPage extends JFrame {
    private JTextField bloodGroupField, stateField, locationField;
    private JTextArea resultsArea;

    public UserSearchPage() {
        setTitle("Search Blood Donors");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(200, 0, 0));
        JLabel title = new JLabel("Find a Donor", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Blood Group (e.g., O+):"), gbc);
        gbc.gridx = 1; bloodGroupField = new JTextField(10); searchPanel.add(bloodGroupField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; stateField = new JTextField(10); searchPanel.add(stateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Location/City:"), gbc);
        gbc.gridx = 1; locationField = new JTextField(10); searchPanel.add(locationField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JButton searchBtn = new JButton("Search Now");
        searchBtn.setBackground(new Color(200, 0, 0));
        searchBtn.setForeground(Color.WHITE);
        searchPanel.add(searchBtn, gbc);

        add(searchPanel, BorderLayout.WEST);

        // Results Panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Donors Found"));
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsPanel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.CENTER);

        // Bottom: Back
        JButton backBtn = new JButton("Back to Login");
        add(backBtn, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> performSearch());
        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
    }

    private void performSearch() {
        String bg = bloodGroupField.getText().trim().toLowerCase();
        String st = stateField.getText().trim().toLowerCase();
        String loc = locationField.getText().trim().toLowerCase();

        resultsArea.setText("Searching...\n");
        List<Donor> filtered = new ArrayList<>();

        for (Donor d : DataStore.donors) {
            boolean matchBg = bg.isEmpty() || d.getBloodGroup().toLowerCase().contains(bg);
            boolean matchSt = st.isEmpty() || d.getState().toLowerCase().contains(st);
            boolean matchLoc = loc.isEmpty() || d.getLocation().toLowerCase().contains(loc);

            if (matchBg && matchSt && matchLoc && d.isAvailable()) {
                filtered.add(d);
            }
        }

        resultsArea.setText("");
        if (filtered.isEmpty()) {
            resultsArea.append("No available donors found matching your criteria.");
        } else {
            for (Donor d : filtered) {
                resultsArea.append(d.toString() + "\nContact: " + d.getEmail() + "\n\n");
            }
        }
    }
}
