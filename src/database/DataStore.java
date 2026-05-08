package database;

import model.BloodRequest;
import model.Donor;
import model.User;
import model.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {

    static final String URL = "jdbc:mysql://localhost:3306/blood_bank";
    static final String USER = "root";
    static final String PASSWORD = "your_password_here";

    public static List<BloodRequest> bloodRequests = new ArrayList<>();
    public static List<Donor> donors = new ArrayList<>();
    public static List<User> users = new ArrayList<>();
    public static List<Admin> admins = new ArrayList<>();
    public static User currentUser = null;

    //App start hole data load hobe
    public static void loadAll() {
        loadAdmins();
        loadUsers(); // users + donors দুটোই load হবে
        loadBloodRequests();
    }

    public static void updateRequestStatus(BloodRequest request, String status) {
        String query = "UPDATE blood_requests SET status = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, request.getId());
            ps.executeUpdate();
            request.setStatus(status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserBlockStatus(User user) {
        String query = "UPDATE users SET isBlocked = ? WHERE email = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setBoolean(1, user.isBlocked());
            ps.setString(2, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // new User save
    public static void saveUser(User user) {
        String query = "INSERT INTO users (name, email, password, state, location, isDonor, bloodGroup, medicalCondition) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getState());
            ps.setString(5, user.getLocation());
            ps.setBoolean(6, user instanceof Donor);
            ps.setString(7, user instanceof Donor ? ((Donor) user).getBloodGroup() : null);
            ps.setString(8, user instanceof Donor ? ((Donor) user).getMedicalCondition() : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Blood Request DB e save hobe
    public static void saveBloodRequest(BloodRequest request) {
        String query = "INSERT INTO blood_requests (requester_email, requester_name, donor_email, blood_group, " +
                "patient_name, hospital_name, location, medical_condition, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, request.getRequesterEmail());
            ps.setString(2, request.getRequesterName());
            ps.setString(3, request.getDonorEmail());
            ps.setString(4, request.getBloodGroup());
            ps.setString(5, request.getPatientName());
            ps.setString(6, request.getHospitalName());
            ps.setString(7, request.getLocation());
            ps.setString(8, request.getMedicalCondition());
            ps.setString(9, request.getStatus());
            ps.executeUpdate();

            // DB থেকে generated id নিয়ে request এ set করো
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                request.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Password update korle DB te save
    public static void updateUserProfile(User user) {
        String query = "UPDATE users SET name = ?, state = ?, location = ? WHERE email = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getState());
            ps.setString(3, user.getLocation());
            ps.setString(4, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserPassword(User user) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Profile edit & Password change DB te save hobe
    public static void updateDonorProfile(Donor donor) {
        String query = "UPDATE users SET name = ?, state = ?, location = ?, bloodGroup = ?, medicalCondition = ?, isDonor = true WHERE email = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, donor.getName());
            ps.setString(2, donor.getState());
            ps.setString(3, donor.getLocation());
            ps.setString(4, donor.getBloodGroup());
            ps.setString(5, donor.getMedicalCondition());
            ps.setString(6, donor.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // User ke Donor a upgrade
    public static void upgradeUserToDonor(User oldUser, Donor newDonor) {
        String query = "UPDATE users SET isDonor = true, bloodGroup = ?, medicalCondition = ?, state = ?, location = ? WHERE email = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newDonor.getBloodGroup());
            ps.setString(2, newDonor.getMedicalCondition());
            ps.setString(3, newDonor.getState());
            ps.setString(4, newDonor.getLocation());
            ps.setString(5, oldUser.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadAdmins() {
        admins.clear();
        String query = "SELECT * FROM admins";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Admin a = new Admin(
                        rs.getString("adminId"),  // username → adminId
                        rs.getString("password")
                );
                admins.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadUsers() {
        users.clear();
        donors.clear();
        String query = "SELECT * FROM users";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                boolean isDonor = rs.getBoolean("isDonor");
                if (isDonor) {
                    Donor d = new Donor(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("bloodGroup"),
                            rs.getString("state"),
                            rs.getString("location"),
                            rs.getString("medicalCondition")
                    );
                    d.setBlocked(rs.getBoolean("isBlocked"));
                    users.add(d);
                    donors.add(d);
                } else {
                    User u = new User(
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("state"),
                            rs.getString("location"),
                            false
                    );
                    u.setBlocked(rs.getBoolean("isBlocked"));
                    users.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadBloodRequests() {
        bloodRequests.clear();
        String query = "SELECT * FROM blood_requests";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BloodRequest req = new BloodRequest(
                        rs.getString("requester_email"),
                        rs.getString("requester_name"),
                        rs.getString("donor_email"),
                        rs.getString("blood_group"),
                        rs.getString("patient_name"),
                        rs.getString("hospital_name"),
                        rs.getString("location"),
                        rs.getString("medical_condition")
                );
                req.setId(rs.getInt("id"));
                req.setStatus(rs.getString("status"));
                bloodRequests.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Checking JDBC Connection...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            if (con != null) {
                System.out.println("✅ Connected Successfully!");
                con.close();
            }
        } catch (Exception e) {
            System.out.println("❌ Connection Failed!");
            e.printStackTrace();
        }
    }
}