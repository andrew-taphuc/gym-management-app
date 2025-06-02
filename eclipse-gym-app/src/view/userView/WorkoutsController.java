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
import model.enums.enum_TrainingStatus;
import java.time.format.DateTimeFormatter;
import controller.TrainingController;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import model.Exercise;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

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

    private User currentUser;
    private TrainingController trainingController = new TrainingController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Chào mừng đến với trang theo dõi lịch tập của bạn!");
        setupTable();
        if (currentUser != null) {
            loadSchedulesByMember(currentUser.getUserId());
        }
        addButtonToTable();
    }

    private void setupTable() {
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getScheduleDate() != null)
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getScheduleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colTime.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartTime() != null)
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStartTime().toString());
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colTrainer.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Huấn luyện viên A")); // Placeholder
        colRoom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Phòng 1")); // Placeholder
        colStatus.setCellValueFactory(cellData -> {
            enum_TrainingStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status != null ? status.getValue() : "");
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
                    private final Button btn = new Button("Xem bài tập");
                    {
                        btn.setPrefWidth(200);
                        btn.setOnAction(event -> {
                            TrainingSchedule schedule = getTableView().getItems().get(getIndex());
                            showExercisesPopup(schedule.getScheduleId());
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
        java.util.List<Exercise> exercises = trainingController.getExercisesByScheduleId(scheduleId);
        Stage popupStage = new Stage();
        popupStage.setTitle("Danh sách bài tập của buổi tập");

        TableView<Exercise> exerciseTable = new TableView<>();
        TableColumn<Exercise, String> colName = new TableColumn<>("Tên bài tập");
        colName.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExerciseName()));
        colName.setPrefWidth(200);
        TableColumn<Exercise, String> colCode = new TableColumn<>("Mã bài tập");
        colCode.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExerciseCode()));
        colCode.setPrefWidth(120);
        TableColumn<Exercise, String> colCategory = new TableColumn<>("Loại");
        colCategory.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        colCategory.setPrefWidth(120);
        TableColumn<Exercise, String> colDesc = new TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colDesc.setPrefWidth(250);
        exerciseTable.getColumns().addAll(colName, colCode, colCategory, colDesc);
        exerciseTable.setItems(javafx.collections.FXCollections.observableArrayList(exercises));
        exerciseTable.setPrefWidth(700);
        exerciseTable.setPrefHeight(400);

        VBox vbox = new VBox(exerciseTable);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.show();
    }
}