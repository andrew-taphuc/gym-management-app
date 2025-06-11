package view.adminView;

import controller.MemberController;
import javafx.event.ActionEvent;
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
import model.User;
import model.Member;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
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

        // Tạo cột Action với button "Chọn"
        colAction.setCellFactory(param -> new TableCell<Member, Void>() {
            private final Button selectButton = new Button("Check-in");

            {
                selectButton.setStyle(
                        "-fx-background-color:linear-gradient(to bottom, #4caf50, #388e3c);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-cursor: hand;" +
                                "-fx-pref-width: 80px;" +
                                "-fx-alignment: center;");
                selectButton.setMaxWidth(Double.MAX_VALUE);

                selectButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    loadMemberTrackView(currentUser, member);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(selectButton);
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
}