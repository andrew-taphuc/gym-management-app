package view.adminView;

import model.Member;

import controller.MemberController;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import model.User;
import view.register.RegisterView;

import java.util.List;
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
    private TableColumn<Member, String> statusColumn;
    
    @FXML
    private TextField searchField;

    private User currentUser;
    private MemberController memberController = new MemberController();
    private ObservableList<Member> allMembers = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Plans & Renewals | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
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
        memberCodeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMemberCode())
        );
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
        statusColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStatus() != null)
                return new SimpleStringProperty(cellData.getValue().getStatus().getValue());
            else
                return new SimpleStringProperty("");
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
        ObservableList<Member> filtered = allMembers.filtered(member ->
            member.getMemberCode().toLowerCase().contains(lower) ||
            (member.getUser() != null && member.getUser().getPhoneNumber() != null &&
             member.getUser().getPhoneNumber().toLowerCase().contains(lower))
        );
        memberTable.setItems(filtered);
    }

    @FXML
    private void handleRegisterNewAccount() {
        try {
            Stage stage = new Stage();
            RegisterView registerView = new RegisterView(stage);
            registerView.loadView("/view/register/register.fxml");

            stage.setTitle("Đăng ký tài khoản mới");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}