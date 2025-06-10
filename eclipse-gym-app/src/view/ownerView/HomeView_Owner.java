package view.ownerView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import model.enums.enum_Role;
import view.BaseView;
import view.LoginView;
import view.ProfileView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class HomeView_Owner extends BaseView {
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

    public HomeView_Owner(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            roleLabel.setText("Role: " + currentUser.getRole());
        }
        // Load home content by default
        handleDashboardClick();
    }

    // @FXML
    // private void handleHomeClick() {
    //     try {
    //         Parent homeContent = FXMLLoader.load(getClass().getResource("home_content.fxml"));
    //         contentArea.getChildren().clear();
    //         contentArea.getChildren().add(homeContent);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

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
    private void handleDashboardClick() {
        view.ownerView.DashboardController controller = new view.ownerView.DashboardController();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("dashboard.fxml", controller);
    }

    @FXML
    private void handleEquipmentClick() {
        view.ownerView.EquipmentView controller = new view.ownerView.EquipmentView();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("equipment.fxml", controller);
    }

    @FXML
    private void handlePlansClick() {
        view.ownerView.PlansRenewalsController controller = new view.ownerView.PlansRenewalsController();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("plans_renewals.fxml", controller);
    }

    @FXML
    private void handlePromosClick() {
        view.adminView.PromosView controller = new view.adminView.PromosView();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("/view/adminView/promos.fxml", controller);
    }

    @FXML
    private void handleFeedbackClick() {
        view.adminView.FeedbackView controller = new view.adminView.FeedbackView();
        controller.setCurrentUser(currentUser);
        loadViewWithUser("/view/adminView/feedback.fxml", controller);
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

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}