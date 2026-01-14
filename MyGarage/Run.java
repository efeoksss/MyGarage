package MyGarage;

/**
 * The Launcher class for the application.
 * * This class serves as a workaround entry point to avoid 
 * "JavaFX runtime components are missing" errors when building and running 
 * the application as a JAR file. It simply delegates execution to the Main class.
 */
public class Run {
    
    public static void main(String[] args) {
        // Redirects the startup process to the actual JavaFX Main class.
        Main.main(args); 
    }
}