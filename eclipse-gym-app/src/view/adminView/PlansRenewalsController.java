package view.adminView;

import model.Member;
import controller.MemberController;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import model.User;
import view.register.RegisterView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlansRenewalsController {
    @FXML
    private Button registerNewAccountButton;

    @FXML
    private TableView<Member> memberTable;

    @FXML
    private TableColumn<Member, String> memberCodeColumn;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, String> statusColumn;

    @FXML
    private TableColumn<Member, Void> actionColumn;

    @FXML
    private TextField searchField;

    private User currentUser;
    private MemberController memberController = new MemberController();
    private ObservableList<Member> allMembers = FXCollections.observableArrayList();
    private Stage stage;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public PlansRenewalsController() {
        // Constructor mặc định
    }

    public PlansRenewalsController(Stage stage) {
        this.stage = stage;
    }

    public void refreshMemberTable() {
        allMembers.setAll(memberController.getAllMembers());
        loadMemberTable(allMembers);
    }

    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(this);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        System.out.println(
                "Trang: Plans & Renewals | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        allMembers.setAll(memberController.getAllMembers());
        loadMemberTable(allMembers);

        // Sự kiện tìm kiếm
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterMemberTable(newVal);
        });

        // Sự kiện click vào nút đăng ký tài khoản mới
        registerNewAccountButton.setOnAction(event -> handleRegisterNewAccount());
    }

    private void loadMemberTable(ObservableList<Member> members) {
        // Thiết lập cách lấy dữ liệu cho từng cột
        memberCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberCode()));
        nameColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUser() != null)
                return new SimpleStringProperty(cellData.getValue().getUser().getFullName());
            else
                return new SimpleStringProperty("");
        });
        phoneColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUser() != null)
                return new SimpleStringProperty(cellData.getValue().getUser().getPhoneNumber());
            else
                return new SimpleStringProperty("");
        });
        emailColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getUser() != null)
                return new SimpleStringProperty(cellData.getValue().getUser().getEmail());
            else
                return new SimpleStringProperty("");
        });
        statusColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStatus() != null)
                return new SimpleStringProperty(cellData.getValue().getStatus().getValue());
            else
                return new SimpleStringProperty("");
        });

        // Cột thao tác
        actionColumn.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            private final Button infoButton = new Button("Gia hạn + mua PT");
            private final javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(infoButton);

            {
                infoButton.setOnAction(event -> {
                    Member member = getTableView().getItems().get(getIndex());
                    System.out.println("Bạn đang thao tác với hội viên: " + member.getUserId());

                    // Load HomeView với Plans & Renewals của member được chọn
                    try {
                        // Tạo HomeView với member's user
                        Stage userStage = new Stage();
                        view.userView.HomeView homeView = new view.userView.HomeView(userStage);
                        homeView.setCurrentUser(member.getUser());

                        // Load HomeView FXML
                        FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/view/userView/home.fxml"));
                        homeLoader.setController(homeView);
                        Parent homeRoot = homeLoader.load();

                        // Tạo và load Plans & Renewals controller
                        view.userView.PlansRenewalsController plansController = new view.userView.PlansRenewalsController();
                        plansController.setCurrentUser(member.getUser());

                        // Load Plans & Renewals vào HomeView
                        homeView.loadViewWithUser("plans_renewals.fxml", plansController);

                        // Hiển thị HomeView
                        userStage.setTitle("Quản lý gói tập - " + member.getUser().getFullName());
                        userStage.setScene(new Scene(homeRoot));
                        userStage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Lỗi khi load HomeView cho user: " + e.getMessage());
                    }
                });
                infoButton.setStyle("-fx-font-size: 14px;");
                hbox.setAlignment(javafx.geometry.Pos.CENTER); // Căn giữa nút trong cell
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

        // Đưa dữ liệu vào bảng
        memberTable.setItems(members);
    }

    private void filterMemberTable(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            memberTable.setItems(allMembers);
            return;
        }
        String lower = keyword.toLowerCase();
        ObservableList<Member> filtered = allMembers
                .filtered(member -> member.getMemberCode().toLowerCase().contains(lower) ||
                        (member.getUser() != null && member.getUser().getPhoneNumber() != null &&
                                member.getUser().getPhoneNumber().toLowerCase().contains(lower)));
        memberTable.setItems(filtered);
    }

    @FXML
    private void handleRegisterNewAccount() {
        try {
            Stage stage = new Stage();
            RegisterView registerView = new RegisterView(stage);
            registerView.loadView("/view/register/register.fxml");

            stage.setTitle("Đăng ký tài khoản mới");
            stage.showAndWait();

            refreshMemberTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}