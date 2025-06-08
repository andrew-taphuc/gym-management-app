package view.adminView;

import controller.MemberController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    private User currentUser;
    private MemberController memberController = new MemberController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Workouts | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Tìm kiếm hội viên");
        searchButton.setOnAction(this::handleSearchButtonClick);
    }

    private void handleSearchButtonClick(ActionEvent event) {
        String keyword = inputField.getText().trim();
        if (keyword.isEmpty()) {
            showAlert("Vui lòng nhập mã hội viên hoặc số điện thoại.");
            return;
        }
        Member member = memberController.findMemberByCodeOrPhone(keyword);
        if (member == null) {
            showAlert("Không tìm thấy hội viên phù hợp.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("memberTrack.fxml"));
            Parent root = loader.load();
            MemberTrackController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setMember(member);
            Stage stage = (Stage) searchButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở trang thông tin hội viên.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // private void loadViewWithUser(String fxml, Object controller) {
    //     // Code chuyển scene ở đây
    // }
    

}