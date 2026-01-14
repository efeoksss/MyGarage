package MyGarage;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a specific item or modification in the user's wishlist (Dream Spec).
 * This class stores details about the planned modification, including cost, currency, and status.
 */
public class DreamItem implements Serializable {
    
    // Enumeration for predefined modification categories.
    // This helps in organizing and filtering modifications.
    public enum DreamCategory { 
        WHEELS, TIRES, SPOILER, WRAP_PAINT, EXTERIOR_TRIM, 
        INTERIOR_TRIM, LIGHTS, SUSPENSION, PERFORMANCE, AUDIO_SYSTEM, OTHER
    }

    private DreamCategory category;
    private String description;
    private double estimatedCost;
    private String currency; // Stores currency type (e.g., "TL", "USD", "EUR")
    private LocalDate plannedDate;
    private boolean isDone; // Tracks if the modification has been completed

    /**
     * Constructor to initialize a new DreamItem.
     * By default, the item is marked as incomplete (isDone = false).
     * * @param category The category of the modification.
     * @param description Details about the part or brand.
     * @param estimatedCost The projected cost.
     * @param currency The currency unit for the cost.
     * @param plannedDate The target date for the modification.
     */
    public DreamItem(DreamCategory category, String description, double estimatedCost, String currency, LocalDate plannedDate) {
        this.category = category;
        this.description = description;
        this.estimatedCost = estimatedCost;
        this.currency = currency;
        this.plannedDate = plannedDate;
        this.isDone = false; // Default status is "not done"
    }

    // --- GETTERS & SETTERS ---

    public DreamCategory getCategory() { return category; }
    
    public String getDescription() { return description; }
    
    public double getEstimatedCost() { return estimatedCost; }
    
    public String getCurrency() { return currency; } 
    
    public LocalDate getPlannedDate() { return plannedDate; }
    
    // Returns the completion status (used for the checkbox in the UI)
    public boolean isDone() { return isDone; }
    
    // Updates the completion status when the user checks/unchecks the box
    public void setDone(boolean done) { isDone = done; }
}