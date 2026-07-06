package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * Data store for the application, backed by a MySQL database.
 * @author Naimur Rahman Durjoy
 */
public class DataStore {
    public static List<User> users = new ArrayList<>();
    public static List<Admin> admins = new ArrayList<>();
    public static List<Donor> donors = new ArrayList<>();
    public static List<BloodRequest> bloodRequests = new ArrayList<>();
    public static List<AuditLog> auditLogs = new ArrayList<>();

    public static User currentUser;
    public static String currentAdminId; // Track logged in admin

    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean checkPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) return false;
        String hashed = hashPassword(inputPassword);
        return storedPassword.equals(hashed) || storedPassword.equals(inputPassword);
    }

    static {
        initializeDatabase();
        loadDataFromDatabase();
    }

    /**
     * Ensures all required tables and columns exist in the database.
     */
    private static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // 1. Create audit_logs table if missing
            String createLogsTable = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "admin_id VARCHAR(50), " +
                                    "action TEXT, " +
                                    "target_email VARCHAR(100), " +
                                    "log_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                    ")";
            stmt.execute(createLogsTable);

            // 2. Ensure 'urgency' column exists in blood_requests
            try {
                stmt.execute("ALTER TABLE blood_requests ADD COLUMN urgency VARCHAR(20) DEFAULT 'Normal'");
            } catch (SQLException e) {
                // Column likely already exists, ignore
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadDataFromDatabase() {
        users.clear();
        admins.clear();
        donors.clear();
        bloodRequests.clear();
        auditLogs.clear();

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
                    req.setId(rs.getInt("id"));
                    req.setStatus(rs.getString("status"));
                    
                    // Handle potential missing urgency column gracefully if initialization failed
                    try {
                        req.setUrgency(rs.getString("urgency"));
                    } catch (SQLException ex) {
                        req.setUrgency("Normal");
                    }
                    
                    bloodRequests.add(req);
                }
            }

            // Load Audit Logs
            String logQuery = "SELECT * FROM audit_logs ORDER BY log_date DESC";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(logQuery)) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(rs.getString("admin_id"), rs.getString("action"), rs.getString("target_email"));
                    log.setId(rs.getInt("id"));
                    log.setLogDate(rs.getTimestamp("log_date"));
                    auditLogs.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            // Synchronize in-memory cache
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getEmail().equalsIgnoreCase(u.getEmail())) {
                    users.set(i, u);
                    break;
                }
            }
            // Sync donors list
            if (u instanceof Donor) {
                boolean found = false;
                for (int i = 0; i < donors.size(); i++) {
                    if (donors.get(i).getEmail().equalsIgnoreCase(u.getEmail())) {
                        donors.set(i, (Donor) u);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    donors.add((Donor) u);
                }
            } else {
                donors.removeIf(d -> d.getEmail().equalsIgnoreCase(u.getEmail()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addBloodRequest(BloodRequest req) {
        String query = "INSERT INTO blood_requests (requester_email, requester_name, donor_email, blood_group, patient_name, hospital_name, location, medical_condition, status, urgency) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, req.getRequesterEmail());
            pstmt.setString(2, req.getRequesterName());
            pstmt.setString(3, req.getDonorEmail());
            pstmt.setString(4, req.getBloodGroup());
            pstmt.setString(5, req.getPatientName());
            pstmt.setString(6, req.getHospitalName());
            pstmt.setString(7, req.getLocation());
            pstmt.setString(8, req.getMedicalCondition());
            pstmt.setString(9, req.getStatus());
            pstmt.setString(10, req.getUrgency());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    req.setId(generatedKeys.getInt(1));
                }
            }
            bloodRequests.add(req);
            notifyDonorOfRequest(req.getDonorEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateRequestStatus(BloodRequest req, String newStatus) {
        String simpleQuery = "UPDATE blood_requests SET status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(simpleQuery)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, req.getId());
            pstmt.executeUpdate();
            
            req.setStatus(newStatus);
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

    public static void deleteBloodRequest(BloodRequest req) {
        String query = "DELETE FROM blood_requests WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, req.getId());
            pstmt.executeUpdate();
            bloodRequests.remove(req);
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

    public static void addAuditLog(String action, String target) {
        String query = "INSERT INTO audit_logs (admin_id, action, target_email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            String adminId = currentAdminId == null ? "System" : currentAdminId;
            pstmt.setString(1, adminId);
            pstmt.setString(2, action);
            pstmt.setString(3, target);
            pstmt.executeUpdate();
            AuditLog log = new AuditLog(adminId, action, target);
            auditLogs.add(0, log); // Add to top for instant UI update
        } catch (SQLException e) {
            e.printStackTrace();
            // Critical error: Table probably missing even after attempted init
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Critical Logging Error: " + e.getMessage() + 
                "\n\nPlease ensure your database is updated.", 
                "Logging Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void deleteUser(User u) {
        String deleteRequestsQuery = "DELETE FROM blood_requests WHERE requester_email=? OR donor_email=?";
        String deleteUserQuery = "DELETE FROM users WHERE email=?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pstmt1 = conn.prepareStatement(deleteRequestsQuery)) {
                    pstmt1.setString(1, u.getEmail());
                    pstmt1.setString(2, u.getEmail());
                    pstmt1.executeUpdate();
                }
                try (PreparedStatement pstmt2 = conn.prepareStatement(deleteUserQuery)) {
                    pstmt2.setString(1, u.getEmail());
                    int rows = pstmt2.executeUpdate();
                    if (rows > 0) {
                        conn.commit();
                        
                        // LOG THE DELETION BEFORE REFRESHING CACHES
                        addAuditLog("Deleted User Account", u.getEmail());

                        users.removeIf(user -> user.getEmail().equalsIgnoreCase(u.getEmail()));
                        donors.removeIf(donor -> donor.getEmail().equalsIgnoreCase(u.getEmail()));
                        bloodRequests.removeIf(req -> req.getRequesterEmail().equalsIgnoreCase(u.getEmail()) || 
                                                     req.getDonorEmail().equalsIgnoreCase(u.getEmail()));
                        
                        javax.swing.JOptionPane.showMessageDialog(null, "User " + u.getName() + " deleted successfully.");
                    } else {
                        conn.rollback();
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
