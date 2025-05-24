package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import view.userView.HomeView;
import controller.UserController;
import java.time.format.DateTimeFormatter;

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
    @FXML
    private Label errorLabel;

    private UserController userController;

    public ProfileView() {
        super(null);
        userController = new UserController();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("currentUser: " + currentUser);
        if (currentUser != null) {
            // Lấy thông tin mới nhất của user
            User user = userController.getUserByID(currentUser.getUserId());
            if (user != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                userIdLabel.setText(String.valueOf(user.getUserId()));
                usernameLabel.setText(user.getUsername());
                emailLabel.setText(user.getEmail());
                phoneLabel.setText(user.getPhoneNumber());
                fullNameLabel.setText(user.getFullName());
                dobLabel.setText(user.getDateOfBirth().format(formatter));
                genderLabel.setText(user.getGender().getValue());
                addressLabel.setText(user.getAddress());
                roleLabel.setText(user.getRole().getValue());
                statusLabel.setText(user.getStatus().getValue());
            } else {
                errorLabel.setText("Không thể tải thông tin người dùng");
            }
        } else {
            errorLabel.setText("Vui lòng đăng nhập để xem thông tin");
        }
    }

    @FXML
    private void handleBack() {
        HomeView homeView = new HomeView(stage);
        homeView.setCurrentUser(currentUser);
        homeView.loadView("/view/home.fxml");
    }

    @FXML
    private void handleClose() {
        // Lấy node gốc VBox của profile
        javafx.scene.Node root = userIdLabel.getScene().getRoot();
        // Tìm StackPane cha (contentArea)
        javafx.scene.Parent parent = root.getParent();
        if (parent instanceof javafx.scene.layout.StackPane) {
            ((javafx.scene.layout.StackPane) parent).getChildren().remove(root);
        }
        // Nếu dùng Stage riêng:
        // ((Stage) userIdLabel.getScene().getWindow()).close();
    }
}