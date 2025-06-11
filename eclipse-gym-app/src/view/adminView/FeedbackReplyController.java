package view.adminView;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Feedback;
import model.User;
import controller.FeedbackController;
import controller.RoomEquipmentController;
import javafx.scene.layout.HBox;

public class FeedbackReplyController {

    @FXML
    private Label memberNameLabel;
    @FXML
    private Label feedbackTypeLabel;
    @FXML
    private VBox equipmentInfoSection;
    @FXML
    private Label equipmentInfoLabel;
    @FXML
    private Button updateStatusButton;
    @FXML
    private TextArea memberCommentArea;
    @FXML
    private Label feedbackDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea replyCommentArea;
    @FXML
    private Button sendReplyButton;

    private Feedback feedback;
    private User currentUser;
    private FeedbackController feedbackController;
    private RoomEquipmentController roomEquipmentController;
    private String currentEquipmentStatus;
    private int equipmentId;
    private Runnable onSuccessCallback;

    public void setData(Feedback feedback, User currentUser, Runnable onSuccessCallback) {
        this.feedback = feedback;
        this.currentUser = currentUser;
        this.onSuccessCallback = onSuccessCallback;
        this.feedbackController = new FeedbackController();
        this.roomEquipmentController = new RoomEquipmentController();

        populateFields();
    }

    @FXML
    public void initialize() {
        // No need to initialize ComboBox anymore
    }

    private void populateFields() {
        if (feedback == null)
            return;

        // Display member feedback info
        memberNameLabel.setText(feedback.getMemberName());
        feedbackTypeLabel.setText(feedback.getFeedbackType());
        memberCommentArea.setText(feedback.getComment());
        feedbackDateLabel.setText(formatDate(feedback.getFeedbackDate()));

        // Set status color
        String status = feedback.getStatus();
        statusLabel.setText(status);
        setStatusStyle(status);

        // Show equipment info if it's facility feedback
        if ("Cơ sở vật chất".equals(feedback.getFeedbackType()) && feedback.getEquipmentId() > 0) {
            showEquipmentInfo(feedback.getEquipmentId());
        } else {
            equipmentInfoSection.setVisible(false);
            equipmentInfoSection.setManaged(false);
        }

        // Display existing response if any
        String existingResponse = feedback.getResponseComment();
        if (existingResponse != null && !existingResponse.isEmpty()) {
            replyCommentArea.setText(existingResponse);
            replyCommentArea.setEditable(false);
            replyCommentArea.setStyle("-fx-background-color: #f5f5f5; -fx-font-size: 14px; -fx-border-color: #ddd; -fx-border-radius: 5;");
            
            // Disable send reply button
            sendReplyButton.setDisable(true);
            sendReplyButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 10 20; -fx-cursor: default; -fx-font-size: 16px;");
        }
    }

