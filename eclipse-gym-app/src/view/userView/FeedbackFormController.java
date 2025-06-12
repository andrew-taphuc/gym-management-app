package view.userView;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;
import controller.FeedbackController;
import controller.EquipmentController;
import controller.RoomEquipmentController;
import controller.MemberController;

import java.util.function.Consumer;

public class FeedbackFormController {

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private VBox equipmentSection;
    @FXML
    private TextField equipmentCodeField;
    @FXML
    private Label equipmentStatusLabel;
    @FXML
    private TextArea commentTextArea;

    private User user;
    private Consumer<Void> feedbackSubmittedCallback;
    private FeedbackController feedbackController;
    private EquipmentController equipmentController;
    private RoomEquipmentController roomEquipmentController;
    private MemberController memberController;

    @FXML
    public void initialize() {
        // Thiết lập các options cho ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(
                "Cơ sở vật chất",
                "Nhân viên",
                "Khác"));

        // Lắng nghe thay đổi loại feedback
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            handleFeedbackTypeChange(newVal);
        });

        // Lắng nghe thay đổi Equipment Code
        equipmentCodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleEquipmentCodeChange(newVal);
        });
    }

    private void handleFeedbackTypeChange(String feedbackType) {
        if ("Cơ sở vật chất".equals(feedbackType)) {
            // Hiển thị phần nhập Equipment Code
            equipmentSection.setVisible(true);
            equipmentSection.setManaged(true);
            equipmentStatusLabel.setText("Vui lòng nhập mã thiết bị");
        } else {
            // Ẩn phần nhập Equipment Code
            equipmentSection.setVisible(false);
            equipmentSection.setManaged(false);
            equipmentCodeField.clear();
            equipmentStatusLabel.setText("");
        }
    }

    private void handleEquipmentCodeChange(String equipmentCodeText) {
        if (equipmentCodeText == null || equipmentCodeText.trim().isEmpty()) {
            equipmentStatusLabel.setText("Vui lòng nhập mã thiết bị");
            equipmentStatusLabel.setStyle("-fx-text-fill: #666;");
            return;
        }

        String equipmentCode = equipmentCodeText.trim();

        // Kiểm tra mã tồn tại
        boolean exists = roomEquipmentController.isEquipmentCodeExists(equipmentCode);

        if (exists) {
            String equipmentInfo = equipmentController.getEquipmentInfoByCode(equipmentCode);
            equipmentStatusLabel.setText("✓ Thiết bị: " + equipmentInfo);
            equipmentStatusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            equipmentStatusLabel.setText("✗ Mã thiết bị không tồn tại");
            equipmentStatusLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFeedbackSubmittedCallback(Consumer<Void> callback) {
        this.feedbackSubmittedCallback = callback;
    }

    public void setFeedbackController(FeedbackController controller) {
        this.feedbackController = controller;
        this.equipmentController = new EquipmentController();
        this.roomEquipmentController = new RoomEquipmentController();
        this.memberController = new MemberController();
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

        // Validate Equipment Code nếu là feedback về cơ sở vật chất
        Integer equipmentId = null;
        if ("Cơ sở vật chất".equals(type)) {
            String equipmentCode = equipmentCodeField.getText();

            if (equipmentCode == null || equipmentCode.trim().isEmpty()) {
                showAlert("Lỗi", "Vui lòng nhập mã thiết bị cho feedback về cơ sở vật chất!");
                return;
            }

            try {
                // Kiểm tra mã tồn tại
                if (!roomEquipmentController.isEquipmentCodeExists(equipmentCode)) {
                    showAlert("Lỗi", "Mã thiết bị không tồn tại trong hệ thống!");
                    return;
                }

                // Lấy EquipmentID từ EquipmentCode
                var equipment = roomEquipmentController.getRoomEquipmentByCode(equipmentCode.trim());
                if (equipment == null) {
                    showAlert("Lỗi", "Không thể lấy ID thiết bị từ mã!");
                    return;
                }
                equipmentId = equipment.getRoomEquipmentId();

            } catch (Exception e) {
                showAlert("Lỗi", "Có lỗi xảy ra khi kiểm tra mã thiết bị: " + e.getMessage());
                return;
            }
        }

        try {
            // Lưu feedback vào database
            Integer memberId = memberController.getMemberIdByUserId(user.getUserId());
            if (memberId == null) {
                showAlert("Lỗi", "Không tìm thấy thông tin hội viên!");
                return;
            }
            
            boolean success = feedbackController.insertFeedback(memberId, type, comment.trim(), equipmentId);

            if (success) {
                String equipmentInfo = equipmentId != null ? " (Thiết bị ID: " + equipmentId + ")" : "";
                System.out.printf("Feedback đã lưu vào DB: [%s] %s%s\n", type, comment.trim(), equipmentInfo);

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