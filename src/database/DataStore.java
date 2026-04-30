package database;

import model.Donor;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    public static List<Donor> donors = new ArrayList<>();

    static {
        // Sample data
        donors.add(new Donor("John Doe", "john@example.com", "pass123", "A+", "California", "Los Angeles"));
        donors.add(new Donor("Jane Smith", "jane@example.com", "pass456", "O-", "New York", "Brooklyn"));
    }
}
