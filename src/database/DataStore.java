package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import ui.*;

/**
 * Data store for the application, backed by a MySQL database.
 * @author Naimur Rahman Durjoy
 */
public class DataStore {
    public static List<User> users = new ArrayList<>();
    public static List<Admin> admins = new ArrayList<>();
    public static List<Donor> donors = new ArrayList<>();
    public static List<User> deletedUsers = new ArrayList<>();
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
        return java.security.MessageDigest.isEqual(storedPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8), 
                                                 hashed.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public static String safe(String s) {
        return s != null ? s : "";
    }

    static {
        initializeDatabase();
        loadDataFromDatabase();
    }

    private static boolean dbConnectionAlertShown = false;

    private static void showDbError(Exception e) {
        if (!dbConnectionAlertShown) {
            dbConnectionAlertShown = true;
            javax.swing.JOptionPane.showMessageDialog(null,
                "Database Connection Error: " + e.getMessage() +
                "\n\nPlease ensure MySQL is running on port 3306 and settings in DatabaseConnection.java match your database setup.",
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ensures all required tables and columns exist in the database.
     */
    private static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // 1. Create users table if missing
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                      "email VARCHAR(100) PRIMARY KEY, " +
                                      "name VARCHAR(100) NOT NULL, " +
                                      "password VARCHAR(100) NOT NULL, " +
                                      "state VARCHAR(100), " +
                                      "location VARCHAR(100), " +
                                      "is_donor BOOLEAN DEFAULT FALSE, " +
                                      "is_blocked BOOLEAN DEFAULT FALSE, " +
                                      "has_update BOOLEAN DEFAULT FALSE, " +
                                      "blood_group VARCHAR(5), " +
                                      "medical_condition TEXT, " +
                                      "is_available BOOLEAN DEFAULT TRUE, " +
                                      "last_donation_date DATE, " +
                                      "is_deleted BOOLEAN DEFAULT FALSE, " +
                                      "deleted_at TIMESTAMP NULL DEFAULT NULL" +
                                      ")";
            stmt.execute(createUsersTable);

            // 2. Create admins table if missing
            String createAdminsTable = "CREATE TABLE IF NOT EXISTS admins (" +
                                       "admin_id VARCHAR(50) PRIMARY KEY, " +
                                       "password VARCHAR(100) NOT NULL" +
                                       ")";
            stmt.execute(createAdminsTable);

            // 3. Create blood_requests table if missing
            String createRequestsTable = "CREATE TABLE IF NOT EXISTS blood_requests (" +
                                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                         "requester_email VARCHAR(100), " +
                                         "requester_name VARCHAR(100), " +
                                         "donor_email VARCHAR(100), " +
                                         "blood_group VARCHAR(5), " +
                                         "patient_name VARCHAR(100), " +
                                         "hospital_name VARCHAR(100), " +
                                         "location VARCHAR(100), " +
                                         "medical_condition TEXT, " +
                                         "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                         "status VARCHAR(20) DEFAULT 'Pending', " +
                                         "urgency VARCHAR(20) DEFAULT 'Normal', " +
                                         "FOREIGN KEY (requester_email) REFERENCES users(email) ON DELETE CASCADE, " +
                                         "FOREIGN KEY (donor_email) REFERENCES users(email) ON DELETE CASCADE" +
                                         ")";
            stmt.execute(createRequestsTable);

            // 4. Create audit_logs table if missing
            String createLogsTable = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                     "admin_id VARCHAR(50), " +
                                     "action TEXT, " +
                                     "target_email VARCHAR(100), " +
                                     "log_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                     ")";
            stmt.execute(createLogsTable);

            // 5. Ensure 'urgency' column exists in blood_requests
            try {
                stmt.execute("ALTER TABLE blood_requests ADD COLUMN urgency VARCHAR(20) DEFAULT 'Normal'");
            } catch (SQLException e) {
                // Ignore if it's "Duplicate column name" (MySQL error code 1060)
                if (e.getErrorCode() != 1060) {
                    throw e;
                }
            }

            // Ensure is_deleted, deleted_at, and last_donation_date columns exist in users
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN last_donation_date DATE");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) throw e;
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) throw e;
            }
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1060) throw e;
            }

            // 6. Insert default admin if table is empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM admins")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String hashedDefaultPass = hashPassword("admin123");
                    try (PreparedStatement seedStmt = conn.prepareStatement("INSERT INTO admins (admin_id, password) VALUES ('admin', ?)")) {
                        seedStmt.setString(1, hashedDefaultPass);
                        seedStmt.executeUpdate();
                    }
                }
            }
            
            // 7. Hash any existing plain-text passwords in database (e.g. from schema.sql)
            stmt.execute("UPDATE users SET password = SHA2(password, 256) WHERE LENGTH(password) != 64");
            stmt.execute("UPDATE admins SET password = SHA2(password, 256) WHERE LENGTH(password) != 64");
        } catch (SQLException e) {
            e.printStackTrace();
            showDbError(e);
        }
    }

    public static void loadDataFromDatabase() {
        users.clear();
        admins.clear();
        donors.clear();
        deletedUsers.clear();
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
            String userQuery = "SELECT * FROM users WHERE is_deleted = FALSE";
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
                    Date lastDonationDate = rs.getDate("last_donation_date");

                    if (isDonor) {
                        Donor d = new Donor(name, email, password, rs.getString("blood_group"), state, location, rs.getString("medical_condition"));
                        d.setAvailable(rs.getBoolean("is_available"));
                        d.setBlocked(isBlocked);
                        d.setHasUpdate(hasUpdate);
                        if (lastDonationDate != null) d.setLastDonationDate(lastDonationDate.toLocalDate());
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

            // Load Deleted Users
            String deletedUserQuery = "SELECT * FROM users WHERE is_deleted = TRUE";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(deletedUserQuery)) {
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
                    Date lastDonationDate = rs.getDate("last_donation_date");
                    java.sql.Timestamp deletedAt = rs.getTimestamp("deleted_at");

                    if (isDonor) {
                        Donor d = new Donor(name, email, password, rs.getString("blood_group"), state, location, rs.getString("medical_condition"));
                        d.setAvailable(rs.getBoolean("is_available"));
                        d.setBlocked(isBlocked);
                        d.setHasUpdate(hasUpdate);
                        d.setDeleted(true);
                        d.setDeletedAt(deletedAt);
                        if (lastDonationDate != null) d.setLastDonationDate(lastDonationDate.toLocalDate());
                        u = d;
                    } else {
                        u = new User(name, email, password, state, location, false);
                        u.setBlocked(isBlocked);
                        u.setHasUpdate(hasUpdate);
                        u.setDeleted(true);
                        u.setDeletedAt(deletedAt);
                    }
                    deletedUsers.add(u);
                }
            }

            // Load Blood Requests (filtering out soft-deleted requesters or donors)
            String requestQuery = "SELECT r.* FROM blood_requests r " +
                                  "LEFT JOIN users req ON r.requester_email = req.email " +
                                  "LEFT JOIN users don ON r.donor_email = don.email " +
                                  "WHERE (req.is_deleted = FALSE OR req.is_deleted IS NULL) " +
                                  "  AND (don.is_deleted = FALSE OR don.is_deleted IS NULL)";
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
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    
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
            showDbError(e);
        }
    }

    public static void addUser(User u) {
        String query = "INSERT INTO users (email, name, password, state, location, is_donor, blood_group, medical_condition, is_available, last_donation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                pstmt.setDate(10, d.getLastDonationDate() != null ? java.sql.Date.valueOf(d.getLastDonationDate()) : null);
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setBoolean(9, false);
                pstmt.setNull(10, java.sql.Types.DATE);
            }
            
            pstmt.executeUpdate();
            users.add(u);
            if (u instanceof Donor) donors.add((Donor) u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(User u) {
        String query = "UPDATE users SET name=?, password=?, state=?, location=?, is_donor=?, is_blocked=?, has_update=?, blood_group=?, medical_condition=?, is_available=?, last_donation_date=? WHERE email=?";
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
                pstmt.setDate(11, d.getLastDonationDate() != null ? java.sql.Date.valueOf(d.getLastDonationDate()) : null);
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
                pstmt.setBoolean(10, false);
                pstmt.setNull(11, java.sql.Types.DATE);
            }
            pstmt.setString(12, u.getEmail());
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
            
            // If request completed, update donor's last donation date and mark unavailable
            if ("Completed".equalsIgnoreCase(newStatus) && req.getDonorEmail() != null) {
                for (Donor d : donors) {
                    if (d.getEmail().equalsIgnoreCase(req.getDonorEmail())) {
                        d.setLastDonationDate(java.time.LocalDate.now());
                        d.setAvailable(false);
                        updateUser(d);
                        break;
                    }
                }
            }
            
            for (User u : users) {
                if (u.getEmail().equalsIgnoreCase(req.getRequesterEmail())) {
                    u.setHasUpdate(true);
                    updateUser(u);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEligible(Donor d) {
        if (d.getLastDonationDate() == null) return true;
        return d.getLastDonationDate().plusDays(90).isBefore(java.time.LocalDate.now());
    }

    public static void clearDonationCooldown(Donor d) {
        String query = "UPDATE users SET last_donation_date = NULL WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, d.getEmail());
            pstmt.executeUpdate();
            d.setLastDonationDate(null);
            d.setAvailable(true);
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
            if (u.getEmail().equalsIgnoreCase(donorEmail)) {
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
        String query = "UPDATE users SET is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, u.getEmail());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // LOG THE DELETION BEFORE REFRESHING CACHES
                addAuditLog("Deleted User Account", u.getEmail());

                // If this user is currently logged in, log them out immediately
                if (currentUser != null && currentUser.getEmail().equalsIgnoreCase(u.getEmail())) {
                    currentUser = null;
                    for (java.awt.Window window : java.awt.Window.getWindows()) {
                        if (window instanceof UserHomePage || window instanceof DonorProfilePage || window instanceof UserSearchPage) {
                            window.dispose();
                        }
                    }
                    new LoginPage().setVisible(true);
                }

                loadDataFromDatabase();
                javax.swing.JOptionPane.showMessageDialog(null, "User " + u.getName() + " moved to Recycle Bin.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void restoreUser(User u) {
        String query = "UPDATE users SET is_deleted = FALSE, deleted_at = NULL WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, u.getEmail());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                addAuditLog("Restored User Account", u.getEmail());
                loadDataFromDatabase();
                javax.swing.JOptionPane.showMessageDialog(null, "User " + u.getName() + " restored successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearAuditLogs(String adminId) {
        // First, log the clearing action with the admin's ID
        String oldAdminId = currentAdminId;
        currentAdminId = adminId;
        
        String query = "DELETE FROM audit_logs";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            auditLogs.clear();
            
            // Log the clearing action (this adds a new entry after deletion)
            addAuditLog("Cleared all audit logs", "System");
            
            javax.swing.JOptionPane.showMessageDialog(null,
                "All audit logs have been cleared successfully.",
                "Logs Cleared", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                "Failed to clear audit logs: " + e.getMessage(),
                "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } finally {
            currentAdminId = oldAdminId;
        }
    }

    public static void cleanupDeletedUsers() {
        String query = "DELETE FROM users WHERE is_deleted = TRUE AND deleted_at < DATE_SUB(NOW(), INTERVAL 30 DAY)";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            int count = stmt.executeUpdate(query);
            if (count > 0) {
                System.out.println("Cleaned up " + count + " expired soft-deleted user records.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
