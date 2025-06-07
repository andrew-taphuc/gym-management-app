package view.trainerView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import model.TrainingSchedule;
import model.TrainingRegistration;
import model.Member;
import model.Trainer;
import model.ExerciseWithDetails;
import model.enums.enum_TrainingStatus;
import java.time.format.DateTimeFormatter;
import controller.TrainingController;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import controller.TrainerController;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import model.Room;
import model.Exercise;
import controller.RoomController;
import controller.ExerciseController;

public class WorkoutsController {
    @FXML
    private Label pageTitle;

    // Bảng lịch dạy
    @FXML
    private TableView<TrainingSchedule> scheduleTable;
    @FXML
    private TableColumn<TrainingSchedule, String> colDate;
    @FXML
    private TableColumn<TrainingSchedule, String> colTime;
    @FXML
    private TableColumn<TrainingSchedule, String> colMember;
    @FXML
    private TableColumn<TrainingSchedule, String> colRoom;
    @FXML
    private TableColumn<TrainingSchedule, String> colStatus;
    @FXML
    private TableColumn<TrainingSchedule, String> colNotes;
    @FXML
    private TableColumn<TrainingSchedule, Void> colAction;

    // Bảng hội viên đang quản lý
    @FXML
    private TableView<TrainingRegistration> managedMembersTable;
    @FXML
    private TableColumn<TrainingRegistration, String> colMemberCode;
    @FXML
    private TableColumn<TrainingRegistration, String> colMemberName;
    @FXML
    private TableColumn<TrainingRegistration, String> colMemberPlan;
    @FXML
    private TableColumn<TrainingRegistration, String> colStartDate;
    @FXML
    private TableColumn<TrainingRegistration, String> colSessionsLeft;
    @FXML
    private TableColumn<TrainingRegistration, Void> colMemberAction;

    // Controls
    @FXML
    private TextField registrationIdField;
    @FXML
    private Button addByIdButton;
    @FXML
    private Button showUnassignedButton;
    @FXML
    private Button refreshMembersButton;

    @FXML
    private ComboBox<Room> roomComboBox;

    private User currentUser;
    private TrainingController trainingController = new TrainingController();
    private RoomController roomController = new RoomController();
    private ExerciseController exerciseController = new ExerciseController();
    private int currentTrainerId;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Chào mừng đến với trang quản lý lịch tập!");
        setupScheduleTable();
        setupManagedMembersTable();
        addButtonToScheduleTable();
        addButtonToMembersTable();

