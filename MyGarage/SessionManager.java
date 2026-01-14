package MyGarage;

import java.io.*;
import java.util.ArrayList;

/**
 * Manages user sessions for auto-login functionality.
 * It saves the username of the logged-in user to a local text file
 * and retrieves it when the application starts to bypass the login screen.
 */
public class SessionManager {
    
    private static final String SESSION_FILE = "session_config.txt";

    /**
     * Saves the username to a local file to remember the session.
     * This overload accepts a direct String (used by WelcomeScreen).
     * * @param username The username string to save.
     */
    public static void saveSession(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(username);
        } catch (IOException e) {
            System.out.println("Could not save session: " + e.getMessage());
        }
    }

    /**
     * Overloaded method to save session using a User object.
     * Extracts the username and calls the main saveSession method.
     * * @param user The User object to remember.
     */
    public static void saveSession(User user) {
        saveSession(user.getUsername());
    }

    /**
     * Checks for a valid session file and retrieves the logged-in User.
     * Reads the username from the file and matches it against the database.
     * * @return The User object if a valid session exists, otherwise null.
     */
    public static User getCurrentUser() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String username = reader.readLine();
            if (username != null && !username.isEmpty()) {
                // Username found in file, now fetch the full User object from DB
                ArrayList<User> users = DataBaseManager.loadUsers();
                for (User u : users) {
                    if (u.getUsername().equals(username)) {
                        return u;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read session: " + e.getMessage());
        }
        return null;
    }

    /**
     * Clears the saved session file (Logout).
     * This forces the user to log in again next time.
     */
    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}