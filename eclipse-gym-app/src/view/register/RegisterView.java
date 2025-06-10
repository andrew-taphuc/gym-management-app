package view.register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.User;
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;
import view.BaseView;
import view.LoginView;
import controller.UserController;
import view.adminView.PlansRenewalsController;

public class RegisterView extends BaseView {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField fullNameField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField addressField;
    @FXML
    private Label errorLabel;

    private UserController userController;

    // Mật khẩu cần có ít nhất 6 ký tự
    private boolean isValidPassword(String password) {
        return password != null && password.matches("^\\d{6,}$");
    }
    
    // Kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    // Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0
    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^0\\d{9}$");
    }

    public RegisterView(Stage stage) {
        super(stage);
        this.userController = new UserController();
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Nam", "Nữ", "Khác");
        genderComboBox.setValue("Nam");
    }

    @FXML
    private void handleRegister() {
        // Validate input
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                emailField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                fullNameField.getText().isEmpty() || dobPicker.getValue() == null ||
                addressField.getText().isEmpty()) {
            errorLabel.setText("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if (!isValidPassword(passwordField.getText())) {
            errorLabel.setText("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (!isValidEmail(emailField.getText())) {
            errorLabel.setText("Email không đúng định dạng!");
            return;
        }
        if (!isValidPhone(phoneField.getText())) {
            errorLabel.setText("Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0!");
            return;
        }

        if (userController.isUsernameExists(usernameField.getText())) {
            errorLabel.setText("Tên đăng nhập đã tồn tại!");
            return;
        }

        if( userController.isEmailExists(emailField.getText())) {
            errorLabel.setText("Email đã được sử dụng!");
            return;
        }

        // Create user object
        User newUser = new User();
        newUser.setUsername(usernameField.getText());
        newUser.setPassword(passwordField.getText());
        newUser.setEmail(emailField.getText());
        newUser.setPhoneNumber(phoneField.getText());
        newUser.setFullName(fullNameField.getText());
        newUser.setDateOfBirth(dobPicker.getValue());
        newUser.setGender(enum_Gender.fromValue(genderComboBox.getValue()));
        newUser.setAddress(addressField.getText());
        newUser.setRole(enum_Role.MEMBER);
        newUser.setStatus(enum_UserStatus.ACTIVE);

        // Load membership plan view
        MembershipPlanView membershipPlanView = new MembershipPlanView(stage);
        membershipPlanView.setNewUser(newUser);
        membershipPlanView.loadView("/view/register/membership_plan.fxml");
    }

    @FXML
    private void handleBack() {
        if (UserController.getCurrentUser() == null) {
            LoginView loginView = new LoginView(stage);
            loginView.loadView("/view/login.fxml");
        } else {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        }
    }
}
