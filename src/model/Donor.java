package model;

/**
 * Represents a donor, extending the basic User.
 * @author Emon Ahmed Joy
 */
public class Donor extends User {
    private String bloodGroup;
    private String state;
    private String location;
    private boolean isAvailable;

    public Donor(String name, String email, String password, String bloodGroup, String state, String location) {
        super(name, email, password, true);
        this.bloodGroup = bloodGroup;
        this.state = state;
        this.location = location;
        this.isAvailable = true;
    }

    public String getBloodGroup() { return bloodGroup; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return name + " [" + bloodGroup + "] - " + location + ", " + state + (isAvailable ? " (Available)" : " (Busy)");
    }
}
