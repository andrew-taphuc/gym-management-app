package view.userView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import model.enums.enum_Role;
import view.BaseView;
import view.LoginView;
import view.ProfileView;

import java.io.IOException;

public class HomeView extends BaseView {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private StackPane contentArea;

    public HomeView() {
        super(new Stage()); // or pass an existing Stage if available
        // Initialize as needed
    }

    public HomeView(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            roleLabel.setText("Role: " + currentUser.getRole());

        }
        // Load home content by default
        handleHomeClick();
    }

    @FXML
    private void handleHomeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home_content.fxml"));
            HomeContent homeContent = new HomeContent();
            homeContent.setCurrentUser(currentUser);
            loader.setController(homeContent);
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void loadViewWithUser(String fxmlPath, T controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleWorkoutsClick() {
        view.userView.WorkoutsController controller = new view.userView.WorkoutsController();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("workouts.fxml", controller);
    }

    @FXML
    private void handlePlansClick() {
        view.userView.PlansRenewalsController controller = new view.userView.PlansRenewalsController();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("plans_renewals.fxml", controller);
    }

    @FXML
    private void handlePromosClick() {
        view.userView.PromosView controller = new view.userView.PromosView();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("promos.fxml", controller);
    }

    @FXML
    private void handleFeedbackClick() {
        view.userView.FeedbackView controller = new view.userView.FeedbackView();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("feedback.fxml", controller);
    }

    @FXML
    private void handleLogout() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");
        alert.getDialogPane().getStylesheets().clear();
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/view/dialog.css").toExternalForm());
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            currentUser = null;
            view.LoginView loginView = new view.LoginView(stage);
            loginView.loadView("/view/login.fxml");
        }
    }

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
            view.ProfileView profileController = new view.ProfileView();
            profileController.setCurrentUser(currentUser);
            loader.setController(profileController);
            Parent profileView = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(profileView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}