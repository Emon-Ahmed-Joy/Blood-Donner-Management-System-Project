package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * Data store for the application, backed by a MySQL database.
 * @author Emon Ahmed Joy
 */
public class DataStore {
    public static List<User> users = new ArrayList<>();
    public static List<Admin> admins = new ArrayList<>();
    public static List<Donor> donors = new ArrayList<>();
    public static List<BloodRequest> bloodRequests = new ArrayList<>();

    public static User currentUser;

    static {
        loadDataFromDatabase();
    }

    public static void loadDataFromDatabase() {
        users.clear();
        admins.clear();
        donors.clear();
        bloodRequests.clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load Admins
            String adminQuery = "SELECT * FROM admins";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(adminQuery)) {
                while (rs.next()) {
                    admins.add(new Admin(rs.getString("admin_id"), rs.getString("password")));
                }
            }

            // Load Users and Donors
            String userQuery = "SELECT * FROM users";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(userQuery)) {
                while (rs.next()) {
                    User u;
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String state = rs.getString("state");
                    String location = rs.getString("location");
                    boolean isDonor = rs.getBoolean("is_donor");
                    boolean isBlocked = rs.getBoolean("is_blocked");
                    boolean hasUpdate = rs.getBoolean("has_update");

                    if (isDonor) {
                        Donor d = new Donor(name, email, password, rs.getString("blood_group"), state, location, rs.getString("medical_condition"));
                        d.setAvailable(rs.getBoolean("is_available"));
                        d.setBlocked(isBlocked);
                        d.setHasUpdate(hasUpdate);
                        u = d;
                        donors.add(d);
                    } else {
                        u = new User(name, email, password, state, location, false);
                        u.setBlocked(isBlocked);
                        u.setHasUpdate(hasUpdate);
                    }
                    users.add(u);
                }
            }

            // Load Blood Requests
            String requestQuery = "SELECT * FROM blood_requests";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(requestQuery)) {
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
                    req.setStatus(rs.getString("status"));
                    bloodRequests.add(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Database Connection Error: " + e.getMessage() + 
                "\n\n1. Ensure MySQL is running." +
                "\n2. Ensure 'blood_donor_db' exists (Run schema.sql)." +
                "\n3. Check credentials in DatabaseConnection.java." +
                "\n4. Ensure MySQL Connector/J is added to project libraries.",
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void addUser(User u) {
        String query = "INSERT INTO users (email, name, password, state, location, is_donor, blood_group, medical_condition, is_available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, u.getEmail());
            pstmt.setString(2, u.getName());
            pstmt.setString(3, u.getPassword());
            pstmt.setString(4, u.getState());
            pstmt.setString(5, u.getLocation());
            pstmt.setBoolean(6, u.isDonor());
            
            if (u instanceof Donor) {
                Donor d = (Donor) u;
                pstmt.setString(7, d.getBloodGroup());
                pstmt.setString(8, d.getMedicalCondition());
                pstmt.setBoolean(9, d.isAvailable());
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setBoolean(9, false);
            }
            
            pstmt.executeUpdate();
            users.add(u);
            if (u instanceof Donor) donors.add((Donor) u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(User u) {
        String query = "UPDATE users SET name=?, password=?, state=?, location=?, is_donor=?, is_blocked=?, has_update=?, blood_group=?, medical_condition=?, is_available=? WHERE email=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, u.getName());
            pstmt.setString(2, u.getPassword());
            pstmt.setString(3, u.getState());
            pstmt.setString(4, u.getLocation());
            pstmt.setBoolean(5, u.isDonor());
            pstmt.setBoolean(6, u.isBlocked());
            pstmt.setBoolean(7, u.hasUpdate());
            
            if (u instanceof Donor) {
                Donor d = (Donor) u;
                pstmt.setString(8, d.getBloodGroup());
                pstmt.setString(9, d.getMedicalCondition());
                pstmt.setBoolean(10, d.isAvailable());
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
                pstmt.setBoolean(10, false);
            }
            pstmt.setString(11, u.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addBloodRequest(BloodRequest req) {
        String query = "INSERT INTO blood_requests (requester_email, requester_name, donor_email, blood_group, patient_name, hospital_name, location, medical_condition, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, req.getRequesterEmail());
            pstmt.setString(2, req.getRequesterName());
            pstmt.setString(3, req.getDonorEmail());
            pstmt.setString(4, req.getBloodGroup());
            pstmt.setString(5, req.getPatientName());
            pstmt.setString(6, req.getHospitalName());
            pstmt.setString(7, req.getLocation());
            pstmt.setString(8, req.getMedicalCondition());
            pstmt.setString(9, req.getStatus());
            pstmt.executeUpdate();
            bloodRequests.add(req);
            
            // Notify donor
            notifyDonorOfRequest(req.getDonorEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateRequestStatus(BloodRequest req, String newStatus) {
        String simpleQuery = "UPDATE blood_requests SET status=? WHERE requester_email=? AND donor_email=? AND status='Pending'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(simpleQuery)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, req.getRequesterEmail());
            pstmt.setString(3, req.getDonorEmail());
            pstmt.executeUpdate();
            
            req.setStatus(newStatus);
            // Notify user
            for (User u : users) {
                if (u.getEmail().equals(req.getRequesterEmail())) {
                    u.setHasUpdate(true);
                    updateUser(u);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void notifyDonorOfRequest(String donorEmail) {
        for (User u : users) {
            if (u.getEmail().equals(donorEmail)) {
                u.setHasUpdate(true);
                updateUser(u);
                break;
            }
        }
    }

    public static void deleteUser(User u) {
        String query = "DELETE FROM users WHERE email=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, u.getEmail());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Remove from local cache using email to ensure match
                users.removeIf(user -> user.getEmail().equals(u.getEmail()));
                donors.removeIf(donor -> donor.getEmail().equals(u.getEmail()));
                javax.swing.JOptionPane.showMessageDialog(null, "User " + u.getName() + " has been permanently deleted.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Failed to delete user: User not found in database.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Database Error during deletion: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
