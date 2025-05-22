package view;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class LoginView extends BaseView {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private UserController userController;

    public LoginView(Stage stage) {
        super(stage);
        this.userController = new UserController();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin đăng nhập");
            return;
        }

        User user = userController.login(username, password);
        if (user != null) {
            setCurrentUser(user);
            System.out.println("Đăng nhập thành công!");
            System.out.println("Vai trò: " + user.getRole().getValue());
            System.out.println("Tên người dùng: " + user.getFullName());
            HomeView homeView = new HomeView(stage);
            homeView.setCurrentUser(user);
            homeView.loadView("/view/home.fxml");
        } else {
            showError("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }
}