package ui;

import database.DataStore;
import model.Donor;

import javax.swing.*;
import java.awt.*;

public class RegistrationPage extends JFrame {
    public RegistrationPage() {
        setTitle("Donor Registration");
        setSize(400, 400);
        setLayout(new GridLayout(7, 2, 5, 5));

        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        JTextField passF = new JTextField();
        JTextField groupF = new JTextField();
        JTextField stateF = new JTextField();
        JTextField locF = new JTextField();

        add(new JLabel("Name:")); add(nameF);
        add(new JLabel("Email:")); add(emailF);
        add(new JLabel("Password:")); add(passF);
        add(new JLabel("Blood Group:")); add(groupF);
        add(new JLabel("State:")); add(stateF);
        add(new JLabel("Location:")); add(locF);

        JButton regBtn = new JButton("Register");
        add(regBtn);
        JButton backBtn = new JButton("Back");
        add(backBtn);

        regBtn.addActionListener(e -> {
            DataStore.donors.add(new Donor(nameF.getText(), emailF.getText(), passF.getText(), groupF.getText(), stateF.getText(), locF.getText()));
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            new LoginPage().setVisible(true);
            this.dispose();
        });

        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
    }
}
