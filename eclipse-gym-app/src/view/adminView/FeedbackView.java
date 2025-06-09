package view.adminView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Feedback;
import model.User;
import view.userView.FeedbackFormController;

import java.io.IOException;
import java.util.List;

import controller.FeedbackController;

public class FeedbackView {
    @FXML
    private Label pageTitle;
    @FXML
    private TableView<Feedback> feedbackTable;
    @FXML
    private TableColumn<Feedback, String> memberName;
    @FXML
    private TableColumn<Feedback, String> typeColumn;
    @FXML
    private TableColumn<Feedback, String> commentColumn;
    @FXML
    private TableColumn<Feedback, String> statusColumn;
    @FXML
    private TableColumn<Feedback, String> dateColumn;
    @FXML
    private TableColumn<Feedback, Void> actionColumn;

    private User currentUser;
    private final ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();
    private final FeedbackController feedbackController = new FeedbackController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadFeedbacks();
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Danh sách các phản hồi từ hội viên");

        memberName.setCellValueFactory(data -> data.getValue().memberNameProperty());
        typeColumn.setCellValueFactory(data -> data.getValue().feedbackTypeProperty());
        commentColumn.setCellValueFactory(data -> data.getValue().commentProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().feedbackDateProperty());
        actionColumn.setCellFactory(data -> new TableCell<Feedback, Void>() {
            private final Button replyButton = new Button("Trả lời");
            private final HBox hbox = new HBox(replyButton);
            {
                hbox.setAlignment(javafx.geometry.Pos.CENTER); // căn giữa nút trong cell
                replyButton.setStyle(
                    "-fx-background-color: #2196f3;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 6 16 6 16;" +
                    "-fx-cursor: hand;"
                );
                replyButton.setOnAction(e -> {
                    Feedback feedback = getTableView().getItems().get(getIndex());

                    // Kiểm tra đã phản hồi chưa
                    if (feedback.responseCommentProperty().get() != null &&
                        !feedback.responseCommentProperty().get().isEmpty()) {
                        showAlert("Thông báo", "Phản hồi này đã được xử lý.");
                        return;
                    }

                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Trả lời phản hồi");
                    dialog.setHeaderText("Nhập trả lời của bạn:");

                    TextArea inputArea = new TextArea();
                    inputArea.setPromptText("Nhập nội dung trả lời phản hồi...");
                    inputArea.setPrefRowCount(4);
                    inputArea.setPrefColumnCount(20);
                    inputArea.setWrapText(true);
                    inputArea.setStyle("-fx-font-size: 14px;");
                    inputArea.setMaxWidth(Double.MAX_VALUE); // Cho phép rộng tối đa

                    HBox contentBox = new HBox(inputArea);
                    contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    HBox.setHgrow(inputArea, javafx.scene.layout.Priority.ALWAYS); // Cho phép inputArea giãn hết HBox
                    dialog.getDialogPane().setContent(contentBox);

                    // Ẩn label mặc định
                    dialog.setContentText(null);
                    dialog.setGraphic(null);
                    // CSS cho dialog
                    dialog.getDialogPane().setStyle(
                        "-fx-background-color: #f5f5f5;" + // màu nền
                        "-fx-border-color: #2196f3;" + // màu viền
                        "-fx-border-width: 2;" + // độ dày viền
                        "-fx-border-radius: 8;" + // bo góc viền
                        "-fx-padding: 10;" // khoảng cách bên trong
                    );
                    dialog.getDialogPane().setPrefSize(400, 300);

                    // CSS cho các nút trong dialog
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle(
                        "-fx-background-color: #4caf50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 6 16 6 16;" +
                        "-fx-cursor: hand;"
                    );

                    dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
                        "-fx-background-color: #f44336;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 6 16 6 16;" +
                        "-fx-cursor: hand;"
                    );

                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(replyButton.getScene().getWindow());

                    dialog.showAndWait().ifPresent(response -> {
                        String text = inputArea.getText();
                        if (text.trim().isEmpty()) {
                            showAlert("Lỗi", "Nội dung phản hồi không được để trống.");
                            return;
                        }

                        boolean success = feedbackController.replyFeedback(
                            feedback.getId(),
                            text,
                            currentUser.getUserId()
                        );

                        if (success) {
                            showSuccess("Thành công", "Phản hồi đã được gửi.");
                            loadFeedbacks();
                            feedbackTable.setItems(feedbackList);
                        } else {
                            showAlert("Lỗi", "Không thể gửi phản hồi.");
                        }
                    });
                });
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
        
        feedbackTable.setItems(feedbackList);

    }


    private void loadFeedbacks() {
        if (currentUser == null) return;
        
        try {
            // Lấy feedback từ database
            List<Feedback> feedbacks = feedbackController.getAllFeedbacks();
            feedbackList.clear();
            feedbackList.addAll(feedbacks);
            
            System.out.println("Đã load " + feedbacks.size() + " feedback từ database");
            
        } catch (Exception e) {
            System.err.println("Lỗi khi load feedback: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: hiển thị danh sách trống
            feedbackList.clear();
        }
        // feedbackTable.setItems(feedbackList);
    }
    

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(null);
        alert.getDialogPane().setPrefSize(400, 220);
        alert.getDialogPane().setStyle(
            "-fx-background-color: #fff5f5;" +      // nền đỏ nhạt
            "-fx-border-color: #f44336;" +         // viền đỏ
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 16;"
        );
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(null);
        alert.getDialogPane().setPrefSize(400, 220);
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f5fff5;" +      // nền xanh nhạt
            "-fx-border-color: #4caf50;" +         // viền xanh
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 15px;" +
            "-fx-padding: 16;"
        );
        alert.showAndWait();
    }
}
