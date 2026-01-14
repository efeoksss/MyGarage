package MyGarage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a registered user of the application.
 * This class serves as the root of the data hierarchy, holding personal details
 * and a list of vehicles (the garage).
 */
public class User implements Serializable {
    
    private String username;
    private String password; // Stored as plain text for this MVP; encryption can be added later.
    private String email;    // Optional contact information.
    
    // THE CORE RELATIONSHIP: A user can own multiple vehicles.
    private ArrayList<Vehicle> myGarage; 

    /**
     * Constructor to create a new User.
     * Initializes the vehicle list (garage) as empty.
     * @param username The unique login name.
     * @param password The login password.
     * @param email    The user's email address.
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.myGarage = new ArrayList<>(); // Garage is open for business!
    }

    /**
     * Adds a new vehicle object to the user's personal garage list.
     * @param v The Vehicle object to add.
     */
    public void addVehicleToGarage(Vehicle v) {
        myGarage.add(v);
    }
    
    /**
     * Removes a vehicle from the garage.
     * @param v The Vehicle object to remove.
     */
    public void removeVehicle(Vehicle v) {
        myGarage.remove(v);
    }

    /**
     * Retrieves the list of all vehicles owned by the user.
     * @return An ArrayList containing Vehicle objects.
     */
    public ArrayList<Vehicle> getGarage() {
        return myGarage;
    }

    // --- GETTERS ---
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}