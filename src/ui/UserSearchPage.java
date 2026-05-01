package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Advanced Search Page with Proximity Weighting and Interactive Results.
 * @author Emon Ahmed Joy
 */
public class UserSearchPage extends JFrame {
    private JTextField bloodGroupField, stateField, locationField;
    private JPanel resultsContainer;
    private JScrollPane scrollPane;

    public UserSearchPage() {
        setTitle("🔍 Search Blood Donors");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1150, 600);

        // Header
        JLabel title = new JLabel("💉 Find a Life Saver", SwingConstants.CENTER);
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(title, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);

        // Search Form (Left Side)
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createTitledBorder("📋 Search Criteria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("🩸 Blood Group:"), gbc);
        gbc.gridx = 1; bloodGroupField = new JTextField(12); searchPanel.add(bloodGroupField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("🗺️ State:"), gbc);
        gbc.gridx = 1; stateField = new JTextField(12); searchPanel.add(stateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("📍 City/Location:"), gbc);
        gbc.gridx = 1; locationField = new JTextField(12); searchPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        RoundedButton searchBtn = new RoundedButton("🔍 Search Donors");
        searchPanel.add(searchBtn, gbc);

        mainPanel.add(searchPanel, BorderLayout.WEST);

        // Results Container (Right Side)
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(BorderFactory.createTitledBorder("✨ Available Donors (Sorted by Proximity)"));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        card.add(mainPanel, BorderLayout.CENTER);

        // Bottom: Back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("⬅️ Back to Home", new Color(50, 50, 50), new Color(80, 80, 80));
        bottomPanel.add(backBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> performSearch());
        backBtn.addActionListener(e -> {
            if (DataStore.currentUser instanceof Donor) {
                new DonorProfilePage((Donor) DataStore.currentUser).setVisible(true);
            } else {
                new UserHomePage(DataStore.currentUser).setVisible(true);
            }
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
        List<DonorMatch> matched = new ArrayList<>();
        
        for (Donor d : DataStore.donors) {
            int score = 0;
            boolean matchesBG = bg.isEmpty() || d.getBloodGroup().toLowerCase().equals(bg);
            boolean matchesST = st.isEmpty() || d.getState().toLowerCase().contains(st);
            boolean matchesLOC = loc.isEmpty() || d.getLocation().toLowerCase().contains(loc);

            if (matchesBG && matchesST && matchesLOC && d.isAvailable()) {
                // Scoring system for "Proximity"
                if (d.getBloodGroup().toLowerCase().equals(bg)) score += 100;
                if (!loc.isEmpty() && d.getLocation().toLowerCase().equals(loc)) score += 50;
                if (!st.isEmpty() && d.getState().toLowerCase().equals(st)) score += 20;
                
                matched.add(new DonorMatch(d, score));
            }
        }

        // Sort by score (descending)
        matched.sort(Comparator.comparingInt(m -> -m.score));

        if (matched.isEmpty()) {
            resultsContainer.add(new JLabel("No donors found matching these criteria. 🍃"));
        } else {
            for (DonorMatch m : matched) {
                resultsContainer.add(createDonorResultRow(m.donor));
                resultsContainer.add(Box.createVerticalStrut(10));
            }
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private JPanel createDonorResultRow(Donor donor) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(800, 90));
        row.setPreferredSize(new Dimension(750, 90));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Info Label
        String info = "<html><b>👤 " + donor.getName() + "</b> <font color='red'>(" + donor.getBloodGroup() + ")</font><br>" +
                      "📍 " + donor.getLocation() + ", " + donor.getState() + "</html>";
        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        row.add(infoLabel, BorderLayout.CENTER);

        // Request Button
        RoundedButton requestBtn = new RoundedButton("📧 Request Blood", new Color(180, 0, 0), new Color(220, 20, 20));
        requestBtn.setPreferredSize(new Dimension(170, 40));
        row.add(requestBtn, BorderLayout.EAST);

        requestBtn.addActionListener(e -> showRequestForm(donor, requestBtn));

        return row;
    }

    private void showRequestForm(Donor donor, RoundedButton btn) {
        JDialog dialog = new JDialog(this, "🩸 Finalize Blood Request", true);
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Donor Info Header
        JPanel donorHeader = new JPanel(new GridLayout(2, 1));
        donorHeader.setBorder(BorderFactory.createTitledBorder("Recipient Information"));
        donorHeader.add(new JLabel("Donor: " + donor.getName() + " (" + donor.getBloodGroup() + ")"));
        donorHeader.add(new JLabel("Location: " + donor.getLocation()));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(donorHeader, gbc);
        gbc.gridwidth = 1;

        JTextField patientF = new JTextField();
        JTextField hospitalF = new JTextField();
        JTextField locationF = new JTextField();
        JTextArea conditionA = new JTextArea(3, 20);
        conditionA.setLineWrap(true);
        conditionA.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        int r = 1;
        gbc.gridx = 0; gbc.gridy = r++; dialog.add(new JLabel("👤 Patient Name:"), gbc);
        gbc.gridx = 1; dialog.add(patientF, gbc);

        gbc.gridx = 0; gbc.gridy = r++; dialog.add(new JLabel("🏥 Hospital Name:"), gbc);
        gbc.gridx = 1; dialog.add(hospitalF, gbc);

        gbc.gridx = 0; gbc.gridy = r++; dialog.add(new JLabel("📍 Hospital Location:"), gbc);
        gbc.gridx = 1; dialog.add(locationF, gbc);

        gbc.gridx = 0; gbc.gridy = r++; dialog.add(new JLabel("🩺 Condition:"), gbc);
        gbc.gridx = 1; dialog.add(new JScrollPane(conditionA), gbc);

        RoundedButton submitBtn = new RoundedButton("🚀 Send Emergency Request");
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        dialog.add(submitBtn, gbc);

        submitBtn.addActionListener(ev -> {
            if (patientF.getText().isEmpty() || hospitalF.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill required fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String reqEmail = (DataStore.currentUser != null) ? DataStore.currentUser.getEmail() : "guest@system.com";
            String reqName = (DataStore.currentUser != null) ? DataStore.currentUser.getName() : "Guest";

            model.BloodRequest newRequest = new model.BloodRequest(
                reqEmail, reqName, donor.getEmail(), donor.getBloodGroup(),
                patientF.getText(), hospitalF.getText(), locationF.getText(), conditionA.getText()
            );
            DataStore.bloodRequests.add(newRequest);

            JOptionPane.showMessageDialog(dialog, "Request sent successfully! 🕊️", "Success", JOptionPane.INFORMATION_MESSAGE);
            btn.setEnabled(false);
            btn.setText("✅ Requested");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private static class DonorMatch {
        Donor donor;
        int score;
        DonorMatch(Donor donor, int score) {
            this.donor = donor;
            this.score = score;
        }
    }
}