    private void showEquipmentInfo(int equipmentId) {
        try {
            this.equipmentId = equipmentId;

            // Get equipment info using existing method
            String equipmentCode = feedbackController.getEquipmentCodeById(equipmentId);
            if (equipmentCode != null) {
                String equipmentInfo = feedbackController.getEquipmentInfoByCode(equipmentCode);
                if (equipmentInfo != null) {
                    // Parse equipment info to get status
                    parseAndDisplayEquipmentInfo(equipmentInfo);

                    equipmentInfoSection.setVisible(true);
                    equipmentInfoSection.setManaged(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị thông tin thiết bị: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseAndDisplayEquipmentInfo(String equipmentInfo) {
        // Parse format: "Tên (Mã) - Trạng thái"
        String[] parts = equipmentInfo.split(" - ");
        if (parts.length >= 2) {
            String nameAndCode = parts[0];
            currentEquipmentStatus = parts[1];

            // Hiển thị tên và mã thiết bị trước, sau đó đến trạng thái
            equipmentInfoLabel.setText(nameAndCode + " - " + currentEquipmentStatus);

            // Set button text based on current status
            updateStatusButtonText();
        }
    }

    private Label createStatusLabel(String status) {
        Label statusLabel = new Label(status);
        if ("Hoạt động".equals(status)) {
            statusLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
        } else if ("Bảo trì".equals(status)) {
            statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
        }
        return statusLabel;
    }

    private void updateStatusButtonText() {
        if ("Hoạt động".equals(currentEquipmentStatus)) {
            updateStatusButton.setText("Bảo trì");
            updateStatusButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 4; -fx-padding: 4 12; -fx-cursor: hand; -fx-font-size: 12px;");
        } else if ("Bảo trì".equals(currentEquipmentStatus)) {
            updateStatusButton.setText("Hoạt động");
            updateStatusButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 4; -fx-padding: 4 12; -fx-cursor: hand; -fx-font-size: 12px;");
        } else {
            updateStatusButton.setText("Cập nhật");
            updateStatusButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 4; -fx-padding: 4 12; -fx-cursor: hand; -fx-font-size: 12px;");
        }
    }

    private void setStatusStyle(String status) {
        switch (status) {
            case "Chờ xử lý":
                statusLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
                break;
            case "Đang xử lý":
                statusLabel.setStyle("-fx-text-fill: #2196f3; -fx-font-weight: bold;");
                break;
            case "Đã giải quyết":
                statusLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                break;
            default:
                statusLabel.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
                break;
        }
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Không có thông tin";
        }
        try {
            // Parse from format "2024-01-15 10:30:45" to "15/01/2024 (10:30)"
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dateString.replace(" ", "T"));
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy (HH:mm)"));
        } catch (Exception e) {
            return dateString; // Return original if parsing fails
        }
    }

    @FXML
    private void handleUpdateEquipmentStatus() {
        try {
            String newStatus;
            if ("Hoạt động".equals(currentEquipmentStatus)) {
                newStatus = "Bảo trì";
            } else if ("Bảo trì".equals(currentEquipmentStatus)) {
                newStatus = "Hoạt động";
            } else {
                // Default to "Hoạt động" if current status is unknown
                newStatus = "Hoạt động";
            }

            boolean success = roomEquipmentController.updateRoomEquipmentStatus(equipmentId, newStatus);

            if (success) {
                currentEquipmentStatus = newStatus;

                // Update display
                String equipmentCode = feedbackController.getEquipmentCodeById(equipmentId);
                if (equipmentCode != null) {
                    String updatedInfo = feedbackController.getEquipmentInfoByCode(equipmentCode);
                    if (updatedInfo != null) {
                        parseAndDisplayEquipmentInfo(updatedInfo);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái thiết bị: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendReply() {
        String replyContent = replyCommentArea.getText();

        // Validate input
        if (replyContent == null || replyContent.trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập nội dung trả lời!", Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean success = feedbackController.replyFeedback(
                    feedback.getId(),
                    replyContent.trim(),
                    currentUser.getUserId());

            if (success) {
                showAlert("Thành công", "Phản hồi đã được gửi thành công!", Alert.AlertType.INFORMATION);

                // Callback to refresh the feedback list
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }

                // Close dialog
                closeDialog();
            } else {
                showAlert("Lỗi", "Không thể gửi phản hồi. Vui lòng thử lại.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi phản hồi: " + e.getMessage());
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi gửi phản hồi: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) memberNameLabel.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(null);
        alert.getDialogPane().setPrefSize(400, 220);

        String bgColor = alertType == Alert.AlertType.ERROR ? "#fff5f5"
                : alertType == Alert.AlertType.WARNING ? "#fffbf0" : "#f5fff5";
        String borderColor = alertType == Alert.AlertType.ERROR ? "#f44336"
                : alertType == Alert.AlertType.WARNING ? "#ff9800" : "#4caf50";

        alert.getDialogPane().setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 16;");
        alert.showAndWait();
    }
}