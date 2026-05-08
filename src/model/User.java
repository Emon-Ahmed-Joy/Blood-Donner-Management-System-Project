package model;

/**
 * Base User class for all system users.
 * @author Emon Ahmed Joy
 */
public class User {
    protected String name;
    protected String email;
    protected String password;
    protected String state;
    protected String location;
    protected boolean isDonor;
    protected boolean isBlocked;
    protected boolean hasUpdate; // Notification flag

    public User(String name, String email, String password, String state, String location, boolean isDonor) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.state = (state != null) ? state : "N/A";
        this.location = (location != null) ? location : "N/A";
        this.isDonor = isDonor;
        this.isBlocked = false;
        this.hasUpdate = false;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getState() { return state; }
    public String getLocation() { return location; }
    public boolean isDonor() { return isDonor; }
    public boolean isBlocked() { return isBlocked; }
    public boolean hasUpdate() { return hasUpdate; }
    
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setState(String state) { this.state = state; }
    public void setLocation(String location) { this.location = location; }
    public void setDonor(boolean donor) { isDonor = donor; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
    public void setHasUpdate(boolean hasUpdate) { this.hasUpdate = hasUpdate; }
}
