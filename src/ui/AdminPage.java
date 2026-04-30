package ui;

import database.DataStore;
import model.Donor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminPage extends JFrame {
    private JTextArea donorListArea;

    public AdminPage() {
        setTitle("Admin Panel - Manage Donors");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Admin Panel", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        donorListArea = new JTextArea();
        donorListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(donorListArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Donor List");
        JButton backButton = new JButton("Back to Login");
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadDonors());

        backButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        loadDonors(); // Load donors on initial display
    }

    private void loadDonors() {
        donorListArea.setText(""); // Clear existing text
        List<Donor> donors = DataStore.donors; // Using the in-memory DataStore for now
        if (donors.isEmpty()) {
            donorListArea.append("No donors registered yet.");
        } else {
            donorListArea.append("Registered Donors:\n\n");
            for (int i = 0; i < donors.size(); i++) {
                donorListArea.append((i + 1) + ". " + donors.get(i).toString() + "\n");
            }
        }
    }
}
