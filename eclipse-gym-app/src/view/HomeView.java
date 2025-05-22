package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;

public class HomeView extends BaseView {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    public HomeView(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            welcomeLabel.setText("Xin chào, " + currentUser.getFullName());
            roleLabel.setText("Vai trò: " + currentUser.getRole().getValue());
        }
    }

    @FXML
    private void handleProfile() {
        ProfileView profileView = new ProfileView(stage);
        profileView.setCurrentUser(currentUser);
        profileView.loadView("/view/profile.fxml");
    }

    @FXML
    private void handleLogout() {
        currentUser = null;
        LoginView loginView = new LoginView(stage);
        loginView.loadView("/view/login.fxml");
    }
}