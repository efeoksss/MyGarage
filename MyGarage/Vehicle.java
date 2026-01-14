package MyGarage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a vehicle owned by the user.
 * This class acts as a central hub for all data related to a specific machine,
 * including its specifications, expense history, modification plans (Dream Spec),
 * and track day records.
 */
public class Vehicle implements Serializable {
    
    // REMOVED: VehicleType enum is no longer needed as the app focuses on Cars.

    private String id; // Unique identifier for the vehicle
    private String brand;
    private String model;
    private String generation; // e.g., "F30", "MK4"
    private String year;
    private String color;
    private int kilometer;
    private int power;  // Horsepower (HP)
    private int torque; // Newton-meter (Nm)

    // --- DATA LOGS (The history of the vehicle) ---
    private ArrayList<Expense> expenseLog;
    private ArrayList<DreamItem> dreamSpecLog;
    private ArrayList<TrackSession> trackLog; 

    /**
     * Constructor to initialize a new Vehicle.
     * Note: VehicleType parameter has been removed for simplicity.
     * @param brand      Manufacturer name (e.g., BMW)
     * @param model      Model name (e.g., 320i)
     * @param generation Chassis code or generation (e.g., F30)
     * @param year       Production year
     * @param color      Exterior color
     * @param kilometer  Current mileage
     * @param power      Engine power in HP
     * @param torque     Engine torque in Nm
     */
    public Vehicle(String brand, String model, String generation, String year, String color, int kilometer, int power, int torque) {
        this.brand = brand;
        this.model = model;
        this.generation = generation;
        this.year = year;
        this.color = color;
        this.kilometer = kilometer;
        this.power = power;
        this.torque = torque;
        
        // Initialize empty lists for logs to avoid NullPointerException
        this.expenseLog = new ArrayList<>();
        this.dreamSpecLog = new ArrayList<>();
        this.trackLog = new ArrayList<>(); 
        
        // Generate a simple unique ID based on the current timestamp
        this.id = System.currentTimeMillis() + "";
    }

    // --- UTILITY METHODS ---

    /**
     * Creates a formatted string for display in UI lists.
     * @return A string like "2016 BMW 320i (F30)"
     */
    public String getDisplayName() { 
        return year + " " + brand + " " + model + " (" + generation + ")"; 
    }

    // --- GETTERS & SETTERS ---

    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getGeneration() { return generation; }
    public String getYear() { return year; }
    public String getColor() { return color; }
    public int getKilometer() { return kilometer; }
    public int getPower() { return power; }
    public int getTorque() { return torque; }

    // Allows updating the mileage from the Dashboard
    public void setKilometer(int kilometer) { 
        this.kilometer = kilometer; 
    }

    // --- EXPENSE MANAGEMENT ---
    
    public void addExpense(Expense e) { 
        if(expenseLog == null) expenseLog = new ArrayList<>(); // Safety check
        expenseLog.add(e); 
    }
    
    public ArrayList<Expense> getExpenses() { 
        if(expenseLog == null) expenseLog = new ArrayList<>();
        return expenseLog; 
    }
    
    // --- DREAM SPEC (WISHLIST) MANAGEMENT ---
    
    public void addDreamItem(DreamItem item) { 
        if(dreamSpecLog == null) dreamSpecLog = new ArrayList<>();
        dreamSpecLog.add(item); 
    }
    
    public ArrayList<DreamItem> getDreamList() { 
        if(dreamSpecLog == null) dreamSpecLog = new ArrayList<>();
        return dreamSpecLog; 
    }

    // --- TRACK DAY MANAGEMENT ---
    
    public void addTrackSession(TrackSession session) {
        if(trackLog == null) trackLog = new ArrayList<>();
        trackLog.add(session);
    }
    
    public ArrayList<TrackSession> getTrackLog() {
        if(trackLog == null) trackLog = new ArrayList<>();
        return trackLog;
    }
}