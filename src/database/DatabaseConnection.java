package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the connection to the MySQL database.
 * Credentials can be configured via environment variables:
 *   BLOOD_DB_URL, BLOOD_DB_USER, BLOOD_DB_PASSWORD
 */
public class DatabaseConnection {
    private static final String URL = getEnvOrDefault("BLOOD_DB_URL", "jdbc:mysql://localhost:3306/blood_donor_db");
    private static final String USER = getEnvOrDefault("BLOOD_DB_USER", "root");
    private static final String PASSWORD = getEnvOrDefault("BLOOD_DB_PASSWORD", "PassWord1443%");

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please add it to the project libraries.", e);
        }
    }
}
