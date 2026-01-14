package MyGarage;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a financial expense related to a vehicle.
 * This class stores details such as cost, category, currency, and date.
 * It is used to generate expense reports and charts.
 */
public class Expense implements Serializable {
    
    // Enumeration defining standard categories for expenses.
    // This allows for consistent grouping in statistics and charts.
    public enum ExpenseCategory { 
        FUEL, MAINTENANCE, MODIFICATION, INSURANCE, TAX, FINE, ACCIDENT, PARKING, WASH_DETAIL, OTHER
    }

    private ExpenseCategory category;
    private double amount;
    private String currency; // Currency code (e.g., "TL", "USD", "EUR")
    private String description;
    private LocalDate date;

    /**
     * Constructor to create a new Expense record.
     * * @param category    The category of the expense (selected from Enum).
     * @param amount      The monetary value of the expense.
     * @param currency    The currency unit of the amount.
     * @param description A short text describing the expense (e.g., "Shell V-Power").
     * @param date        The date when the expense occurred.
     */
    public Expense(ExpenseCategory category, double amount, String currency, String description, LocalDate date) {
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.date = date;
    }
    
    // --- GETTERS ---

    public ExpenseCategory getCategory() { return category; }
    
    public double getAmount() { return amount; }
    
    public String getCurrency() { return currency; } 
    
    public String getDescription() { return description; }
    
    public LocalDate getDate() { return date; }
}