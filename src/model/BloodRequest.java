package model;

import java.util.Date;

/**
 * Represents a blood request.
 * @author Emon Ahmed Joy
 */
public class BloodRequest {
    private int id; // Database primary key
    private String requesterEmail;
    private String requesterName;
    private String donorEmail;
    private String bloodGroup;
    private Date requestDate;
    private String status; // "Pending", "Accepted", "Declined"
    private String patientName;
    private String hospitalName;
    private String location;
    private String medicalCondition;

    // Constructor (same as before — id is set later from DB)
    public BloodRequest(String requesterEmail, String requesterName, String donorEmail, String bloodGroup,
                        String patientName, String hospitalName, String location, String medicalCondition) {
        this.requesterEmail = requesterEmail;
        this.requesterName = requesterName;
        this.donorEmail = donorEmail;
        this.bloodGroup = bloodGroup;
        this.patientName = patientName;
        this.hospitalName = hospitalName;
        this.location = location;
        this.medicalCondition = medicalCondition;
        this.requestDate = new Date();
        this.status = "Pending";
    }

    // ✅ NEW — id getter & setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Existing Getters
    public String getRequesterEmail() { return requesterEmail; }
    public String getRequesterName() { return requesterName; }
    public String getDonorEmail() { return donorEmail; }
    public String getBloodGroup() { return bloodGroup; }
    public Date getRequestDate() { return requestDate; }
    public String getStatus() { return status; }
    public String getPatientName() { return patientName; }
    public String getHospitalName() { return hospitalName; }
    public String getLocation() { return location; }
    public String getMedicalCondition() { return medicalCondition; }

    // Existing Setters
    public void setStatus(String status) { this.status = status; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public void setDonorEmail(String donorEmail) { this.donorEmail = donorEmail; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    public void setLocation(String location) { this.location = location; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    @Override
    public String toString() {
        return "[" + status + "] Request for " + bloodGroup + " at " + hospitalName;
    }

    public String toAdminString() {
        return "[" + status + "] " + requesterName + " -> " + donorEmail + " (" + bloodGroup + ")";
    }
}