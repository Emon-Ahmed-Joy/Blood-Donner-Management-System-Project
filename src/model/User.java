package model;

/**
 * Base User class for all system users.
 * @author Emon Ahmed Joy
 */
public class User {
    protected String name;
    protected String email;
    protected String password;
    protected boolean isDonor;

    public User(String name, String email, String password, boolean isDonor) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isDonor = isDonor;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isDonor() { return isDonor; }
    public void setDonor(boolean donor) { isDonor = donor; }
}
