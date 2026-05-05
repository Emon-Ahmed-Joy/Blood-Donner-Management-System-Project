package model;

/**
 * Represents a donor, extending the basic User.
 * @author Emon Ahmed Joy
 */
public class Donor extends User {
    private String bloodGroup;
    private String medicalCondition;
    private boolean isAvailable;

    public Donor(String name, String email, String password, String bloodGroup, String state, String location, String medicalCondition) {
        super(name, email, password, state, location, true);
        this.bloodGroup = bloodGroup;
        this.medicalCondition = medicalCondition;
        this.isAvailable = true;
    }

    public String getBloodGroup() { return bloodGroup; }
    public String getMedicalCondition() { return medicalCondition; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }

    @Override
    public String toString() {
        return name + " [" + bloodGroup + "] - " + location + ", " + state + (isAvailable ? " (Available)" : " (Busy)");
    }
}
