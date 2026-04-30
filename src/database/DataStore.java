package database;

import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Donor;
import model.User;

import model.BloodRequest;

public class DataStore {
    public static List<User> users = new ArrayList<>();
    public static List<Admin> admins = new ArrayList<>();
    public static List<Donor> donors = new ArrayList<>();
    public static List<BloodRequest> bloodRequests = new ArrayList<>();

    public static User currentUser; // To track who is logged in and making requests

    static {
        // Sample Admins
        admins.add(new Admin("admin", "admin123"));

        // Sample Donors
        Donor emon = new Donor("Emon Ahmed Joy", "emon@gmail.com", "pass123", "O+", "Dhaka", "Savar");
        users.add(emon);
        donors.add(emon);

        Donor durjoy = new Donor("Durjoy", "durjoy@gmail.com", "pass456", "O-", "Dhaka", "Khagan");
        users.add(durjoy);
        donors.add(durjoy);

        // Sample User
        users.add(new User("Normal User", "user@gmail.com", "pass123", false));
    }
}
