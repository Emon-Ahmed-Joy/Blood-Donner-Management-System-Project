package model;

import java.util.Date;

/**
 * Represents an administrative action log.
 * @author Emon Ahmed Joy
 */
public class AuditLog {
    private int id;
    private String adminId;
    private String action;
    private String targetEmail;
    private Date logDate;

    public AuditLog(String adminId, String action, String targetEmail) {
        this.adminId = adminId;
        this.action = action;
        this.targetEmail = targetEmail;
        this.logDate = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAdminId() { return adminId; }
    public String getAction() { return action; }
    public String getTargetEmail() { return targetEmail; }
    public Date getLogDate() { return logDate; }

    public void setLogDate(Date logDate) { this.logDate = logDate; }

    @Override
    public String toString() {
        return "[" + logDate + "] " + adminId + ": " + action + " (Target: " + targetEmail + ")";
    }
}
