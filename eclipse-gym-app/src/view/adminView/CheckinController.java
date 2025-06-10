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
import model.User;
import model.Member;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CheckinController {
    @FXML
    private Label pageTitle;
    @FXML
    private TextField inputField;
    @FXML 
    private Button searchButton;
    @FXML
    private StackPane contentArea;

    private User currentUser;
    private MemberController memberController = new MemberController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Workouts | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Tìm kiếm hội viên");
        searchButton.setOnAction(e -> handleSearchClick());
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