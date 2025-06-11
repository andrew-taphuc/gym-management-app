package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import model.User;
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;
import view.userView.HomeView;
import controller.UserController;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

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

                // userIdLabel.setText(String.valueOf(user.getUserId()));
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

    @FXML
    private void handleEdit() {
        if (currentUser == null) {
            errorLabel.setText("Vui lòng đăng nhập để chỉnh sửa thông tin");
            return;
        }

        // Tạo dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa thông tin");
        dialog.setHeaderText("Chỉnh sửa thông tin cá nhân");

        // Tạo các trường nhập liệu
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField emailField = new TextField(currentUser.getEmail());
        TextField phoneField = new TextField(currentUser.getPhoneNumber());
        TextField fullNameField = new TextField(currentUser.getFullName());
        DatePicker dobPicker = new DatePicker(currentUser.getDateOfBirth());
        ComboBox<enum_Gender> genderCombo = new ComboBox<>(FXCollections.observableArrayList(enum_Gender.values()));
        genderCombo.setValue(currentUser.getGender());
        genderCombo.setCellFactory(lv -> new ListCell<enum_Gender>() {
            @Override
            protected void updateItem(enum_Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getValue());
            }
        });
        genderCombo.setButtonCell(new ListCell<enum_Gender>() {
            @Override
            protected void updateItem(enum_Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getValue());
            }
        });
        TextField addressField = new TextField(currentUser.getAddress());

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Số điện thoại:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Họ và tên:"), 0, 2);
        grid.add(fullNameField, 1, 2);
        grid.add(new Label("Ngày sinh:"), 0, 3);
        grid.add(dobPicker, 1, 3);
        grid.add(new Label("Giới tính:"), 0, 4);
        grid.add(genderCombo, 1, 4);
        grid.add(new Label("Địa chỉ:"), 0, 5);
        grid.add(addressField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Thêm nút OK và Cancel
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Xử lý khi nhấn nút Lưu
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                User updatedUser = new User();
                updatedUser.setUserId(currentUser.getUserId());
                updatedUser.setUsername(currentUser.getUsername());
                updatedUser.setPassword(currentUser.getPassword());
                updatedUser.setEmail(emailField.getText());
                updatedUser.setPhoneNumber(phoneField.getText());
                updatedUser.setFullName(fullNameField.getText());
                updatedUser.setDateOfBirth(dobPicker.getValue());
                updatedUser.setGender(genderCombo.getValue());
                updatedUser.setAddress(addressField.getText());
                updatedUser.setCreatedAt(currentUser.getCreatedAt());
                updatedUser.setUpdatedAt(currentUser.getUpdatedAt());
                updatedUser.setStatus(currentUser.getStatus());
                updatedUser.setRole(currentUser.getRole());
                return updatedUser;
            }
            return null;
        });

        // Hiển thị dialog và xử lý kết quả
        dialog.showAndWait().ifPresent(updatedUser -> {
            // Cập nhật thông tin user trong database
            if (userController.updateUser(updatedUser)) {
                // Cập nhật giao diện
                emailLabel.setText(updatedUser.getEmail());
                phoneLabel.setText(updatedUser.getPhoneNumber());
                fullNameLabel.setText(updatedUser.getFullName());
                dobLabel.setText(updatedUser.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                genderLabel.setText(updatedUser.getGender().getValue());
                addressLabel.setText(updatedUser.getAddress());
                errorLabel.getStyleClass().remove("profile-error");
                errorLabel.getStyleClass().add("profile-success");
                errorLabel.setText("Cập nhật thông tin thành công!");
            } else {
                errorLabel.getStyleClass().remove("profile-success");
                errorLabel.getStyleClass().add("profile-error");
                errorLabel.setText("Không thể cập nhật thông tin!");
            }
        });
    }
}