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
    public Date getLogDate() { 
        return logDate != null ? new Date(logDate.getTime()) : null; 
    }

    public void setLogDate(Date logDate) { 
        this.logDate = logDate != null ? new Date(logDate.getTime()) : null; 
    }

    @Override
    public String toString() {
        return "[" + logDate + "] " + adminId + ": " + action + " (Target: " + targetEmail + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return id == auditLog.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
