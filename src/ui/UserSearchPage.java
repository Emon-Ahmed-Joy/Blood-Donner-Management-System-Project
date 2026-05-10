package ui;

import database.DataStore;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import model.Donor;

/**
 * Search Page to find donors.
 * @author Emon Ahmed Joy
 */
public class UserSearchPage extends JFrame {
    private JTextField bloodGroupField, stateField, locationField;
    private FadingPanel resultsContainer;
    private JScrollPane scrollPane;
    private final Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private final Font fieldFont = new Font("Dialog", Font.PLAIN, 20);

    public UserSearchPage() {
        setTitle("Search Blood Donors");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GradientPanel bgPanel = new GradientPanel();
        JPanel card = GradientPanel.createCard(1150, 650);

        // Header
        JLabel title = new JLabel("Find a Life Saver", SwingConstants.CENTER);
        title.setIcon(new VectorIcon(VectorIcon.Type.HEART, 40, new Color(180, 0, 0)));
        title.setForeground(new Color(180, 0, 0));
        title.setFont(new Font("Dialog", Font.BOLD, 32));
        card.add(title, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);

        // Search Form (Left Side)
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria", 0, 0, labelFont));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel bgLbl = new JLabel("Blood Group:"); bgLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; searchPanel.add(bgLbl, gbc);
        gbc.gridx = 1; bloodGroupField = new JTextField(12); bloodGroupField.setFont(fieldFont); searchPanel.add(bloodGroupField, gbc); row++;

        JLabel stLbl = new JLabel("State:"); stLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; searchPanel.add(stLbl, gbc);
        gbc.gridx = 1; stateField = new JTextField(12); stateField.setFont(fieldFont); searchPanel.add(stateField, gbc); row++;

        JLabel locLbl = new JLabel("City/Location:"); locLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; searchPanel.add(locLbl, gbc);
        gbc.gridx = 1; locationField = new JTextField(12); locationField.setFont(fieldFont); searchPanel.add(locationField, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        RoundedButton searchBtn = new RoundedButton("Search Donors");
        searchBtn.setIcon(new VectorIcon(VectorIcon.Type.SEARCH, 24, Color.WHITE));
        searchBtn.setPreferredSize(new Dimension(200, 50));
        searchPanel.add(searchBtn, gbc);

        mainPanel.add(searchPanel, BorderLayout.WEST);

        // Results Container (Right Side)
        resultsContainer = new FadingPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(resultsContainer);
        optimizeScroll(scrollPane);
        scrollPane.setBorder(BorderFactory.createTitledBorder(null, "Available Donors", 0, 0, labelFont));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        card.add(mainPanel, BorderLayout.CENTER);

        // Bottom: Back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("Back to Home", new Color(50, 50, 50), new Color(80, 80, 80));
        backBtn.setPreferredSize(new Dimension(200, 45));
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
        JLabel initLbl = new JLabel("Enter criteria and click Search...");
        initLbl.setFont(labelFont);
        resultsContainer.add(initLbl);

        // Animation
        bgPanel.fadeIn();
    }

    private void optimizeScroll(JScrollPane sp) {
        sp.getVerticalScrollBar().setUnitIncrement(20);
        sp.setBorder(null);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
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
            JLabel noneLbl = new JLabel("No donors found matching these criteria.");
            noneLbl.setFont(labelFont);
            resultsContainer.add(noneLbl);
        } else {
            for (DonorMatch m : matched) {
                resultsContainer.add(createDonorResultRow(m.donor));
                resultsContainer.add(Box.createVerticalStrut(15));
            }
        }

        resultsContainer.fadeIn();
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private JPanel createDonorResultRow(Donor donor) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(800, 110));
        row.setPreferredSize(new Dimension(750, 110));
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Info Label
        String info = "<html><font size='5'><b>" + donor.getName() + "</b> <font color='red'>(" + donor.getBloodGroup() + ")</font><br>" +
                      donor.getLocation() + ", " + donor.getState() + "</font></html>";
        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
        row.add(infoLabel, BorderLayout.CENTER);

        // Request Button
        RoundedButton requestBtn = new RoundedButton("Request Blood", new Color(180, 0, 0), new Color(220, 20, 20));
        requestBtn.setPreferredSize(new Dimension(200, 50));
        row.add(requestBtn, BorderLayout.EAST);

        requestBtn.addActionListener(e -> showRequestForm(donor, requestBtn));

        return row;
    }

    private void showRequestForm(Donor donor, RoundedButton btn) {
        JDialog dialog = new JDialog(this, "Finalize Blood Request", true);
        dialog.setSize(550, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Donor Info Header
        JPanel donorHeader = new JPanel(new GridLayout(2, 1));
        donorHeader.setBorder(BorderFactory.createTitledBorder(null, "Recipient Information", 0, 0, labelFont));
        JLabel dh1 = new JLabel("Donor: " + donor.getName() + " (" + donor.getBloodGroup() + ")");
        dh1.setFont(labelFont);
        donorHeader.add(dh1);
        JLabel dh2 = new JLabel("Location: " + donor.getLocation());
        dh2.setFont(labelFont);
        donorHeader.add(dh2);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(donorHeader, gbc);
        gbc.gridwidth = 1;

        JTextField patientF = new JTextField(); patientF.setFont(fieldFont);
        JTextField hospitalF = new JTextField(); hospitalF.setFont(fieldFont);
        JTextField locationF = new JTextField(); locationF.setFont(fieldFont);
        JTextArea conditionA = new JTextArea(4, 20);
        conditionA.setLineWrap(true);
        conditionA.setFont(fieldFont);
        conditionA.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        int r = 1;
        JLabel pLbl = new JLabel("Patient Name:"); pLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r++; dialog.add(pLbl, gbc);
        gbc.gridx = 1; dialog.add(patientF, gbc);

        JLabel hLbl = new JLabel("Hospital Name:"); hLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r++; dialog.add(hLbl, gbc);
        gbc.gridx = 1; dialog.add(hospitalF, gbc);

        JLabel hlLbl = new JLabel("Hospital Location:"); hlLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r++; dialog.add(hlLbl, gbc);
        gbc.gridx = 1; dialog.add(locationF, gbc);

        JLabel cLbl = new JLabel("Condition:"); cLbl.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = r++; dialog.add(cLbl, gbc);
        gbc.gridx = 1; dialog.add(new JScrollPane(conditionA), gbc);

        RoundedButton submitBtn = new RoundedButton("Send Emergency Request");
        submitBtn.setPreferredSize(new Dimension(250, 50));
        gbc.gridx = 0; gbc.gridy = r++; gbc.gridwidth = 2;
        dialog.add(submitBtn, gbc);

        submitBtn.addActionListener(ev -> {
            if (patientF.getText().isEmpty() || hospitalF.getText().isEmpty()) {
                UIManager.put("OptionPane.messageFont", labelFont);
                JOptionPane.showMessageDialog(dialog, "Please fill required fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String reqEmail = (DataStore.currentUser != null) ? DataStore.currentUser.getEmail() : "guest@system.com";
            String reqName = (DataStore.currentUser != null) ? DataStore.currentUser.getName() : "Guest";

            model.BloodRequest newRequest = new model.BloodRequest(
                reqEmail, reqName, donor.getEmail(), donor.getBloodGroup(),
                patientF.getText(), hospitalF.getText(), locationF.getText(), conditionA.getText()
            );
            DataStore.addBloodRequest(newRequest);

            UIManager.put("OptionPane.messageFont", labelFont);
            JOptionPane.showMessageDialog(dialog, "Request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            btn.setEnabled(false);
            btn.setText("Requested");
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