        if (currentUser != null) {
            // Lấy trainerId từ userId
            TrainerController trainerController = new TrainerController();
            currentTrainerId = trainerController.getTrainerIdByUserId(currentUser.getUserId());
            System.out.println("[DEBUG] userId: " + currentUser.getUserId() + ", trainerId: " + currentTrainerId);
            loadSchedulesByTrainer(currentTrainerId);
            loadManagedMembers(currentTrainerId);
        }
    }

    private void setupScheduleTable() {
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
        colMember.setCellValueFactory(cellData -> {
            String memberName = cellData.getValue().getMemberName();
            return new javafx.beans.property.SimpleStringProperty(memberName != null ? memberName : "Chưa có học viên");
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

    private void setupManagedMembersTable() {
        colMemberCode.setCellValueFactory(cellData -> {
            String memberCode = cellData.getValue().getMember() != null
                    ? cellData.getValue().getMember().getMemberCode()
                    : "";
            return new javafx.beans.property.SimpleStringProperty(memberCode);
        });

        colMemberName.setCellValueFactory(cellData -> {
            String memberName = cellData.getValue().getMember() != null
                    && cellData.getValue().getMember().getUser() != null
                            ? cellData.getValue().getMember().getUser().getFullName()
                            : "";
            return new javafx.beans.property.SimpleStringProperty(memberName);
        });

        colMemberPlan.setCellValueFactory(cellData -> {
            String planName = cellData.getValue().getPlan() != null ? cellData.getValue().getPlan().getPlanName() : "";
            return new javafx.beans.property.SimpleStringProperty(planName);
        });

        colStartDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null)
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colSessionsLeft.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cellData.getValue().getSessionsLeft()));
        });
    }

    private void loadSchedulesByTrainer(int trainerId) {
        ObservableList<TrainingSchedule> list = FXCollections.observableArrayList();
        list.addAll(trainingController.getSchedulesByTrainerId(trainerId));
        // Log kiểm tra dữ liệu
        System.out.println("==== DANH SÁCH LỊCH TẬP CỦA HLV ====");
        for (TrainingSchedule ts : list) {
            System.out.println("ScheduleID: " + ts.getId() + ", Member: " + ts.getMemberName() + ", TrainerID: "
                    + ts.getTrainerId() + ", Date: " + ts.getDate());
        }
        System.out.println("==== TỔNG SỐ: " + list.size() + " lịch ====");
        scheduleTable.setItems(list);
        scheduleTable.refresh();
    }

    private void loadManagedMembers(int trainerId) {
        ObservableList<TrainingRegistration> list = FXCollections.observableArrayList();

        // Lấy thông tin trainer để biết specialization
        Trainer trainer = trainingController.getTrainerById(trainerId);
        if (trainer != null) {
            // Lấy danh sách hội viên đang quản lý thông qua TrainingRegistrations
            java.util.List<Member> members = trainingController.getMembersByTrainerId(trainerId);

            // Tạo TrainingRegistration objects từ members (cần query riêng để lấy đầy đủ
            // thông tin)
            String sql = "SELECT tr.*, tp.planname, tp.type " +
                    "FROM TrainingRegistrations tr " +
                    "JOIN TrainingPlans tp ON tr.planid = tp.planid " +
                    "WHERE tr.trainerid = ? " +
                    "ORDER BY tr.startdate DESC";

            try (java.sql.Connection conn = utils.DBConnection.getConnection();
                    java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, trainerId);
                java.sql.ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    TrainingRegistration registration = new TrainingRegistration();
                    registration.setRegistrationId(rs.getInt("registrationid"));
                    registration.setMemberId(rs.getInt("memberid"));
                    registration.setPlanId(rs.getInt("planid"));
                    registration.setTrainerId(rs.getInt("trainerid"));
                    if (rs.getDate("startdate") != null) {
                        registration.setStartDate(rs.getDate("startdate").toLocalDate());
                    }
                    registration.setSessionsLeft(rs.getInt("sessionsleft"));
                    registration.setPaymentId(rs.getInt("paymentid"));

                    // Tìm member tương ứng
                    final int memberIdFromRS = rs.getInt("memberid");
                    Member member = members.stream()
                            .filter(m -> m.getMemberId() == memberIdFromRS)
                            .findFirst()
                            .orElse(null);

                    if (member != null) {
                        // Tạo TrainingPlan object
                        model.TrainingPlan plan = new model.TrainingPlan();
                        plan.setPlanId(rs.getInt("planid"));
                        plan.setPlanName(rs.getString("planname"));
                        plan.setType(model.enums.enum_TrainerSpecialization.fromValue(rs.getString("type")));

                        registration.setMember(member);
                        registration.setPlan(plan);

                        list.add(registration);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        managedMembersTable.setItems(list);
        managedMembersTable.refresh();
    }

    private void addButtonToScheduleTable() {
        Callback<TableColumn<TrainingSchedule, Void>, TableCell<TrainingSchedule, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TrainingSchedule, Void> call(final TableColumn<TrainingSchedule, Void> param) {
                return new TableCell<>() {
                    private final Button viewBtn = new Button("Xem bài tập");

                    {
                        viewBtn.setPrefWidth(120);

                        viewBtn.setOnAction(event -> {
                            TrainingSchedule schedule = getTableView().getItems().get(getIndex());
                            showExercisesPopup(schedule);
                        });

                        viewBtn.setStyle(
                                "-fx-background-color: #4FC3F7; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(viewBtn);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    private void addButtonToMembersTable() {
        Callback<TableColumn<TrainingRegistration, Void>, TableCell<TrainingRegistration, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TrainingRegistration, Void> call(final TableColumn<TrainingRegistration, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Xem chi tiết");
                    {
                        btn.setPrefWidth(120);
                        btn.setOnAction(event -> {
                            TrainingRegistration registration = getTableView().getItems().get(getIndex());
                            showMemberDetailsPopup(registration);
                        });
                        btn.setStyle(
                                "-fx-background-color: #81C784; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
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
        colMemberAction.setCellFactory(cellFactory);
    }

    @FXML
    private void addMemberById() {
        String registrationIdStr = registrationIdField.getText().trim();
        if (registrationIdStr.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập Registration ID!");
            return;
        }

        try {
            int registrationId = Integer.parseInt(registrationIdStr);
            boolean success = trainingController.assignMemberToTrainerByRegistrationId(registrationId,
                    currentTrainerId);

            if (success) {
                showAlert("Thành công", "Đã thêm hội viên vào danh sách quản lý!");
                registrationIdField.clear();
                refreshMembersList();
            } else {
                showAlert("Thất bại",
                        "Không thể thêm hội viên. Kiểm tra lại Registration ID hoặc hội viên đã được phân công!");
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Registration ID phải là số!");
        }
    }

    @FXML
    private void showUnassignedMembers() {
        // Lấy thông tin trainer để biết specialization
        Trainer trainer = trainingController.getTrainerById(currentTrainerId);
        if (trainer == null) {
            showAlert("Lỗi", "Không thể lấy thông tin trainer!");
            return;
        }

        java.util.List<TrainingRegistration> unassignedList = trainingController
                .getUnassignedTrainingRegistrationsBySpecialization(trainer.getSpecialization());

        if (unassignedList.isEmpty()) {
            showAlert("Thông báo", "Không có hội viên nào chưa được phân công với chuyên môn "
                    + trainer.getSpecialization().getValue());
            return;
        }

        showUnassignedMembersPopup(unassignedList);
    }

    @FXML
    private void refreshMembersList() {
        loadManagedMembers(currentTrainerId);
    }

    private void showMemberDetailsPopup(TrainingRegistration registration) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Chi tiết hội viên");

        VBox vbox = new VBox(15);
        vbox.setStyle("-fx-padding: 20;");

        Label memberInfo = new Label("Thông tin hội viên:");
        memberInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label memberName = new Label(
                "Tên: " + (registration.getMember().getUser() != null ? registration.getMember().getUser().getFullName()
                        : "N/A"));
        Label memberCode = new Label("Mã hội viên: " + registration.getMember().getMemberCode());
        Label planName = new Label(
                "Gói tập: " + (registration.getPlan() != null ? registration.getPlan().getPlanName() : "N/A"));
        Label startDate = new Label("Ngày bắt đầu: " + (registration.getStartDate() != null
                ? registration.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "N/A"));
        Label sessionsLeft = new Label("Buổi còn lại: " + registration.getSessionsLeft());

        vbox.getChildren().addAll(memberInfo, memberName, memberCode, planName, startDate, sessionsLeft);

        Scene scene = new Scene(vbox, 400, 250);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void showUnassignedMembersPopup(java.util.List<TrainingRegistration> unassignedList) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Chọn hội viên để thêm vào danh sách quản lý");

        TableView<TrainingRegistration> unassignedTable = new TableView<>();

        // Cột Registration ID
        TableColumn<TrainingRegistration, String> colRegId = new TableColumn<>("Registration ID");
        colRegId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getRegistrationId())));
        colRegId.setPrefWidth(120);

        // Cột tên hội viên
        TableColumn<TrainingRegistration, String> colName = new TableColumn<>("Tên hội viên");
        colName.setCellValueFactory(cellData -> {
            String memberName = cellData.getValue().getMember() != null
                    && cellData.getValue().getMember().getUser() != null
                            ? cellData.getValue().getMember().getUser().getFullName()
                            : "";
            return new javafx.beans.property.SimpleStringProperty(memberName);
        });
        colName.setPrefWidth(180);

        // Cột gói tập
        TableColumn<TrainingRegistration, String> colPlan = new TableColumn<>("Gói tập");
        colPlan.setCellValueFactory(cellData -> {
            String planName = cellData.getValue().getPlan() != null ? cellData.getValue().getPlan().getPlanName() : "";
            return new javafx.beans.property.SimpleStringProperty(planName);
        });
        colPlan.setPrefWidth(150);

        // Cột ngày bắt đầu
        TableColumn<TrainingRegistration, String> colStart = new TableColumn<>("Ngày bắt đầu");
        colStart.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null)
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colStart.setPrefWidth(120);

        // Cột thao tác
        TableColumn<TrainingRegistration, Void> colSelectAction = new TableColumn<>("Thao tác");
        Callback<TableColumn<TrainingRegistration, Void>, TableCell<TrainingRegistration, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TrainingRegistration, Void> call(final TableColumn<TrainingRegistration, Void> param) {
                return new TableCell<>() {
                    private final Button selectBtn = new Button("Chọn");
                    {
                        selectBtn.setOnAction(event -> {
                            TrainingRegistration selected = getTableView().getItems().get(getIndex());
                            boolean success = trainingController.assignMemberToTrainerByRegistrationId(
                                    selected.getRegistrationId(), currentTrainerId);

                            if (success) {
                                showAlert("Thành công", "Đã thêm hội viên vào danh sách quản lý!");
                                popupStage.close();
                                refreshMembersList();
                            } else {
                                showAlert("Thất bại", "Không thể thêm hội viên!");
                            }
                        });
                        selectBtn.setStyle(
                                "-fx-background-color: #4FC3F7; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(selectBtn);
                        }
                    }
                };
            }
        };
        colSelectAction.setCellFactory(cellFactory);
        colSelectAction.setPrefWidth(100);

        unassignedTable.getColumns().addAll(colRegId, colName, colPlan, colStart, colSelectAction);
        unassignedTable.setItems(javafx.collections.FXCollections.observableArrayList(unassignedList));
        unassignedTable.setPrefWidth(670);
        unassignedTable.setPrefHeight(400);

        VBox vbox = new VBox(unassignedTable);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showExercisesPopup(TrainingSchedule schedule) {
        TrainingController trainingController = new TrainingController();
        java.util.List<ExerciseWithDetails> exerciseDetails = trainingController
                .getExercisesByScheduleId(schedule.getId());
        Stage popupStage = new Stage();
        popupStage.setTitle("Danh sách bài tập của buổi tập");

        TableView<ExerciseWithDetails> exerciseTable = new TableView<>();

        // Cột tên bài tập
        TableColumn<ExerciseWithDetails, String> colName = new TableColumn<>("Tên bài tập");
        colName.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExerciseName()));
        colName.setPrefWidth(150);

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
        colQuantity.setPrefWidth(100);

        // Cột ghi chú
        TableColumn<ExerciseWithDetails, String> colComment = new TableColumn<>("Ghi chú");
        colComment.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getComment()));
        colComment.setPrefWidth(150);

        // Cột mô tả
        TableColumn<ExerciseWithDetails, String> colDesc = new TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colDesc.setPrefWidth(180);

        // Cột thao tác
        TableColumn<ExerciseWithDetails, Void> colExerciseAction = new TableColumn<>("Thao tác");
        colExerciseAction.setPrefWidth(120);

        Callback<TableColumn<ExerciseWithDetails, Void>, TableCell<ExerciseWithDetails, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ExerciseWithDetails, Void> call(final TableColumn<ExerciseWithDetails, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Sửa");
                    private final Button deleteBtn = new Button("Xóa");
                    private final HBox hbox = new HBox(5, editBtn, deleteBtn);

                    {
                        editBtn.setPrefWidth(50);
                        deleteBtn.setPrefWidth(50);
                        hbox.setAlignment(Pos.CENTER);

                        editBtn.setOnAction(event -> {
                            ExerciseWithDetails exercise = getTableView().getItems().get(getIndex());
                            showEditExerciseDialog(schedule, exercise, popupStage);
                        });

                        deleteBtn.setOnAction(event -> {
                            ExerciseWithDetails exercise = getTableView().getItems().get(getIndex());
                            deleteExerciseFromSchedule(schedule, exercise, popupStage);
                        });

                        editBtn.setStyle(
                                "-fx-background-color: #FFB74D; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px;");
                        deleteBtn.setStyle(
                                "-fx-background-color: #F48FB1; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };
        colExerciseAction.setCellFactory(cellFactory);

        exerciseTable.getColumns().addAll(colName, colCode, colCategory, colQuantity, colComment, colDesc,
                colExerciseAction);
        exerciseTable.setItems(javafx.collections.FXCollections.observableArrayList(exerciseDetails));
        exerciseTable.setPrefWidth(980);
        exerciseTable.setPrefHeight(400);

        // Nút thêm bài tập mới
        Button addExerciseBtn = new Button("Thêm bài tập mới");
        addExerciseBtn.setPrefWidth(150);
        addExerciseBtn.setStyle(
                "-fx-background-color: #81C784; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        addExerciseBtn.setOnAction(event -> {
            showAddExercisesDialog(schedule, popupStage);
        });

        VBox vbox = new VBox(15);
        vbox.getChildren().addAll(exerciseTable, addExerciseBtn);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 20;");
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void loadRooms() {
        try {
            ObservableList<Room> rooms = FXCollections.observableArrayList(roomController.getAllRooms());
            roomComboBox.setItems(rooms);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải danh sách phòng tập");
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void refreshScheduleTable() {
        try {
            loadSchedulesByTrainer(currentTrainerId);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải lại dữ liệu");
        }
    }

    private void showAddExercisesDialog(TrainingSchedule schedule) {
        showAddExercisesDialog(schedule, null);
    }

    private void showAddExercisesDialog(TrainingSchedule schedule, Stage parentStage) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Thêm bài tập cho lịch tập");

            VBox root = new VBox(10);
            root.setPadding(new javafx.geometry.Insets(10));

            // ComboBox chọn bài tập
            ComboBox<Exercise> exerciseComboBox = new ComboBox<>();
            exerciseComboBox.setPromptText("Chọn bài tập");
            exerciseComboBox.setCellFactory(param -> new ListCell<Exercise>() {
                @Override
                protected void updateItem(Exercise item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getExerciseCode() + " - " + item.getExerciseName());
                    }
                }
            });
            exerciseComboBox.setButtonCell(new ListCell<Exercise>() {
                @Override
                protected void updateItem(Exercise item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getExerciseCode() + " - " + item.getExerciseName());
                    }
                }
            });

            // TextField nhập số hiệp
            TextField setField = new TextField();
            setField.setPromptText("Số hiệp");

            // TextField nhập số lần lặp
            TextField repField = new TextField();
            repField.setPromptText("Số lần lặp");

            // TextField nhập ghi chú
            TextField commentField = new TextField();
            commentField.setPromptText("Ghi chú");

            // Nút thêm bài tập
            Button addButton = new Button("Thêm bài tập");
            addButton.setOnAction(e -> {
                Exercise selectedExercise = exerciseComboBox.getValue();
                String set = setField.getText();
                String rep = repField.getText();
                String comment = commentField.getText();

                if (selectedExercise == null || set.isEmpty() || rep.isEmpty()) {
                    showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin");
                    return;
                }

                // Kiểm tra xem bài tập đã tồn tại trong schedule này chưa
                TrainingController trainingController = new TrainingController();
                java.util.List<ExerciseWithDetails> existingExercises = trainingController
                        .getExercisesByScheduleId(schedule.getId());
                boolean exerciseExists = existingExercises.stream()
                        .anyMatch(existing -> existing.getExerciseId() == selectedExercise.getExerciseId());

                if (exerciseExists) {
                    showAlert(AlertType.ERROR, "Lỗi",
                            "Bài tập \"" + selectedExercise.getExerciseName() + "\" đã tồn tại trong lịch tập này!");
                    return;
                }

                try {
                    int setValue = Integer.parseInt(set);
                    int repValue = Integer.parseInt(rep);
                    if (trainingController.addExerciseToSchedule(schedule.getId(), selectedExercise.getExerciseId(),
                            repValue, setValue, comment)) {
                        showAlert(AlertType.INFORMATION, "Thành công", "Đã thêm bài tập vào lịch");
                        dialog.close();
                        if (parentStage != null) {
                            parentStage.close();
                            showExercisesPopup(schedule); // Refresh popup với dữ liệu mới
                        }
                    } else {
                        showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm bài tập");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(AlertType.ERROR, "Lỗi", "Số hiệp và số lần lặp phải là số");
                }
            });

            // Load danh sách bài tập và sort theo mã bài tập
            java.util.List<Exercise> exercises = exerciseController.getAllExercises();
            exercises.sort((e1, e2) -> e1.getExerciseCode().compareTo(e2.getExerciseCode()));
            exerciseComboBox.setItems(FXCollections.observableArrayList(exercises));

            root.getChildren().addAll(
                    new Label("Chọn bài tập:"),
                    exerciseComboBox,
                    new Label("Số hiệp:"),
                    setField,
                    new Label("Số lần lặp:"),
                    repField,
                    new Label("Ghi chú:"),
                    commentField,
                    addButton);

            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể hiển thị dialog thêm bài tập");
        }
    }

    private void viewExercises(TrainingSchedule schedule) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Danh sách bài tập");

            VBox root = new VBox(10);
            root.setPadding(new javafx.geometry.Insets(10));

            // Bảng hiển thị bài tập
            TableView<ExerciseWithDetails> exerciseTable = new TableView<>();
            TableColumn<ExerciseWithDetails, String> nameCol = new TableColumn<>("Tên bài tập");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
            TableColumn<ExerciseWithDetails, String> repCol = new TableColumn<>("Số lần lặp");
            repCol.setCellValueFactory(new PropertyValueFactory<>("rep"));
            TableColumn<ExerciseWithDetails, String> setCol = new TableColumn<>("Số hiệp");
            setCol.setCellValueFactory(new PropertyValueFactory<>("set"));

            exerciseTable.getColumns().addAll(nameCol, repCol, setCol);

            // Nút thêm bài tập mới
            Button addButton = new Button("Thêm bài tập mới");
            addButton.setOnAction(e -> {
                dialog.close();
                showAddExercisesDialog(schedule);
            });

            root.getChildren().addAll(exerciseTable, addButton);

            // Load dữ liệu
            TrainingController trainingController = new TrainingController();
            ObservableList<ExerciseWithDetails> exercises = FXCollections.observableArrayList(
                    trainingController.getExercisesByScheduleId(schedule.getId()));
            exerciseTable.setItems(exercises);

            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể hiển thị danh sách bài tập");
        }
    }

    @FXML
    private void showAddScheduleDialog() {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Thêm lịch tập mới");

            VBox root = new VBox(10);
            root.setPadding(new javafx.geometry.Insets(10));

            // ComboBox chọn hội viên
            ComboBox<Member> memberComboBox = new ComboBox<>();
            memberComboBox.setPromptText("Chọn hội viên");
            memberComboBox.setCellFactory(param -> new ListCell<Member>() {
                @Override
                protected void updateItem(Member item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getUser().getFullName() + " (" + item.getMemberCode() + ")");
                    }
                }
            });
            memberComboBox.setButtonCell(new ListCell<Member>() {
                @Override
                protected void updateItem(Member item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getUser().getFullName() + " (" + item.getMemberCode() + ")");
                    }
                }
            });

            // ComboBox chọn phòng
            ComboBox<Room> roomComboBox = new ComboBox<>();
            roomComboBox.setPromptText("Chọn phòng");
            roomComboBox.setCellFactory(param -> new ListCell<Room>() {
                @Override
                protected void updateItem(Room item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getRoomName());
                    }
                }
            });
            roomComboBox.setButtonCell(new ListCell<Room>() {
                @Override
                protected void updateItem(Room item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getRoomName());
                    }
                }
            });

            // DatePicker chọn ngày
            DatePicker datePicker = new DatePicker();
            datePicker.setPromptText("Chọn ngày");

            // TextField nhập giờ
            TextField timeField = new TextField();
            timeField.setPromptText("Giờ (HH:mm)");

            // TextArea nhập ghi chú
            TextArea notesArea = new TextArea();
            notesArea.setPromptText("Ghi chú");
            notesArea.setPrefRowCount(3);

            // Nút thêm lịch tập
            Button addButton = new Button("Thêm lịch tập");
            addButton.setOnAction(e -> {
                Member selectedMember = memberComboBox.getValue();
                Room selectedRoom = roomComboBox.getValue();
                java.time.LocalDate selectedDate = datePicker.getValue();
                String time = timeField.getText();
                String notes = notesArea.getText();

                if (selectedMember == null || selectedRoom == null || selectedDate == null || time.isEmpty()) {
                    showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin");
                    return;
                }

                try {
                    // Kiểm tra định dạng giờ
                    if (!time.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
                        showAlert(AlertType.ERROR, "Lỗi", "Định dạng giờ không hợp lệ (HH:mm)");
                        return;
                    }

                    TrainingController trainingController = new TrainingController();
                    TrainingSchedule schedule = new TrainingSchedule();
                    schedule.setTrainerId(currentTrainerId);
                    schedule.setMemberId(selectedMember.getMemberId());
                    schedule.setMembershipId(1); // Tạm thời set membershipId = 1, cần cải thiện sau
                    schedule.setRoomId(selectedRoom.getRoomId());
                    schedule.setDate(selectedDate);
                    schedule.setTime(time);
                    schedule.setDuration(60); // Mặc định 60 phút
                    schedule.setNotes(notes);
                    schedule.setStatus(model.enums.enum_TrainingStatus.SCHEDULED.getValue());

                    if (trainingController.addTrainingSchedule(schedule)) {
                        showAlert(AlertType.INFORMATION, "Thành công", "Đã thêm lịch tập mới");
                        dialog.close();
                        refreshScheduleTable();
                    } else {
                        showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm lịch tập");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi thêm lịch tập");
                }
            });

            // Load danh sách hội viên và phòng
            memberComboBox.setItems(
                    FXCollections.observableArrayList(trainingController.getMembersByTrainerId(currentTrainerId)));
            roomComboBox.setItems(FXCollections.observableArrayList(roomController.getAllRooms()));

            root.getChildren().addAll(
                    new Label("Chọn hội viên:"),
                    memberComboBox,
                    new Label("Chọn phòng:"),
                    roomComboBox,
                    new Label("Chọn ngày:"),
                    datePicker,
                    new Label("Giờ:"),
                    timeField,
                    new Label("Ghi chú:"),
                    notesArea,
                    addButton);

            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể hiển thị dialog thêm lịch tập");
        }
    }

    private void showEditExerciseDialog(TrainingSchedule schedule, ExerciseWithDetails exercise, Stage parentStage) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Sửa thông tin bài tập");

            VBox root = new VBox(10);
            root.setPadding(new javafx.geometry.Insets(10));

            // Hiển thị tên bài tập (chỉ đọc)
            Label exerciseNameLabel = new Label("Bài tập: " + exercise.getExerciseName());
            exerciseNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // TextField nhập số hiệp
            TextField setField = new TextField();
            setField.setText(String.valueOf(exercise.getSet()));
            setField.setPromptText("Số hiệp");

            // TextField nhập số lần lặp
            TextField repField = new TextField();
            repField.setText(String.valueOf(exercise.getRep()));
            repField.setPromptText("Số lần lặp");

            // TextField nhập ghi chú
            TextField commentField = new TextField();
            commentField.setText(exercise.getComment() != null ? exercise.getComment() : "");
            commentField.setPromptText("Ghi chú");

            // Nút cập nhật
            Button updateButton = new Button("Cập nhật");
            updateButton.setOnAction(e -> {
                String set = setField.getText();
                String rep = repField.getText();
                String comment = commentField.getText();

                if (set.isEmpty() || rep.isEmpty()) {
                    showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin");
                    return;
                }

                try {
                    int setValue = Integer.parseInt(set);
                    int repValue = Integer.parseInt(rep);
                    TrainingController trainingController = new TrainingController();
                    if (trainingController.updateExerciseInSchedule(schedule.getId(), exercise.getExerciseId(),
                            setValue, repValue, comment)) {
                        showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin bài tập");
                        dialog.close();
                        parentStage.close();
                        showExercisesPopup(schedule); // Refresh popup với dữ liệu mới
                    } else {
                        showAlert(AlertType.ERROR, "Lỗi", "Không thể cập nhật bài tập");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(AlertType.ERROR, "Lỗi", "Số hiệp và số lần lặp phải là số");
                }
            });

            updateButton.setStyle(
                    "-fx-background-color: #4FC3F7; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

            root.getChildren().addAll(
                    exerciseNameLabel,
                    new Label("Số hiệp:"),
                    setField,
                    new Label("Số lần lặp:"),
                    repField,
                    new Label("Ghi chú:"),
                    commentField,
                    updateButton);

            Scene scene = new Scene(root, 300, 350);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể hiển thị dialog sửa bài tập");
        }
    }

    private void deleteExerciseFromSchedule(TrainingSchedule schedule, ExerciseWithDetails exercise,
            Stage parentStage) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(
                "Bạn có chắc chắn muốn xóa bài tập \"" + exercise.getExerciseName() + "\" khỏi lịch tập?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                TrainingController trainingController = new TrainingController();
                if (trainingController.deleteExerciseFromSchedule(schedule.getId(), exercise.getExerciseId())) {
                    showAlert(AlertType.INFORMATION, "Thành công", "Đã xóa bài tập khỏi lịch");
                    parentStage.close();
                    showExercisesPopup(schedule); // Refresh popup với dữ liệu mới
                } else {
                    showAlert(AlertType.ERROR, "Lỗi", "Không thể xóa bài tập");
                }
            }
        });
    }
}