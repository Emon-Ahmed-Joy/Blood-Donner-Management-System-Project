package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Search Page with Interactive Results and Request Feature.
 */
public class UserSearchPage extends JFrame {
    private JTextField bloodGroupField, stateField, locationField;
    private JPanel resultsContainer;
    private JScrollPane scrollPane;

    public UserSearchPage() {
        setTitle("Search Blood Donors");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1150, 600);

        // Header
        JLabel title = new JLabel("Find a Life Saver", SwingConstants.CENTER);
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(title, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);

        // Search Form (Left Side)
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1; bloodGroupField = new JTextField(12); searchPanel.add(bloodGroupField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 1; stateField = new JTextField(12); searchPanel.add(stateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; locationField = new JTextField(12); searchPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        RoundedButton searchBtn = new RoundedButton("Search Donors");
        searchPanel.add(searchBtn, gbc);

        mainPanel.add(searchPanel, BorderLayout.WEST);

        // Results Container (Right Side)
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Donors"));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        card.add(mainPanel, BorderLayout.CENTER);

        // Bottom: Back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("Back to Login", new Color(50, 50, 50), new Color(80, 80, 80));
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
        
        // Initial empty state
        resultsContainer.add(new JLabel("Enter criteria and click Search..."));
    }

    private void performSearch() {
        String bg = bloodGroupField.getText().trim().toLowerCase();
        String st = stateField.getText().trim().toLowerCase();
        String loc = locationField.getText().trim().toLowerCase();

        resultsContainer.removeAll();
        List<Donor> filtered = new ArrayList<>();
        
        for (Donor d : DataStore.donors) {
            if ((bg.isEmpty() || d.getBloodGroup().toLowerCase().contains(bg)) &&
                (st.isEmpty() || d.getState().toLowerCase().contains(st)) &&
                (loc.isEmpty() || d.getLocation().toLowerCase().contains(loc)) &&
                d.isAvailable()) {
                filtered.add(d);
            }
        }

        if (filtered.isEmpty()) {
            resultsContainer.add(new JLabel("No donors found matching these criteria."));
        } else {
            for (Donor d : filtered) {
                resultsContainer.add(createDonorResultRow(d));
                resultsContainer.add(Box.createVerticalStrut(10)); // Spacer
            }
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private JPanel createDonorResultRow(Donor donor) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(800, 80));
        row.setPreferredSize(new Dimension(750, 80));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Info Label
        String info = "<html><b>" + donor.getName() + "</b> (" + donor.getBloodGroup() + ")<br>" +
                      donor.getLocation() + ", " + donor.getState() + "</html>";
        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.add(infoLabel, BorderLayout.CENTER);

        // Request Button
        RoundedButton requestBtn = new RoundedButton("Request Blood", new Color(180, 0, 0), new Color(220, 20, 20));
        requestBtn.setPreferredSize(new Dimension(150, 35));
        row.add(requestBtn, BorderLayout.EAST);

        requestBtn.addActionListener(e -> {
            showRequestForm(donor, requestBtn);
        });

        return row;
    }

    private void showRequestForm(Donor donor, RoundedButton btn) {
        JDialog dialog = new JDialog(this, "Blood Request Details", true);
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Donor Info Header
        JPanel donorHeader = new JPanel(new GridLayout(3, 1));
        donorHeader.setBorder(BorderFactory.createTitledBorder("Requesting From"));
        donorHeader.add(new JLabel("Name: " + donor.getName()));
        donorHeader.add(new JLabel("Contact: " + donor.getEmail()));
        donorHeader.add(new JLabel("Address: " + donor.getLocation() + ", " + donor.getState()));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(donorHeader, gbc);
        gbc.gridwidth = 1;

        JTextField patientF = new JTextField();
        JTextField hospitalF = new JTextField();
        JTextField locationF = new JTextField();
        JTextArea conditionA = new JTextArea(3, 20);
        conditionA.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1; dialog.add(patientF, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Hospital Name:"), gbc);
        gbc.gridx = 1; dialog.add(hospitalF, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Hospital Location:"), gbc);
        gbc.gridx = 1; dialog.add(locationF, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1; dialog.add(new JScrollPane(conditionA), gbc);

        RoundedButton submitBtn = new RoundedButton("Submit Request");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(submitBtn, gbc);

        submitBtn.addActionListener(ev -> {
            if (patientF.getText().isEmpty() || hospitalF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill required fields.");
                return;
            }

            String reqEmail = (DataStore.currentUser != null) ? DataStore.currentUser.getEmail() : "guest@system.com";
            String reqName = (DataStore.currentUser != null) ? DataStore.currentUser.getName() : "Guest";

            model.BloodRequest newRequest = new model.BloodRequest(
                reqEmail, reqName, donor.getEmail(), donor.getBloodGroup(),
                patientF.getText(), hospitalF.getText(), locationF.getText(), conditionA.getText()
            );
            DataStore.bloodRequests.add(newRequest);

            JOptionPane.showMessageDialog(dialog, "Request sent to " + donor.getName());
            btn.setEnabled(false);
            btn.setText("Requested");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }
}
