package view.userView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import model.TrainingSchedule;
import model.ExerciseWithDetails;
import model.enums.enum_TrainingStatus;
import java.time.format.DateTimeFormatter;
import controller.TrainingController;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;

public class WorkoutsController {
    @FXML
    private Label pageTitle;
    @FXML
    private TableView<TrainingSchedule> scheduleTable;
    @FXML
    private TableColumn<TrainingSchedule, String> colDate;
    @FXML
    private TableColumn<TrainingSchedule, String> colTime;
    @FXML
    private TableColumn<TrainingSchedule, String> colTrainer;
    @FXML
    private TableColumn<TrainingSchedule, String> colRoom;
    @FXML
    private TableColumn<TrainingSchedule, String> colStatus;
    @FXML
    private TableColumn<TrainingSchedule, String> colNotes;
    @FXML
    private TableColumn<TrainingSchedule, Void> colAction;
    @FXML
    private TableColumn<TrainingSchedule, Void> colRating;

    private User currentUser;
    private TrainingController trainingController = new TrainingController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Chào mừng đến với trang theo dõi lịch tập của bạn!");
        setupTable();
        addButtonToTable();
        addRatingToTable();
        if (currentUser != null) {
            loadSchedulesByMember(currentUser.getUserId());
            trainingController.updateExpiredTrainingSchedules();
        }
    }

    private void setupTable() {
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate() != null)
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colTime.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTime() != null)
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTime());
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colTrainer.setCellValueFactory(cellData -> {
            String trainerName = cellData.getValue().getTrainerName();
            return new javafx.beans.property.SimpleStringProperty(trainerName != null ? trainerName : "Chưa có HLV");
        });
        colRoom.setCellValueFactory(cellData -> {
            String roomName = cellData.getValue().getRoomName();
            return new javafx.beans.property.SimpleStringProperty(roomName != null ? roomName : "Chưa có phòng");
        });
        colStatus.setCellValueFactory(cellData -> {
            String status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status != null ? status : "");
        });
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    private void loadSchedulesByMember(int userId) {
        ObservableList<TrainingSchedule> list = FXCollections.observableArrayList();
        list.addAll(trainingController.getSchedulesByUserId(userId));
        scheduleTable.setItems(list);
    }

    private void addButtonToTable() {
        Callback<TableColumn<TrainingSchedule, Void>, TableCell<TrainingSchedule, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TrainingSchedule, Void> call(final TableColumn<TrainingSchedule, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Chi tiết");
                    {
                        btn.setPrefWidth(200);
                        btn.setOnAction(event -> {
                            TrainingSchedule schedule = getTableView().getItems().get(getIndex());
                            showExercisesPopup(schedule.getId());
                        });
                        btn.setStyle(
                                "-fx-background-color: #4FC3F7; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    private void showExercisesPopup(int scheduleId) {
        TrainingController trainingController = new TrainingController();
        java.util.List<ExerciseWithDetails> exerciseDetails = trainingController.getExercisesByScheduleId(scheduleId);
        String scheduleStatus = trainingController.getTrainingScheduleStatus(scheduleId);

        Stage popupStage = new Stage();
        popupStage.setTitle("Danh sách bài tập của buổi tập");

        TableView<ExerciseWithDetails> exerciseTable = new TableView<>();

        // Cột tên bài tập
        TableColumn<ExerciseWithDetails, String> colName = new TableColumn<>("Tên bài tập");
        colName.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExerciseName()));
        colName.setPrefWidth(180);

        // Cột mã bài tập
        TableColumn<ExerciseWithDetails, String> colCode = new TableColumn<>("Mã bài tập");
        colCode.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExerciseCode()));
        colCode.setPrefWidth(100);

        // Cột loại
        TableColumn<ExerciseWithDetails, String> colCategory = new TableColumn<>("Loại");
        colCategory.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        colCategory.setPrefWidth(80);

        // Cột số lượng với định dạng "X reps x Y"
        TableColumn<ExerciseWithDetails, String> colQuantity = new TableColumn<>("Số lượng");
        colQuantity.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuantityFormatted()));
        colQuantity.setPrefWidth(120);

        // Cột ghi chú
        TableColumn<ExerciseWithDetails, String> colComment = new TableColumn<>("Ghi chú");
        colComment.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getComment()));
        colComment.setPrefWidth(200);

        // Cột mô tả
        TableColumn<ExerciseWithDetails, String> colDesc = new TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colDesc.setPrefWidth(220);

        exerciseTable.getColumns().addAll(colName, colCode, colCategory, colQuantity, colComment, colDesc);
        exerciseTable.setItems(javafx.collections.FXCollections.observableArrayList(exerciseDetails));
        exerciseTable.setPrefWidth(920);
        exerciseTable.setPrefHeight(400);

        // Tạo nút "Xem nhận xét"
        Button viewCommentsBtn = new Button("Xem nhận xét");
        viewCommentsBtn.setPrefWidth(150);
        viewCommentsBtn.setStyle(
                "-fx-background-color: #4FC3F7; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

        // Chỉ cho phép xem nhận xét khi trạng thái là "Hoàn thành"
        boolean isCompleted = "Hoàn thành".equals(scheduleStatus);
        viewCommentsBtn.setDisable(!isCompleted);

        if (!isCompleted) {
            viewCommentsBtn.setStyle(
                    "-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-font-weight: bold; -fx-background-radius: 8;");
        }

        viewCommentsBtn.setOnAction(e -> showCommentsPopup(exerciseDetails));

        VBox vbox = new VBox(exerciseTable, viewCommentsBtn);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 20;");
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void showCommentsPopup(java.util.List<ExerciseWithDetails> exerciseDetails) {
        Stage commentsStage = new Stage();
        commentsStage.setTitle("Nhận xét của huấn luyện viên");

        // Tạo ScrollPane chứa danh sách nhận xét
        ScrollPane scrollPane = new ScrollPane();
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(15);
        mainContainer.setStyle("-fx-padding: 20;");

        // Tạo tiêu đề
        Label titleLabel = new Label("NHẬN XÉT CỦA HUẤN LUYỆN VIÊN");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #232930; " +
                "-fx-font-family: 'Be Vietnam Pro', sans-serif; -fx-padding: 0 0 15 0;");

        mainContainer.getChildren().add(titleLabel);

        // Tạo card cho từng bài tập
        for (ExerciseWithDetails exercise : exerciseDetails) {
            VBox exerciseCard = new VBox();
            exerciseCard.setSpacing(10);
            exerciseCard.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-border-color: #e0e0e0; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 20; " +
                            "-fx-effect: dropshadow(gaussian, #181c20, 2, 0.1, 0, 1);");

            // Tên bài tập (header)
            Label exerciseNameLabel = new Label(exercise.getExerciseName());
            exerciseNameLabel.setStyle(
                    "-fx-font-size: 18px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: #4FC3F7; " +
                            "-fx-font-family: 'Be Vietnam Pro', sans-serif;");

            // Thông tin bài tập
            Label exerciseInfoLabel = new Label(
                    String.format("Mã: %s | Loại: %s | Số lượng: %s",
                            exercise.getExerciseCode(),
                            exercise.getCategory(),
                            exercise.getQuantityFormatted()));
            exerciseInfoLabel.setStyle(
                    "-fx-font-size: 14px; " +
                            "-fx-text-fill: #666666; " +
                            "-fx-font-family: 'Be Vietnam Pro', sans-serif;");

            // Container cho ghi chú
            VBox commentsContainer = new VBox();
            commentsContainer.setSpacing(8);

            // Ghi chú của hội viên
            if (exercise.getComment() != null && !exercise.getComment().trim().isEmpty()) {
                Label memberCommentTitle = new Label("📝 Ghi chú:");
                memberCommentTitle.setStyle(
                        "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-text-fill: #232930; " +
                                "-fx-font-family: 'Be Vietnam Pro', sans-serif;");

                Label memberCommentContent = new Label(exercise.getComment());
                memberCommentContent.setStyle(
                        "-fx-font-size: 14px; " +
                                "-fx-text-fill: #555555; " +
                                "-fx-font-family: 'Be Vietnam Pro', sans-serif; " +
                                "-fx-wrap-text: true; " +
                                "-fx-background-color: #f8f9fa; " +
                                "-fx-padding: 10; " +
                                "-fx-background-radius: 5px;");
                memberCommentContent.setMaxWidth(Double.MAX_VALUE);

                commentsContainer.getChildren().addAll(memberCommentTitle, memberCommentContent);
            }

            // Nhận xét của huấn luyện viên
            Label trainerCommentTitle = new Label("💬 Nhận xét từ huấn luyện viên:");
            trainerCommentTitle.setStyle(
                    "-fx-font-size: 14px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: #232930; " +
                            "-fx-font-family: 'Be Vietnam Pro', sans-serif;");

            String trainerCommentText = (exercise.getTrainerComment() != null
                    && !exercise.getTrainerComment().trim().isEmpty())
                            ? exercise.getTrainerComment()
                            : "Chưa có nhận xét từ huấn luyện viên";

            Label trainerCommentContent = new Label(trainerCommentText);
            String commentStyle = (exercise.getTrainerComment() != null
                    && !exercise.getTrainerComment().trim().isEmpty())
                            ? "-fx-font-size: 14px; " +
                                    "-fx-text-fill: #333333; " +
                                    "-fx-font-family: 'Be Vietnam Pro', sans-serif; " +
                                    "-fx-wrap-text: true; " +
                                    "-fx-background-color: #e8f4fd; " +
                                    "-fx-padding: 10; " +
                                    "-fx-background-radius: 5px;"
                            : "-fx-font-size: 14px; " +
                                    "-fx-text-fill: #999999; " +
                                    "-fx-font-family: 'Be Vietnam Pro', sans-serif; " +
                                    "-fx-font-style: italic; " +
                                    "-fx-wrap-text: true; " +
                                    "-fx-background-color: #f5f5f5; " +
                                    "-fx-padding: 10; " +
                                    "-fx-background-radius: 5px;";

            trainerCommentContent.setStyle(commentStyle);
            trainerCommentContent.setMaxWidth(Double.MAX_VALUE);

            commentsContainer.getChildren().addAll(trainerCommentTitle, trainerCommentContent);

            // Thêm các thành phần vào card
            exerciseCard.getChildren().addAll(exerciseNameLabel, exerciseInfoLabel, commentsContainer);

            // Thêm card vào container chính
            mainContainer.getChildren().add(exerciseCard);
        }

        // Cấu hình ScrollPane
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f5f6;");

        // Tạo Scene và hiển thị
        Scene commentsScene = new Scene(scrollPane, 700, 500);
        commentsStage.setScene(commentsScene);
        commentsStage.setResizable(true);
        commentsStage.show();
    }

    private void addRatingToTable() {
        Callback<TableColumn<TrainingSchedule, Void>, TableCell<TrainingSchedule, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TrainingSchedule, Void> call(final TableColumn<TrainingSchedule, Void> param) {
                return new TableCell<>() {
                    private final HBox ratingContainer = new HBox();
                    private final Button[] starButtons = new Button[5];
                    private final Button confirmButton = new Button("✓");
                    private int selectedRating = 0;

                    {
                        ratingContainer.setSpacing(3);
                        ratingContainer.setAlignment(Pos.CENTER);

                        // Tạo 5 nút sao
                        for (int i = 0; i < 5; i++) {
                            final int starIndex = i + 1;
                            starButtons[i] = new Button("☆");
                            starButtons[i].setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-border-color: transparent; " +
                                            "-fx-text-fill: #cccccc; " +
                                            "-fx-font-size: 16px; " +
                                            "-fx-cursor: hand; " +
                                            "-fx-padding: 2 2 2 2;");

                            starButtons[i].setOnAction(event -> {
                                selectRating(starIndex);
                            });

                            starButtons[i].setOnMouseEntered(event -> {
                                hoverRating(starIndex);
                            });

                            starButtons[i].setOnMouseExited(event -> {
                                resetHover();
                            });
                        }

                        // Nút xác nhận
                        confirmButton.setStyle(
                                "-fx-background-color: #4FC3F7; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-background-radius: 3; " +
                                        "-fx-cursor: hand; " +
                                        "-fx-font-size: 12px; " +
                                        "-fx-padding: 3 6 3 6;");

                        confirmButton.setOnAction(event -> {
                            TrainingSchedule schedule = getTableView().getItems().get(getIndex());
                            if (selectedRating > 0) {
                                submitRating(schedule.getId(), selectedRating);
                            }
                        });

                        ratingContainer.getChildren().addAll(starButtons);
                        ratingContainer.getChildren().add(confirmButton);
                    }

                    private void selectRating(int rating) {
                        selectedRating = rating;
                        updateStarDisplay(rating);
                    }

                    private void hoverRating(int rating) {
                        updateStarDisplay(rating);
                    }

                    private void resetHover() {
                        updateStarDisplay(selectedRating);
                    }

                    private void updateStarDisplay(int rating) {
                        for (int i = 0; i < 5; i++) {
                            if (i < rating) {
                                starButtons[i].setText("★");
                                starButtons[i].setStyle(
                                        "-fx-background-color: transparent; " +
                                                "-fx-border-color: transparent; " +
                                                "-fx-text-fill: #FFD700; " +
                                                "-fx-font-size: 16px; " +
                                                "-fx-cursor: hand; " +
                                                "-fx-padding: 2 2 2 2;");
                            } else {
                                starButtons[i].setText("☆");
                                starButtons[i].setStyle(
                                        "-fx-background-color: transparent; " +
                                                "-fx-border-color: transparent; " +
                                                "-fx-text-fill: #cccccc; " +
                                                "-fx-font-size: 16px; " +
                                                "-fx-cursor: hand; " +
                                                "-fx-padding: 2 2 2 2;");
                            }
                        }
                    }

                    private void submitRating(int scheduleId, int rating) {
                        TrainingController controller = new TrainingController();
                        if (controller.updateTrainingScheduleRating(scheduleId, rating)) {
                            // Thành công - disable rating controls
                            for (Button star : starButtons) {
                                star.setDisable(true);
                            }
                            confirmButton.setDisable(true);
                            confirmButton.setText("✓");
                            confirmButton.setStyle(
                                    "-fx-background-color: #28a745; " +
                                            "-fx-text-fill: white; " +
                                            "-fx-font-weight: bold; " +
                                            "-fx-background-radius: 3; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 3 6 3 6;");

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Thành công");
                            alert.setHeaderText(null);
                            alert.setContentText("Đánh giá của bạn đã được gửi thành công!");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Lỗi");
                            alert.setHeaderText(null);
                            alert.setContentText("Không thể gửi đánh giá. Vui lòng thử lại!");
                            alert.showAndWait();
                        }
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            TrainingSchedule schedule = getTableView().getItems().get(getIndex());
                            // Kiểm tra xem buổi tập đã hoàn thành chưa và có trainer không
                            boolean isCompleted = "Hoàn thành".equals(schedule.getStatus());
                            boolean hasTrainer = schedule.getTrainerId() > 0;

                            if (isCompleted && hasTrainer) {
                                // Kiểm tra xem đã đánh giá chưa
                                int existingRating = trainingController.getTrainingScheduleRating(schedule.getId());
                                if (existingRating > 0) {
                                    // Đã đánh giá - hiển thị rating và disable controls
                                    selectedRating = existingRating;
                                    updateStarDisplay(existingRating);
                                    for (Button star : starButtons) {
                                        star.setDisable(true);
                                    }
                                    confirmButton.setDisable(true);
                                    confirmButton.setText("✓");
                                    confirmButton.setStyle(
                                            "-fx-background-color: #28a745; " +
                                                    "-fx-text-fill: white; " +
                                                    "-fx-font-weight: bold; " +
                                                    "-fx-background-radius: 3; " +
                                                    "-fx-font-size: 12px; " +
                                                    "-fx-padding: 3 6 3 6;");
                                } else {
                                    // Chưa đánh giá - enable controls
                                    for (Button star : starButtons) {
                                        star.setDisable(false);
                                    }
                                    confirmButton.setDisable(false);
                                    selectedRating = 0;
                                    updateStarDisplay(0);
                                }
                                setGraphic(ratingContainer);
                            } else {
                                // Không thể đánh giá
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        };
        colRating.setCellFactory(cellFactory);
    }
}