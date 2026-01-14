package MyGarage;

import java.io.*;
import java.util.ArrayList;

public class DataBaseManager {

    // The name of the file where data will be stored locally.
    private static final String FILE_NAME = "mygarage_data.dat";

    /**
     * Saves the list of users to a binary file using serialization.
     * @param users The ArrayList of User objects to be saved.
     */
    public static void saveUsers(ArrayList<User> users) {
        // try-with-resources: Automatically closes the stream after operation
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users); // Convert object to bytes and write to file
            System.out.println("✅ Data saved successfully: " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("❌ Save Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of users from the binary file.
     * @return An ArrayList of User objects. Returns an empty list if file doesn't exist.
     */
    @SuppressWarnings("unchecked") // Suppresses the warning for casting Object to ArrayList
    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(FILE_NAME);

        // Check if the save file exists before trying to read
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // Read the object and cast it back to ArrayList<User>
                users = (ArrayList<User>) ois.readObject();
                System.out.println("✅ Data loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("❌ Load Error or Corrupted File: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ No save file found. Creating a new database.");
        }
        return users;
    }
}