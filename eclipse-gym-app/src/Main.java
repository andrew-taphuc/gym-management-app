import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Set system properties for better macOS compatibility
            System.setProperty("prism.lcdtext", "false");
            System.setProperty("prism.text", "t2k");

            primaryStage.setTitle("Gym Management System");

            // Ensure the stage is properly configured
            primaryStage.setResizable(true);
            primaryStage.setAlwaysOnTop(false);

            LoginView loginView = new LoginView(primaryStage);
            loginView.loadView("/view/login.fxml");

            // Force the application to front on macOS
            Platform.runLater(() -> {
                primaryStage.toFront();
                primaryStage.requestFocus();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Set macOS-specific properties before launching
        System.setProperty("javafx.macos.embedded", "false");
        System.setProperty("glass.accessible.force", "false");

        // Launch the JavaFX application
        launch(args);
    }
}