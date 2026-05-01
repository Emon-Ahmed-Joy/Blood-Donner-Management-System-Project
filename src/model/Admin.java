package model;

/**
 * Represents an administrator.
 * @author Emon Ahmed Joy
 */
public class Admin {
    private String adminId;
    private String password;

    public Admin(String adminId, String password) {
        this.adminId = adminId;
        this.password = password;
    }

    public String getAdminId() { return adminId; }
    public String getPassword() { return password; }
}
