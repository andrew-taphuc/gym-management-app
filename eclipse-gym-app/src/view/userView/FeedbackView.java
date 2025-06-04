package view.userView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Feedback;
import model.User;

import java.io.IOException;
import java.util.List;

import controller.FeedbackController;

public class FeedbackView {

    @FXML
    private Label pageTitle;
    @FXML
    private TableView<Feedback> feedbackTable;
    @FXML
    private TableColumn<Feedback, String> typeColumn;
    @FXML
    private TableColumn<Feedback, String> commentColumn;
    @FXML
    private TableColumn<Feedback, String> statusColumn;
    @FXML
    private TableColumn<Feedback, String> dateColumn;
    @FXML
    private TableColumn<Feedback, Void> responseCommentColumn;
    @FXML
    private Button addFeedbackButton;

    private User currentUser;
    private final ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();
    private final FeedbackController feedbackController = new FeedbackController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadFeedbacks();
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Danh sách Feedback của bạn");

        typeColumn.setCellValueFactory(data -> data.getValue().feedbackTypeProperty());
        commentColumn.setCellValueFactory(data -> data.getValue().commentProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().feedbackDateProperty());
        responseCommentColumn.setCellFactory(data -> new TableCell<Feedback, Void>() {
            private final Button btn = new Button("Chi tiết");

            {
                btn.setPrefWidth(200);
                btn.setOnAction(event -> {
                    Feedback feedback = getTableView().getItems().get(getIndex());

                    // Lấy nội dung phản hồi
                    String response = feedback.responseCommentProperty().get();

                    // Hiển thị trong Alert (popup)
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Chi tiết phản hồi");
                    alert.setHeaderText("Phản hồi từ chủ phòng tập");
                    alert.setContentText(response != null && !response.isEmpty() ? response : "Không có phản hồi.");
                    alert.showAndWait();
                });
                btn.setStyle("-fx-background-color: #4FC3F7; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });


        feedbackTable.setItems(feedbackList);
    }

    private void loadFeedbacks() {
        if (currentUser == null) return;
        
        try {
            // Lấy feedback từ database
            int MemberId = feedbackController.getMemberIdByUserId(currentUser.getUserId());
            List<Feedback> feedbacks = feedbackController.getFeedbacksByMemberID(MemberId);
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
    @FXML
    private void handleAddFeedback() {
        try {
            // File FXML cùng thư mục với Controller
            String fxmlPath = "feedbackForm.fxml";
            
            // Debug: In ra đường dẫn để kiểm tra
            System.out.println("Trying to load FXML from same package: " + fxmlPath);
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            
            if (loader.getLocation() == null) {
                showAlert("Lỗi", "Không tìm thấy file feedbackForm.fxml tại đường dẫn: " + fxmlPath);
                return;
            }

            Stage formStage = new Stage();
            Scene scene = new Scene(loader.load());
            formStage.setScene(scene);
            formStage.setTitle("Thêm Feedback");
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setResizable(false);

            FeedbackFormController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setFeedbackController(feedbackController);
            controller.setFeedbackSubmittedCallback(v -> loadFeedbacks()); // Reload từ DB

            formStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Lỗi khi mở form feedback: " + e.getMessage());
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở form thêm feedback. Lỗi: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi không xác định xảy ra: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}