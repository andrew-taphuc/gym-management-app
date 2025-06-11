package view;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import model.User;
import view.register.RegisterView;
import view.userView.HomeView;
import view.adminView.HomeView_admin;
import view.ownerView.HomeView_Owner;
import view.trainerView.HomeView_trainer;
import model.enums.enum_Role;
import javafx.application.Platform;
import javafx.concurrent.Task;
import view.ownerView.*;

public class LoginView extends BaseView {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

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

        // Hiển thị loading
        loadingIndicator.setVisible(true);
        loginButton.setDisable(true);

        // Tạo task để xử lý đăng nhập trong background
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                // Giả lập thời gian xử lý
                Thread.sleep(1000);
                return userController.login(username, password);
            }
        };

        // Xử lý kết quả đăng nhập
        loginTask.setOnSucceeded(event -> {
            User user = loginTask.getValue();
            if (user != null) {
                currentUser = user;
                try {
                    if (user.getRole().getValue().equals("Nhân viên quản lý")) {
                        System.out.println("Đăng nhập thành công!");
                        System.out.println("Vai trò: " + user.getRole().getValue());
                        System.out.println("Họ tên: " + user.getFullName());
                        HomeView_admin homeView_admin = new HomeView_admin(stage);
                        navigateToView("/view/adminView/home.fxml", homeView_admin);
                    } else if (user.getRole().getValue().equals("Huấn luyện viên")) {
                        System.out.println("Đăng nhập thành công!");
                        System.out.println("Vai trò: " + user.getRole().getValue());
                        System.out.println("Họ tên: " + user.getFullName());
                        HomeView_trainer homeView_trainer = new HomeView_trainer(stage);
                        navigateToView("/view/trainerView/home.fxml", homeView_trainer);
                    } else if (user.getRole().getValue().equals("Chủ phòng tập")) {
                        System.out.println("Đăng nhập thành công!");
                        System.out.println("Vai trò: " + user.getRole().getValue());
                        System.out.println("Họ tên: " + user.getFullName());
                        HomeView_Owner homeView_Owner = new HomeView_Owner(stage);
                        navigateToView("/view/ownerView/home.fxml", homeView_Owner);
                    } else {
                        // Kiểm tra status của member
                        if (user.getStatus().getValue().equals("Hoạt động")) {
                            System.out.println("Đăng nhập thành công!");
                            System.out.println("Vai trò: " + user.getRole().getValue());
                            System.out.println("Họ tên: " + user.getFullName());
                            HomeView homeView = new HomeView(stage);
                            navigateToView("/view/userView/home.fxml", homeView);
                        } else {
                            errorLabel.setText("Tài khoản đã " + user.getStatus().getValue().toLowerCase()
                                    + ", không thể đăng nhập");
                        }
                    }
                } catch (Exception e) {
                    errorLabel.setText("Lỗi chuyển trang: " + e.getMessage());
                }
            } else {
                errorLabel.setText("Tên đăng nhập hoặc mật khẩu không đúng");
            }
            // Ẩn loading
            loadingIndicator.setVisible(false);
            loginButton.setDisable(false);
        });

        // Xử lý lỗi
        loginTask.setOnFailed(event -> {
            errorLabel.setText("Lỗi đăng nhập: " + loginTask.getException().getMessage());
            loadingIndicator.setVisible(false);
            loginButton.setDisable(false);
        });

        // Chạy task trong thread riêng
        new Thread(loginTask).start();
    }

    @FXML
    private void handleRegister() {
        RegisterView registerView = new RegisterView(stage);
        registerView.loadView("/view/register/register.fxml");
    }
}