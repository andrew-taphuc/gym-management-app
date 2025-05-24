package view;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;
import view.register.RegisterView;
import view.userView.HomeView;
import view.adminView.HomeView_admin;
import view.trainerView.HomeView_trainer;
import model.enums.enum_Role;

public class LoginView extends BaseView {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private UserController userController;

    public LoginView(Stage stage) {
        super(stage);
        this.userController = new UserController();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        try {
            User user = userController.login(username, password);
            if (user != null) {
                if (user.getRole().getValue() == "Nhân viên quản lý") {
                    System.out.println("Đăng nhập thành công!");
                    System.out.println("Vai trò: " + user.getRole().getValue());
                    System.out.println("Họ tên: " + user.getFullName());
                    currentUser = user;
                    HomeView_admin homeView_admin = new HomeView_admin(stage);
                    navigateToView("/view/adminView/home.fxml", homeView_admin);
                }
                else if (user.getRole().getValue() == "Huấn luyện viên"){
                    System.out.println("Đăng nhập thành công!");
                    System.out.println("Vai trò: " + user.getRole().getValue());
                    System.out.println("Họ tên: " + user.getFullName());
                    currentUser = user;
                    HomeView_trainer homeView_trainer = new HomeView_trainer(stage);
                    navigateToView("/view/trainerView/home.fxml", homeView_trainer);
                }
                else {
                    System.out.println("Đăng nhập thành công!");
                    System.out.println("Vai trò: " + user.getRole().getValue());
                    System.out.println("Họ tên: " + user.getFullName());
                    currentUser = user;
                    HomeView homeView = new HomeView(stage);
                    navigateToView("/view/userView/home.fxml", homeView);
                }
            } else {
                errorLabel.setText("Tên đăng nhập hoặc mật khẩu không đúng");
            }
        } catch (Exception e) {
            errorLabel.setText("Lỗi đăng nhập: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        RegisterView registerView = new RegisterView(stage);
        registerView.loadView("/view/register/register.fxml");
    }
}