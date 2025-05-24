package view.register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;
import view.BaseView;
import view.LoginView;
import controller.UserController;
import java.time.LocalDate;

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
        LoginView loginView = new LoginView(stage);
        loginView.loadView("/view/login.fxml");
    }
}