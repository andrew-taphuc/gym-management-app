package view.userView;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.User;
import view.userView.FeedbackDAO;

import java.util.function.Consumer;

public class FeedbackFormController {

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextArea commentTextArea;

    private User user;
    private Consumer<Void> feedbackSubmittedCallback;
    private FeedbackDAO feedbackDAO;

    @FXML
    public void initialize() {
        // Thiết lập các options cho ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Cơ sở vật chất",
                "Nhân viên", 
                "Khác"
        ));
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFeedbackSubmittedCallback(Consumer<Void> callback) {
        this.feedbackSubmittedCallback = callback;
    }

    public void setFeedbackDAO(FeedbackDAO dao) {
        this.feedbackDAO = dao;
    }

    @FXML
    private void handleSubmit() {
        String type = typeComboBox.getValue();
        String comment = commentTextArea.getText();

        // Validate input
        if (type == null || type.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn loại phản hồi!");
            return;
        }

        if (comment == null || comment.trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập nội dung phản hồi!");
            return;
        }

        try {
            // Lưu feedback vào database
            boolean success = feedbackDAO.insertFeedback(user.getUserId(), type, comment.trim());
            
            if (success) {
                System.out.printf("Feedback đã lưu vào DB: [%s] %s\n", type, comment.trim());
                
                // Hiển thị thông báo thành công
                showAlert("Thành công", "Feedback đã được gửi thành công!");

                // Callback để reload dữ liệu từ DB
                if (feedbackSubmittedCallback != null) {
                    feedbackSubmittedCallback.accept(null);
                }

                // Đóng form
                closeForm();
            } else {
                showAlert("Lỗi", "Không thể lưu feedback. Vui lòng thử lại!");
            }
            
        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra khi gửi feedback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeForm() {
        Stage stage = (Stage) typeComboBox.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}