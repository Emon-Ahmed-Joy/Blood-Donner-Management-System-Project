package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the connection to the MySQL database.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/blood_donor_db";
    private static final String USER = "root";
    private static final String PASSWORD = "PassWord1443%"; // Updated with user provided password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please add it to the project libraries.", e);
        }
    }
}
