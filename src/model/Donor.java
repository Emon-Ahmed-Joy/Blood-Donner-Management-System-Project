package model;

public class Donor {
    private String name;
    private String email;
    private String password;
    private String bloodGroup;
    private String state;
    private String location;

    public Donor(String name, String email, String password, String bloodGroup, String state, String location) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.bloodGroup = bloodGroup;
        this.state = state;
        this.location = location;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getBloodGroup() { return bloodGroup; }
    public String getState() { return state; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return name + " (" + bloodGroup + ") - " + location + ", " + state;
    }
}
