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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return adminId != null ? adminId.equalsIgnoreCase(admin.adminId) : admin.adminId == null;
    }

    @Override
    public int hashCode() {
        return adminId != null ? adminId.toLowerCase().hashCode() : 0;
    }
}
