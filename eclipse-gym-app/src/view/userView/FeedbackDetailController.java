package view.userView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Feedback;
import controller.FeedbackController;

public class FeedbackDetailController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label feedbackTypeLabel;
    @FXML
    private VBox equipmentInfoSection;
    @FXML
    private Label equipmentInfoLabel;
    @FXML
    private TextArea feedbackCommentArea;
    @FXML
    private Label feedbackDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox responseSection;
    @FXML
    private TextArea responseCommentArea;
    @FXML
    private Label responseDateLabel;

    private Feedback feedback;
    private FeedbackController feedbackController;

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
        this.feedbackController = new FeedbackController();
        populateFields();
    }

    @FXML
    public void initialize() {
        // Initialize controller
    }

    private void populateFields() {
        if (feedback == null)
            return;

        // Hiển thị thông tin feedback gốc
        feedbackTypeLabel.setText(feedback.getFeedbackType());
        feedbackCommentArea.setText(feedback.getComment());
        feedbackDateLabel.setText(feedback.getFeedbackDate());

        // Thiết lập màu sắc cho trạng thái
        String status = feedback.getStatus();
        statusLabel.setText(status);
        setStatusStyle(status);

        // Hiển thị thông tin thiết bị nếu là feedback cơ sở vật chất
        if ("Cơ sở vật chất".equals(feedback.getFeedbackType()) && feedback.getEquipmentId() > 0) {
            showEquipmentInfo(feedback.getEquipmentId());
        }

        // Hiển thị phản hồi từ chủ phòng tập
        String responseComment = feedback.getResponseComment();
        String responseDate = feedback.getResponseDate();

        if (responseComment != null && !responseComment.trim().isEmpty()) {
            responseCommentArea.setText(responseComment);
            if (responseDate != null && !responseDate.trim().isEmpty()) {
                responseDateLabel.setText(responseDate);
            } else {
                responseDateLabel.setText("Chưa có ngày phản hồi");
            }
        } else {
            responseCommentArea.setText("Chưa có phản hồi từ chủ phòng tập.");
            responseDateLabel.setText("");
        }
    }

    private void setStatusStyle(String status) {
        switch (status) {
            case "Chờ xử lý":
                statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff9800;");
                break;
            case "Đang xử lý":
                statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196f3;");
                break;
            case "Đã giải quyết":
                statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4caf50;");
                break;
            default:
                statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #666;");
                break;
        }
    }

    private void showEquipmentInfo(int equipmentId) {
        try {
            // Tận dụng method có sẵn trong FeedbackController để lấy thông tin thiết bị
            // Trước tiên cần lấy equipment code từ ID
            String equipmentInfo = getEquipmentInfoById(equipmentId);

            if (equipmentInfo != null && !equipmentInfo.trim().isEmpty()) {
                equipmentInfoLabel.setText(equipmentInfo);
                equipmentInfoSection.setVisible(true);
                equipmentInfoSection.setManaged(true);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị thông tin thiết bị: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy thông tin thiết bị từ ID (tận dụng method có sẵn)
     */
    private String getEquipmentInfoById(int equipmentId) {
        // Lấy equipment code từ ID bằng method có sẵn trong FeedbackController
        String equipmentCode = feedbackController.getEquipmentCodeById(equipmentId);
        if (equipmentCode != null) {
            // Sau đó dùng method có sẵn để lấy thông tin đầy đủ
            return feedbackController.getEquipmentInfoByCode(equipmentCode);
        }
        return null;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}