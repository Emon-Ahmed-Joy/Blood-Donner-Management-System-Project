package ui;

import javax.swing.*;
import java.awt.*;

public class UserSearchPage extends JFrame {
    public UserSearchPage() {
        setTitle("User Blood Search");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Search for Blood Donors", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        searchPanel.add(new JLabel("Blood Group:"));
        JTextField bloodGroupField = new JTextField();
        searchPanel.add(bloodGroupField);

        searchPanel.add(new JLabel("State:"));
        JTextField stateField = new JTextField();
        searchPanel.add(stateField);

        searchPanel.add(new JLabel("Location:"));
        JTextField locationField = new JTextField();
        searchPanel.add(locationField);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Login");
        add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        // TODO: Add search logic here (Member 3/4)
        searchButton.addActionListener(e -> {
            String bloodGroup = bloodGroupField.getText();
            String state = stateField.getText();
            String location = locationField.getText();
            JOptionPane.showMessageDialog(this, "Searching for: " + bloodGroup + " in " + location + ", " + state);
            // This is where the backend logic will be called to perform the search
        });
    }
}
