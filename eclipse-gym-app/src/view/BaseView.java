package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.application.Platform;
import model.User;

public class BaseView {
    protected Stage stage;
    protected User currentUser;
    protected static final double WINDOW_WIDTH = 800;
    protected static final double WINDOW_HEIGHT = 600;

    public BaseView(Stage stage) {
        this.stage = stage;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(this);
            Parent root = loader.load();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);

            // Center the window on screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - WINDOW_WIDTH) / 2);
            stage.setY((screenBounds.getHeight() - WINDOW_HEIGHT) / 2);

            // Additional fixes for macOS visibility
            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();
            
            // Ensure the stage is shown
            Platform.runLater(() -> {
                stage.show();
                stage.toFront();
                stage.requestFocus();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // New method to navigate to a different view with a new controller
    protected void navigateToView(String fxmlPath, BaseView newController) {
        try {
            // Transfer current user to new controller
            newController.setCurrentUser(this.currentUser);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(newController);
            Parent root = loader.load();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setScene(scene);

            // Center the window on screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - WINDOW_WIDTH) / 2);
            stage.setY((screenBounds.getHeight() - WINDOW_HEIGHT) / 2);

            stage.setResizable(true);
            stage.toFront();
            stage.requestFocus();
            
            Platform.runLater(() -> {
                stage.show();
                stage.toFront();
                stage.requestFocus();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showError(String message) {
        // TODO: Implement error dialog
        System.out.println("Error: " + message);
    }

    protected void showSuccess(String message) {
        // TODO: Implement success dialog
        System.out.println("Success: " + message);
    }
}