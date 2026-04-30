package model;

import java.util.Date;

/**
 * Represents a blood request with detailed patient and hospital information.
 */
public class BloodRequest {
    private String requesterEmail;
    private String requesterName;
    private String donorEmail;
    private String bloodGroup;
    private Date requestDate;
    private String status; // "Pending", "Accepted", "Declined"
    
    // New Detailed Fields
    private String patientName;
    private String hospitalName;
    private String location;
    private String medicalCondition;

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

    // Getters
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

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + status + "] Request for " + bloodGroup + " at " + hospitalName;
    }
    
    public String toAdminString() {
        return "[" + status + "] " + requesterName + " -> " + donorEmail + " (" + bloodGroup + ")";
    }
}
