package MyGarage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * The Welcome/Login Screen of the application.
 * This is the entry point for the user interface. It handles user authentication
 * by checking if a username exists, or creating a new profile if it doesn't.
 */
public class WelcomeScreen {

    /**
     * Builds and displays the Welcome Screen UI.
     * @param stage The primary window (Stage) of the application.
     */
    public void show(Stage stage) {
        // VBox: A vertical layout container. Elements are stacked top-to-bottom.
        VBox root = new VBox(25); // 25px spacing between elements
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        
        // Fallback style in case CSS fails to load
        root.setStyle("-fx-background-color: #121212;"); 

        // --- UI ELEMENTS ---
        
        Label lblTitle = new Label("MY GARAGE 🏁");
        lblTitle.setFont(Font.font("Impact", 52));
        lblTitle.setTextFill(Color.WHITE);

        Label lblSub = new Label("Car Manager");
        lblSub.setFont(Font.font("Arial", 18));
        lblSub.setTextFill(Color.GRAY);

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter a name"); 
        txtUsername.setMaxWidth(320);
        
        // Linking to CSS: Assigning a class name "text-field" to apply styles later
        txtUsername.getStyleClass().add("text-field"); 
        txtUsername.setStyle("-fx-font-size: 16px; -fx-padding: 15;");

        Button btnStart = new Button("LET'S START 🚀");
        btnStart.setMaxWidth(320);
        // Linking to CSS: Giving the button the 'accent-button' look (Red color)
        btnStart.getStyleClass().add("accent-button"); 
        btnStart.setStyle("-fx-font-size: 18px; -fx-padding: 15;");

        // --- EVENTS (What happens when you click) ---
        
        // When button is clicked -> Run handleLogin
        btnStart.setOnAction(e -> handleLogin(stage, txtUsername.getText()));
        
        // When ENTER key is pressed inside the text box -> Run handleLogin
        txtUsername.setOnAction(e -> handleLogin(stage, txtUsername.getText()));

        // Add all elements to the VBox container
        root.getChildren().addAll(lblTitle, lblSub, new Region(), txtUsername, btnStart);

        // Create the Scene (The content inside the window)
        Scene scene = new Scene(root, 900, 650);
        
        // Load the CSS file for styling
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("⚠️ Warning: style.css not found.");
        }
        
        stage.setTitle("Welcome - MyGarage");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Logic to handle user login.
     * It functions as an MVP login system: 
     * 1. If user exists -> Log in.
     * 2. If user is new -> Create account automatically and Log in.
     */
    private void handleLogin(Stage stage, String username) {
        if (username.isEmpty()) return;

        ArrayList<User> allUsers = DataBaseManager.loadUsers();
        User activeUser = null;

        // Search database for the username
        for (User u : allUsers) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                activeUser = u;
                break;
            }
        }

        // Logic: If user not found, create a new one (Auto-Register)
        if (activeUser == null) {
            activeUser = new User(username, "", ""); 
            allUsers.add(activeUser);
            DataBaseManager.saveUsers(allUsers);
            System.out.println("🆕 New user created: " + username);
        } else {
            System.out.println("👋 Welcome back: " + username);
        }

        // Save session locally so the user stays logged in next time
        SessionManager.saveSession(activeUser.getUsername());
        
        // Switch screens: Close Welcome, Open Dashboard
        new MyGarageDashboard(activeUser).show(stage);
    }
}