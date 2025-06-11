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
import view.adminView.FeedbackReplyController;

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
        dateColumn.setCellValueFactory(data -> {
            String originalDate = data.getValue().feedbackDateProperty().get();
            if (originalDate != null && !originalDate.isEmpty()) {
                try {
                    // Chuyển đổi từ định dạng gốc sang dd/MM/yyyy (HH:mm)
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(originalDate.replace(" ", "T"));
                    String formattedDate = dateTime
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy (HH:mm)"));
                    return new javafx.beans.property.SimpleStringProperty(formattedDate);
                } catch (Exception e) {
                    // Nếu không parse được, trả về nguyên bản
                    return new javafx.beans.property.SimpleStringProperty(originalDate);
                }
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
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
                                "-fx-cursor: hand;");
                replyButton.setOnAction(e -> {
                    Feedback feedback = getTableView().getItems().get(getIndex());

                    // Mở dialog trả lời feedback
                    openFeedbackReplyDialog(feedback);
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

    private void openFeedbackReplyDialog(Feedback feedback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("feedbackReplyDialog.fxml"));
            javafx.scene.layout.VBox dialogRoot = loader.load();

            FeedbackReplyController controller = loader.getController();
            controller.setData(feedback, currentUser, () -> {
                loadFeedbacks();
                feedbackTable.refresh();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Trả lời phản hồi - " + feedback.getMemberName());
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(feedbackTable.getScene().getWindow());
            dialogStage.setResizable(false);

            // Style dialog
            dialogStage.getScene().getStylesheets().add(
                    getClass().getResource("/view/userView/feedback.css").toExternalForm());

            dialogStage.showAndWait();

        } catch (IOException ex) {
            System.err.println("Lỗi khi mở dialog trả lời feedback: " + ex.getMessage());
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể mở form trả lời feedback.");
        }
    }

    private void loadFeedbacks() {
        if (currentUser == null)
            return;

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
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setGraphic(null);
        alert.getDialogPane().setPrefSize(400, 220);
        alert.getDialogPane().setStyle(
                "-fx-background-color: #fff5f5;" + // nền đỏ nhạt
                        "-fx-border-color: #f44336;" + // viền đỏ
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 16;");
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
                "-fx-background-color: #f5fff5;" + // nền xanh nhạt
                        "-fx-border-color: #4caf50;" + // viền xanh
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 15px;" +
                        "-fx-padding: 16;");
        alert.showAndWait();
    }
}
