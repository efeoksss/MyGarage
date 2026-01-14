package MyGarage;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The Main Entry Point of the "MyGarage" Application.
 * * This class handles:
 * 1. Initializing the JavaFX Runtime.
 * 2. Setting up the main application window (Stage) properties like Title and Icon.
 * 3. Managing the Auto-Login logic by checking the SessionManager.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Set the Application Title
        primaryStage.setTitle("MyGarage - Vehicle Manager 🏎️");

        // 2. Load Application Icon
        // This block tries to load the icon from the JAR resources first (Standard way).
        // If that fails, it tries to load from the external file system (Fallback).
        try {
            Image icon;
            // Attempt 1: Load from internal resources (Cleanest for GitHub/JAR)
            // Note: 'app_icon.png' must be in the same package/folder as Main.java
            if (getClass().getResource("app_icon.png") != null) {
                icon = new Image(getClass().getResourceAsStream("app_icon.png"));
            } 
            // Attempt 2: Load from external file (Backup for EXE/Testing)
            else {
                icon = new Image("file:app_icon.png");
            }
            
            // Apply the icon if loaded successfully
            if (!icon.isError()) {
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Icon could not be loaded: " + e.getMessage());
        }

        // 3. Auto-Login Logic
        // Check if a user session is already saved in the config file.
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            // Session found! Skip login screen and open Dashboard directly.
            System.out.println("✔ Auto-login successful for: " + currentUser.getUsername());
            new MyGarageDashboard(currentUser).show(primaryStage);
        } else {
            // No session found. Show the Welcome/Login Screen.
            new WelcomeScreen().show(primaryStage);
        }
    }

    /**
     * Main method to launch the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}