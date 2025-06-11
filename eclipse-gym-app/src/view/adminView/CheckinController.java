package view.adminView;

import controller.MemberController;
import controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import model.User;
import model.Member;
import model.enums.enum_Gender;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckinController {
    @FXML
    private Label pageTitle;
    @FXML
    private TextField inputField;
    @FXML
    private Button searchButton;
    @FXML
    private StackPane contentArea;
    @FXML
    private TableView<Member> tblMembers;
    @FXML
    private TableColumn<Member, String> colMemberCode;
    @FXML
    private TableColumn<Member, String> colFullName;
    @FXML
    private TableColumn<Member, String> colPhoneNumber;
    @FXML
    private TableColumn<Member, String> colJoinDate;
    @FXML
    private TableColumn<Member, String> colStatus;
    @FXML
    private TableColumn<Member, String> colUserStatus;
    @FXML
    private TableColumn<Member, Void> colAction;

    private User currentUser;
    private MemberController memberController = new MemberController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Checkin | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Tìm kiếm hội viên");
        searchButton.setOnAction(e -> handleSearchClick());

        setupTableColumns();
        loadAllMembers();
    }

    private void setupTableColumns() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colMemberCode.setCellValueFactory(new PropertyValueFactory<>("memberCode"));

        colFullName.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            if (member.getUser() != null) {
                return new SimpleStringProperty(member.getUser().getFullName());
            }
            return new SimpleStringProperty("");
        });

        colPhoneNumber.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            if (member.getUser() != null) {
                return new SimpleStringProperty(member.getUser().getPhoneNumber());
            }
            return new SimpleStringProperty("");
        });

        colJoinDate.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            if (member.getJoinDate() != null) {
                return new SimpleStringProperty(member.getJoinDate().format(dateFormatter));
            }
            return new SimpleStringProperty("");
        });

        colStatus.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            if (member.getStatus() != null) {
                String statusText = member.getStatus().name().equals("ACTIVE") ? "Hoạt động" : "Chưa kích hoạt";
                return new SimpleStringProperty(statusText);
            }
            return new SimpleStringProperty("");
        });

        colUserStatus.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            if (member.getUser() != null && member.getUser().getStatus() != null) {
                String statusText = "";
                switch (member.getUser().getStatus()) {
                    case ACTIVE:
                        statusText = "Hoạt động";
                        break;
                    case LOCKED:
                        statusText = "Đã khóa";
                        break;
                    case SUSPENDED:
                        statusText = "Tạm ngừng";
                        break;
                }
                return new SimpleStringProperty(statusText);
            }
            return new SimpleStringProperty("Chưa xác định");
        });

        // Tạo cột Action với button "Chọn"
        colAction.setCellFactory(param -> new TableCell<Member, Void>() {
            private final Button selectButton = new Button("Check-in");
            private final Button editButton = new Button("Chỉnh sửa");
            private final Button disableButton = new Button("Khóa");
            private final HBox buttonBox = new HBox(10); // 10 là khoảng cách giữa các button

            {
                selectButton.setStyle(
                        "-fx-background-color:linear-gradient(to bottom, #4caf50, #388e3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-cursor: hand;" +
                                "-fx-pref-width: 100px;" +
                                "-fx-alignment: center;");

                editButton.setStyle(
                        "-fx-background-color:linear-gradient(to bottom, #ffc107, #ffa000);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-cursor: hand;" +
                                "-fx-pref-width: 100px;" +
                                "-fx-alignment: center;");

                disableButton.setStyle(
                        "-fx-background-color:linear-gradient(to bottom, #f44336, #d32f2f);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-cursor: hand;" +
                                "-fx-pref-width: 100px;" +
                                "-fx-alignment: center;");

                buttonBox.getChildren().addAll(selectButton, editButton, disableButton);
                buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

                selectButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    loadMemberTrackView(currentUser, member);
                });

                editButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    showEditDialog(member);
                });

                disableButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    handleDisableMember(member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Member member = getTableView().getItems().get(getIndex());
                    if (member.getUser() != null && member.getUser().getStatus() == model.enums.enum_UserStatus.LOCKED) {
                        disableButton.setText("Mở khóa");
                        disableButton.setStyle(
                                "-fx-background-color:linear-gradient(to bottom, #2196f3, #1976d2);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-cursor: hand;" +
                                "-fx-pref-width: 100px;" +
                                "-fx-alignment: center;");
                    } else {
                        disableButton.setText("Khóa");
                        disableButton.setStyle(
                                "-fx-background-color:linear-gradient(to bottom, #f44336, #d32f2f);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-cursor: hand;" +
                                "-fx-pref-width: 100px;" +
                                "-fx-alignment: center;");
                    }
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void loadAllMembers() {
        try {
            List<Member> members = memberController.getAllMembers();
            tblMembers.getItems().setAll(members);
            System.out.println("Đã load " + members.size() + " hội viên");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể tải danh sách hội viên: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMemberTrackView(User currentUser, Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("memberTrack.fxml"));
            Parent view = loader.load();
            view.adminView.MemberTrackController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setMember(member);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở trang thông tin hội viên.");
        }
    }

    @FXML
    private void handleSearchClick() {
        String keyword = inputField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Vui lòng nhập mã hội viên hoặc số điện thoại.");
            return;
        }
        MemberController memberController = new MemberController();
        Member member = memberController.findMemberByCodeOrPhone(keyword);
        if (member == null) {
            showAlert("Không tìm thấy hội viên phù hợp.");
            return;
        }
        loadMemberTrackView(currentUser, member);
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    private void showEditDialog(Member member) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa thông tin hội viên");
        dialog.setHeaderText("Chỉnh sửa thông tin cá nhân");

        // Tạo các trường nhập liệu
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        User user = member.getUser();
        TextField emailField = new TextField(user.getEmail());
        emailField.setPromptText("Nhập email");
        
        TextField phoneField = new TextField(user.getPhoneNumber());
        phoneField.setPromptText("Nhập số điện thoại");
        
        TextField fullNameField = new TextField(user.getFullName());
        fullNameField.setPromptText("Nhập họ và tên");
        
        DatePicker dobPicker = new DatePicker(user.getDateOfBirth());
        dobPicker.setPromptText("Chọn ngày sinh");
        
        ComboBox<enum_Gender> genderCombo = new ComboBox<>(FXCollections.observableArrayList(enum_Gender.values()));
        genderCombo.setValue(user.getGender());
        genderCombo.setPromptText("Chọn giới tính");
        
        TextField addressField = new TextField(user.getAddress());
        addressField.setPromptText("Nhập địa chỉ");

        // Cấu hình hiển thị tiếng Việt cho ComboBox
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
                updatedUser.setUserId(user.getUserId());
                updatedUser.setUsername(user.getUsername());
                updatedUser.setPassword(user.getPassword());
                updatedUser.setEmail(emailField.getText());
                updatedUser.setPhoneNumber(phoneField.getText());
                updatedUser.setFullName(fullNameField.getText());
                updatedUser.setDateOfBirth(dobPicker.getValue());
                updatedUser.setGender(genderCombo.getValue());
                updatedUser.setAddress(addressField.getText());
                updatedUser.setCreatedAt(user.getCreatedAt());
                updatedUser.setUpdatedAt(user.getUpdatedAt());
                updatedUser.setStatus(user.getStatus());
                updatedUser.setRole(user.getRole());
                return updatedUser;
            }
            return null;
        });

        // Hiển thị dialog và xử lý kết quả
        dialog.showAndWait().ifPresent(updatedUser -> {
            UserController userController = new UserController();
            if (userController.updateUser(updatedUser)) {
                showAlert("Cập nhật thông tin thành công!");
                loadAllMembers(); // Refresh lại bảng
            } else {
                showAlert("Không thể cập nhật thông tin!");
            }
        });
    }

    private void handleDisableMember(Member member) {
        UserController userController = new UserController();
        User user = userController.getUserByID(member.getUserId());
        if (user != null) {
            if (user.getStatus() == model.enums.enum_UserStatus.LOCKED) {
                // Nếu tài khoản đang bị khóa, hiển thị dialog xác nhận mở khóa
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Xác nhận mở khóa");
                confirmDialog.setHeaderText(null);
                confirmDialog.setContentText("Bạn có chắc chắn muốn mở khóa tài khoản của hội viên " + user.getFullName() + "?");

                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        user.setStatus(model.enums.enum_UserStatus.ACTIVE);
                        if (userController.updateUserStatus(user)) {
                            showAlert("Đã mở khóa tài khoản thành công!");
                            loadAllMembers(); // Refresh lại bảng
                        } else {
                            showAlert("Không thể mở khóa tài khoản!");
                        }
                    }
                });
            } else {
                // Nếu tài khoản đang hoạt động, hiển thị dialog xác nhận khóa
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Xác nhận khóa tài khoản");
                confirmDialog.setHeaderText(null);
                confirmDialog.setContentText("Bạn có chắc chắn muốn khóa tài khoản của hội viên " + user.getFullName() + "?");

                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        user.setStatus(model.enums.enum_UserStatus.LOCKED);
                        if (userController.updateUserStatus(user)) {
                            showAlert("Đã khóa tài khoản thành công!");
                            loadAllMembers(); // Refresh lại bảng
                        } else {
                            showAlert("Không thể khóa tài khoản!");
                        }
                    }
                });
            }
        }
    }
}