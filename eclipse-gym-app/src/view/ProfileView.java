package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;
import controller.UserController;

public class ProfileView extends BaseView {
    @FXML
    private Label userIdLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label statusLabel;

    private UserController userController;

    public ProfileView(Stage stage) {
        super(stage);
        this.userController = new UserController();
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            User user = userController.getUserByID(currentUser.getUserId());
            if (user != null) {
                userIdLabel.setText(String.valueOf(user.getUserId()));
                usernameLabel.setText(user.getUsername());
                emailLabel.setText(user.getEmail());
                phoneLabel.setText(user.getPhoneNumber());
                fullNameLabel.setText(user.getFullName());
                dobLabel.setText(user.getDateOfBirth().toString());
                genderLabel.setText(user.getGender().getValue());
                addressLabel.setText(user.getAddress());
                roleLabel.setText(user.getRole().getValue());
                statusLabel.setText(user.getStatus().getValue());
            }
        }
    }

    @FXML
    private void handleBack() {
        HomeView homeView = new HomeView(stage);
        homeView.setCurrentUser(currentUser);
        homeView.loadView("/view/home.fxml");
    }
}