package view.trainerView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Separator;
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
import javafx.scene.control.ButtonType;
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
import controller.TrainingScheduleController;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import javafx.scene.control.ScrollPane;

public class WorkoutsController {
    @FXML
    private Label pageTitle;

    // Bảng lịch dạy
    @FXML
    private TableView<TrainingSchedule> scheduleTable;
    @FXML
    private TableColumn<TrainingSchedule, String> colDateTime;
    @FXML
    private TableColumn<TrainingSchedule, String> colMember;
    @FXML
    private TableColumn<TrainingSchedule, String> colScheduleMemberCode;
    @FXML
    private TableColumn<TrainingSchedule, String> colScheduleRegId;
    @FXML
    private TableColumn<TrainingSchedule, String> colRoom;
    @FXML
    private TableColumn<TrainingSchedule, String> colStatus;
    @FXML
    private TableColumn<TrainingSchedule, String> colNotes;
    @FXML
    private TableColumn<TrainingSchedule, Void> colAction;
    @FXML
    private TableColumn<TrainingSchedule, String> colRating;

    // Bảng hội viên đang quản lý
    @FXML
    private TableView<TrainingRegistration> managedMembersTable;
    @FXML
    private TableColumn<TrainingRegistration, String> colRegId;
    @FXML
    private TableColumn<TrainingRegistration, String> colMemberCode;
    @FXML
    private TableColumn<TrainingRegistration, String> colMemberName;
    @FXML
    private TableColumn<TrainingRegistration, String> colMemberPlan;
    @FXML
    private TableColumn<TrainingRegistration, String> colStartDate;
    @FXML
    private TableColumn<TrainingRegistration, String> colMembershipStatus;
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
    private TrainingScheduleController trainingScheduleController = new TrainingScheduleController();
    private TrainerController trainerController = new TrainerController();
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
        colDateTime.setCellValueFactory(cellData -> {
            TrainingSchedule schedule = cellData.getValue();
            String dateTime = "";

            if (schedule.getDate() != null) {
                dateTime = schedule.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                if (schedule.getTime() != null && !schedule.getTime().trim().isEmpty()) {
                    dateTime += " - " + schedule.getTime();
                }
            }

            return new javafx.beans.property.SimpleStringProperty(dateTime);
        });

        colMember.setCellValueFactory(cellData -> {
            String memberName = cellData.getValue().getMemberName();
            return new javafx.beans.property.SimpleStringProperty(memberName != null ? memberName : "Chưa có học viên");
        });
        colScheduleMemberCode.setCellValueFactory(cellData -> {
            String memberCode = cellData.getValue().getMemberCode();
            return new javafx.beans.property.SimpleStringProperty(memberCode != null ? memberCode : "");
        });
        colScheduleRegId.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cellData.getValue().getRegistrationId()));
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

        colRating.setCellValueFactory(cellData -> {
            TrainingSchedule schedule = cellData.getValue();
            int rating = trainingController.getTrainingScheduleRating(schedule.getId());
            String ratingText = rating > 0 ? rating + "/5" : "-";
            return new javafx.beans.property.SimpleStringProperty(ratingText);
        });
    }

    private void setupManagedMembersTable() {
        colRegId.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cellData.getValue().getRegistrationId()));
        });

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

        colMembershipStatus.setCellValueFactory(cellData -> {
            // Lấy trạng thái membership của hội viên
            String status = getMembershipStatus(cellData.getValue().getMemberId());
            return new javafx.beans.property.SimpleStringProperty(status != null ? status : "Chưa xác định");
        });

        colSessionsLeft.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cellData.getValue().getSessionsLeft()));
        });
    }

    // Method hỗ trợ để lấy trạng thái membership của hội viên
    private String getMembershipStatus(int memberId) {
        String status = null;
        String sql = "SELECT m.status FROM Memberships m " +
                "JOIN Members mem ON m.memberid = mem.memberid " +
                "WHERE mem.memberid = ? " +
                "ORDER BY m.enddate DESC LIMIT 1";

        try (java.sql.Connection conn = utils.DBConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy trạng thái membership: " + e.getMessage());
        }

        return status;
    }

    private void loadSchedulesByTrainer(int trainerId) {
        ObservableList<TrainingSchedule> list = FXCollections.observableArrayList();
        list.addAll(trainingController.getSchedulesByTrainerId(trainerId));

        // Sắp xếp theo trạng thái và thời gian
        list.sort((s1, s2) -> {
            // So sánh theo trạng thái trước
            int statusCompare = Integer.compare(getStatusPriority(s1.getStatus()), getStatusPriority(s2.getStatus()));
            if (statusCompare != 0) {
                return statusCompare;
            }

            // Nếu trạng thái giống nhau, sắp xếp theo thời gian
            // Với "Đã lên lịch" - sắp xếp tăng dần (sớm nhất trước)
            // Với "Hoàn thành" và "Đã hủy" - sắp xếp giảm dần (mới nhất trước)
            int timeCompare = 0;

            if (s1.getDate() != null && s2.getDate() != null) {
                timeCompare = s1.getDate().compareTo(s2.getDate());

                // Nếu cùng ngày, so sánh theo giờ
                if (timeCompare == 0 && s1.getTime() != null && s2.getTime() != null) {
                    timeCompare = s1.getTime().compareTo(s2.getTime());
                }
            }

            // Đối với "Đã lên lịch" - thời gian tăng dần (sớm nhất trước)
            // Đối với "Hoàn thành" và "Đã hủy" - thời gian giảm dần (mới nhất trước)
            if ("Đã lên lịch".equals(s1.getStatus())) {
                return timeCompare;
            } else {
                return -timeCompare;
            }
        });

        // Log kiểm tra dữ liệu
        System.out.println("==== DANH SÁCH LỊCH TẬP CỦA HLV (ĐÃ SẮP XẾP) ====");
        for (TrainingSchedule ts : list) {
            System.out.println("ScheduleID: " + ts.getId() + ", Member: " + ts.getMemberName() + ", TrainerID: "
                    + ts.getTrainerId() + ", Date: " + ts.getDate() + ", Status: " + ts.getStatus());
        }
        System.out.println("==== TỔNG SỐ: " + list.size() + " lịch ====");
        scheduleTable.setItems(list);
        scheduleTable.refresh();
    }

    // Method hỗ trợ để xác định thứ tự ưu tiên của trạng thái
    private int getStatusPriority(String status) {
        switch (status) {
            case "Đã lên lịch":
                return 1;
            case "Hoàn thành":
                return 2;
            case "Đã hủy":
                return 3;
            default:
                return 4;
        }
    }

    private void loadManagedMembers(int trainerId) {
        ObservableList<TrainingRegistration> list = FXCollections.observableArrayList();

        // Lấy thông tin trainer để biết specialization
        Trainer trainer = trainerController.getTrainerById(trainerId);
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
        Callback<TableColumn<TrainingSchedule, Void>, TableCell<TrainingSchedule, Void>> factory = new Callback<TableColumn<TrainingSchedule, Void>, TableCell<TrainingSchedule, Void>>() {
            @Override
            public TableCell<TrainingSchedule, Void> call(final TableColumn<TrainingSchedule, Void> param) {
                return new TableCell<TrainingSchedule, Void>() {
                    private final Button viewBtn = new Button("Bài tập");
                    private final Button deleteBtn = new Button("Hủy");
                    private final HBox hbox = new HBox(5, viewBtn, deleteBtn);

                    {
                        viewBtn.setPrefWidth(100);
                        viewBtn.setStyle(
                                "-fx-background-color: #81C784; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

                        deleteBtn.setPrefWidth(100);

                        hbox.setAlignment(Pos.CENTER);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            TrainingSchedule schedule = getTableView().getItems().get(getIndex());

                            // Kiểm tra trạng thái để enable/disable nút hủy
                            boolean canCancel = "Đã lên lịch".equals(schedule.getStatus());
                            deleteBtn.setDisable(!canCancel);

                            if (canCancel) {
                                // Style cho nút hủy khi có thể ấn
                                deleteBtn.setStyle(
                                        "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
                            } else {
                                // Style cho nút hủy khi bị disable
                                deleteBtn.setStyle(
                                        "-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: default;");
                            }

                            viewBtn.setOnAction(e -> showExercisesPopup(schedule));

                            deleteBtn.setOnAction(e -> {
                                // Kiểm tra lại trạng thái trước khi cho phép hủy
                                if (!"Đã lên lịch".equals(schedule.getStatus())) {
                                    showAlert(AlertType.WARNING, "Thông báo",
                                            "Chỉ có thể hủy lịch tập có trạng thái 'Đã lên lịch'!");
                                    return;
                                }

                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Xác nhận hủy");
                                alert.setHeaderText(null);
                                alert.setContentText("Bạn có chắc chắn muốn hủy lịch tập này?");

                                alert.showAndWait().ifPresent(response -> {
                                    if (response == ButtonType.OK) {
                                        try {
                                            if (trainingController.cancelTrainingSchedule(schedule.getId())) {
                                                refreshScheduleTable();
                                            } else {
                                                showAlert(AlertType.ERROR, "Lỗi", "Không thể hủy lịch tập");
                                            }
                                        } catch (SQLException ex) {
                                            showAlert(AlertType.ERROR, "Lỗi",
                                                    "Lỗi khi hủy lịch tập: " + ex.getMessage());
                                        }
                                    }
                                });
                            });
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(factory);
    }

    private void addButtonToMembersTable() {
        Callback<TableColumn<TrainingRegistration, Void>, TableCell<TrainingRegistration, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<TrainingRegistration, Void> call(final TableColumn<TrainingRegistration, Void> param) {
                return new TableCell<>() {
                    private final Button detailBtn = new Button("Chi tiết");
                    private final Button metricsBtn = new Button("Chỉ số");
                    private final HBox hbox = new HBox(5, detailBtn, metricsBtn);
                    {
                        detailBtn.setPrefWidth(100);
                        detailBtn.setOnAction(event -> {
                            TrainingRegistration registration = getTableView().getItems().get(getIndex());
                            showMemberDetailsPopup(registration);
                        });
                        detailBtn.setStyle(
                                "-fx-background-color: #81C784; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

                        metricsBtn.setPrefWidth(100);
                        metricsBtn.setOnAction(event -> {
                            TrainingRegistration registration = getTableView().getItems().get(getIndex());
                            showBodyMetricsPopup(registration);
                        });
                        metricsBtn.setStyle(
                                "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

                        hbox.setAlignment(Pos.CENTER);
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
        colMemberAction.setCellFactory(cellFactory);
    }

   

    private void showBodyMetricsPopup(TrainingRegistration registration) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Quản lý chỉ số cơ thể - " + registration.getMember().getUser().getFullName());

        VBox mainVBox = new VBox(15);
        mainVBox.setStyle("-fx-padding: 20;");

        // Thông tin hội viên
        Label memberInfo = new Label("Hội viên: " + registration.getMember().getUser().getFullName() +
                " (Mã: " + registration.getMember().getMemberCode() + ")");
        memberInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Bảng hiển thị lịch sử số đo
        TableView<model.MemberProgress> progressTable = new TableView<>();
        setupProgressTable(progressTable);

        // Load dữ liệu
        loadMemberProgress(progressTable, registration.getMemberId());

        // Các nút thao tác
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Thêm số đo mới");
        addBtn.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        addBtn.setOnAction(e -> showAddProgressDialog(registration.getMemberId(), progressTable));

        Button editBtn = new Button("Sửa số đo");
        editBtn.setStyle(
                "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        editBtn.setOnAction(e -> {
            model.MemberProgress selected = progressTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(AlertType.WARNING, "Thông báo", "Vui lòng chọn một số đo để sửa!");
                return;
            }
            showEditProgressDialog(selected, progressTable);
        });

        Button deleteBtn = new Button("Xóa số đo");
        deleteBtn.setStyle(
                "-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        deleteBtn.setOnAction(e -> {
            model.MemberProgress selected = progressTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(AlertType.WARNING, "Thông báo", "Vui lòng chọn một số đo để xóa!");
                return;
            }
            deleteProgress(selected, progressTable);
        });

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn);

        mainVBox.getChildren().addAll(memberInfo, progressTable, buttonBox);

        Scene scene = new Scene(mainVBox, 1200, 600);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void setupProgressTable(TableView<model.MemberProgress> table) {
        // Cột ngày đo
        TableColumn<model.MemberProgress, String> colDate = new TableColumn<>("Ngày đo");
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getMeasurementDate() != null)
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getMeasurementDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colDate.setPrefWidth(100);

        // Cột cân nặng
        TableColumn<model.MemberProgress, String> colWeight = new TableColumn<>("Cân nặng (kg)");
        colWeight.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWeight() != null ? cellData.getValue().getWeight().toString() : "-"));
        colWeight.setPrefWidth(100);

        // Cột chiều cao
        TableColumn<model.MemberProgress, String> colHeight = new TableColumn<>("Chiều cao (cm)");
        colHeight.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHeight() != null ? cellData.getValue().getHeight().toString() : "-"));
        colHeight.setPrefWidth(100);

        // Cột BMI
        TableColumn<model.MemberProgress, String> colBMI = new TableColumn<>("BMI");
        colBMI.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBmi() != null ? cellData.getValue().getBmi().toString() : "-"));
        colBMI.setPrefWidth(80);

        // Cột % mỡ cơ thể
        TableColumn<model.MemberProgress, String> colBodyFat = new TableColumn<>("% Mỡ cơ thể");
        colBodyFat.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBodyFatPercentage() != null
                        ? cellData.getValue().getBodyFatPercentage().toString() + "%"
                        : "-"));
        colBodyFat.setPrefWidth(100);

        // Cột ngực
        TableColumn<model.MemberProgress, String> colChest = new TableColumn<>("Ngực (cm)");
        colChest.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getChest() != null ? cellData.getValue().getChest().toString() : "-"));
        colChest.setPrefWidth(80);

        // Cột eo
        TableColumn<model.MemberProgress, String> colWaist = new TableColumn<>("Eo (cm)");
        colWaist.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWaist() != null ? cellData.getValue().getWaist().toString() : "-"));
        colWaist.setPrefWidth(80);

        // Cột hông
        TableColumn<model.MemberProgress, String> colHip = new TableColumn<>("Hông (cm)");
        colHip.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHip() != null ? cellData.getValue().getHip().toString() : "-"));
        colHip.setPrefWidth(80);

        // Cột cơ tay
        TableColumn<model.MemberProgress, String> colBiceps = new TableColumn<>("Cơ tay (cm)");
        colBiceps.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBiceps() != null ? cellData.getValue().getBiceps().toString() : "-"));
        colBiceps.setPrefWidth(80);

        // Cột đùi
        TableColumn<model.MemberProgress, String> colThigh = new TableColumn<>("Đùi (cm)");
        colThigh.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getThigh() != null ? cellData.getValue().getThigh().toString() : "-"));
        colThigh.setPrefWidth(80);

        // Cột ghi chú
        TableColumn<model.MemberProgress, String> colNotes = new TableColumn<>("Ghi chú");
        colNotes.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNotes() != null ? cellData.getValue().getNotes() : ""));
        colNotes.setPrefWidth(200);

        table.getColumns().addAll(colDate, colWeight, colHeight, colBMI, colBodyFat,
                colChest, colWaist, colHip, colBiceps, colThigh, colNotes);
        table.setPrefHeight(300);
    }

    private void loadMemberProgress(TableView<model.MemberProgress> table, int memberId) {
        ObservableList<model.MemberProgress> progressList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM MemberProgress WHERE memberid = ? AND (status IS NULL OR status != 'Đã xóa') ORDER BY measurementdate DESC";

        try (java.sql.Connection conn = utils.DBConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.MemberProgress progress = new model.MemberProgress();
                progress.setProgressId(rs.getInt("progressid"));
                progress.setMemberId(rs.getInt("memberid"));
                if (rs.getDate("measurementdate") != null) {
                    progress.setMeasurementDate(rs.getDate("measurementdate").toLocalDate());
                }
                progress.setWeight(
                        rs.getBigDecimal("weight") != null ? rs.getBigDecimal("weight").doubleValue() : null);
                progress.setHeight(
                        rs.getBigDecimal("height") != null ? rs.getBigDecimal("height").doubleValue() : null);
                progress.setBmi(rs.getBigDecimal("bmi") != null ? rs.getBigDecimal("bmi").doubleValue() : null);
                progress.setBodyFatPercentage(rs.getBigDecimal("bodyfatpercentage") != null
                        ? rs.getBigDecimal("bodyfatpercentage").doubleValue()
                        : null);
                progress.setChest(rs.getBigDecimal("chest") != null ? rs.getBigDecimal("chest").doubleValue() : null);
                progress.setWaist(rs.getBigDecimal("waist") != null ? rs.getBigDecimal("waist").doubleValue() : null);
                progress.setHip(rs.getBigDecimal("hip") != null ? rs.getBigDecimal("hip").doubleValue() : null);
                progress.setBiceps(
                        rs.getBigDecimal("biceps") != null ? rs.getBigDecimal("biceps").doubleValue() : null);
                progress.setThigh(rs.getBigDecimal("thigh") != null ? rs.getBigDecimal("thigh").doubleValue() : null);
                progress.setTrainerId(rs.getObject("trainerid") != null ? rs.getInt("trainerid") : null);
                progress.setStatus(rs.getString("status"));
                progress.setNotes(rs.getString("notes"));

                progressList.add(progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu chỉ số cơ thể: " + e.getMessage());
        }

        table.setItems(progressList);
    }

    private void showAddProgressDialog(int memberId, TableView<model.MemberProgress> table) {
        Stage dialog = new Stage();
        dialog.setTitle("Thêm số đo mới");

        VBox root = createProgressForm(null, memberId);

        // Nút lưu
        Button saveBtn = new Button("Lưu");
        saveBtn.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        saveBtn.setOnAction(e -> {
            if (saveProgress(root, null, memberId)) {
                dialog.close();
                loadMemberProgress(table, memberId);
            }
        });

        root.getChildren().add(saveBtn);

        Scene scene = new Scene(root, 400, 800);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showEditProgressDialog(model.MemberProgress progress, TableView<model.MemberProgress> table) {
        Stage dialog = new Stage();
        dialog.setTitle("Sửa số đo");

        VBox root = createProgressForm(progress, progress.getMemberId());

        // Nút cập nhật
        Button updateBtn = new Button("Cập nhật");
        updateBtn.setStyle(
                "-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        updateBtn.setOnAction(e -> {
            if (saveProgress(root, progress, progress.getMemberId())) {
                dialog.close();
                loadMemberProgress(table, progress.getMemberId());
            }
        });

        root.getChildren().add(updateBtn);

        Scene scene = new Scene(root, 400, 800);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private VBox createProgressForm(model.MemberProgress progress, int memberId) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");

        // Ngày đo
        Label dateLabel = new Label("Ngày đo:");
        DatePicker datePicker = new DatePicker();
        if (progress != null && progress.getMeasurementDate() != null) {
            datePicker.setValue(progress.getMeasurementDate());
        } else {
            datePicker.setValue(java.time.LocalDate.now());
        }
        datePicker.setUserData("measurementDate");

        // Cân nặng
        Label weightLabel = new Label("Cân nặng (kg):");
        TextField weightField = new TextField();
        weightField.setPromptText("Nhập cân nặng");
        if (progress != null && progress.getWeight() != null) {
            weightField.setText(progress.getWeight().toString());
        }
        weightField.setUserData("weight");

        // Chiều cao
        Label heightLabel = new Label("Chiều cao (cm):");
        TextField heightField = new TextField();
        heightField.setPromptText("Nhập chiều cao");
        if (progress != null && progress.getHeight() != null) {
            heightField.setText(progress.getHeight().toString());
        }
        heightField.setUserData("height");

        // BMI
        Label bmiLabel = new Label("BMI:");
        TextField bmiField = new TextField();
        bmiField.setPromptText("Nhập BMI");
        if (progress != null && progress.getBmi() != null) {
            bmiField.setText(progress.getBmi().toString());
        }
        bmiField.setUserData("bmi");

        // % mỏ cơ thể
        Label bodyFatLabel = new Label("% Mỡ cơ thể:");
        TextField bodyFatField = new TextField();
        bodyFatField.setPromptText("Nhập % mỡ cơ thể");
        if (progress != null && progress.getBodyFatPercentage() != null) {
            bodyFatField.setText(progress.getBodyFatPercentage().toString());
        }
        bodyFatField.setUserData("bodyFatPercentage");

        // Ngực
        Label chestLabel = new Label("Ngực (cm):");
        TextField chestField = new TextField();
        chestField.setPromptText("Nhập số đo ngực");
        if (progress != null && progress.getChest() != null) {
            chestField.setText(progress.getChest().toString());
        }
        chestField.setUserData("chest");

        // Eo
        Label waistLabel = new Label("Eo (cm):");
        TextField waistField = new TextField();
        waistField.setPromptText("Nhập số đo eo");
        if (progress != null && progress.getWaist() != null) {
            waistField.setText(progress.getWaist().toString());
        }
        waistField.setUserData("waist");

        // Hông
        Label hipLabel = new Label("Hông (cm):");
        TextField hipField = new TextField();
        hipField.setPromptText("Nhập số đo hông");
        if (progress != null && progress.getHip() != null) {
            hipField.setText(progress.getHip().toString());
        }
        hipField.setUserData("hip");

        // Cơ tay
        Label bicepsLabel = new Label("Cơ tay (cm):");
        TextField bicepsField = new TextField();
        bicepsField.setPromptText("Nhập số đo cơ tay");
        if (progress != null && progress.getBiceps() != null) {
            bicepsField.setText(progress.getBiceps().toString());
        }
        bicepsField.setUserData("biceps");

        // Đùi
        Label thighLabel = new Label("Đùi (cm):");
        TextField thighField = new TextField();
        thighField.setPromptText("Nhập số đo đùi");
        if (progress != null && progress.getThigh() != null) {
            thighField.setText(progress.getThigh().toString());
        }
        thighField.setUserData("thigh");

        // Ghi chú
        Label notesLabel = new Label("Ghi chú:");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Nhập ghi chú");
        notesArea.setPrefRowCount(3);
        if (progress != null && progress.getNotes() != null) {
            notesArea.setText(progress.getNotes());
        }
        notesArea.setUserData("notes");

        root.getChildren().addAll(
                dateLabel, datePicker,
                weightLabel, weightField,
                heightLabel, heightField,
                bmiLabel, bmiField,
                bodyFatLabel, bodyFatField,
                chestLabel, chestField,
                waistLabel, waistField,
                hipLabel, hipField,
                bicepsLabel, bicepsField,
                thighLabel, thighField,
                notesLabel, notesArea);

        return root;
    }

    private boolean saveProgress(VBox form, model.MemberProgress existingProgress, int memberId) {
        try {
            // Lấy dữ liệu từ form
            DatePicker datePicker = null;
            java.math.BigDecimal weight = null, height = null, bmi = null, bodyFat = null;
            java.math.BigDecimal chest = null, waist = null, hip = null, biceps = null, thigh = null;
            String notes = null;

            for (javafx.scene.Node node : form.getChildren()) {
                if (node instanceof DatePicker && "measurementDate".equals(node.getUserData())) {
                    datePicker = (DatePicker) node;
                } else if (node instanceof TextField) {
                    TextField field = (TextField) node;
                    String value = field.getText().trim();
                    if (!value.isEmpty()) {
                        try {
                            java.math.BigDecimal decimal = new java.math.BigDecimal(value);
                            switch ((String) field.getUserData()) {
                                case "weight":
                                    weight = decimal;
                                    break;
                                case "height":
                                    height = decimal;
                                    break;
                                case "bmi":
                                    bmi = decimal;
                                    break;
                                case "bodyFatPercentage":
                                    bodyFat = decimal;
                                    break;
                                case "chest":
                                    chest = decimal;
                                    break;
                                case "waist":
                                    waist = decimal;
                                    break;
                                case "hip":
                                    hip = decimal;
                                    break;
                                case "biceps":
                                    biceps = decimal;
                                    break;
                                case "thigh":
                                    thigh = decimal;
                                    break;
                            }
                        } catch (NumberFormatException e) {
                            showAlert(AlertType.ERROR, "Lỗi",
                                    "Giá trị không hợp lệ trong trường: " + field.getPromptText());
                            return false;
                        }
                    }
                } else if (node instanceof TextArea && "notes".equals(node.getUserData())) {
                    TextArea area = (TextArea) node;
                    notes = area.getText().trim();
                    if (notes.isEmpty())
                        notes = null;
                }
            }

            if (datePicker == null || datePicker.getValue() == null) {
                showAlert(AlertType.ERROR, "Lỗi", "Vui lòng chọn ngày đo!");
                return false;
            }

            String sql;
            if (existingProgress == null) {
                // Thêm mới - bỏ qua cột status để nhận giá trị null
                sql = "INSERT INTO MemberProgress (memberid, measurementdate, weight, height, bmi, bodyfatpercentage, chest, waist, hip, biceps, thigh, trainerid, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                // Cập nhật
                sql = "UPDATE MemberProgress SET measurementdate = ?, weight = ?, height = ?, bmi = ?, bodyfatpercentage = ?, chest = ?, waist = ?, hip = ?, biceps = ?, thigh = ?, trainerid = ?, notes = ? WHERE progressid = ?";
            }

            try (java.sql.Connection conn = utils.DBConnection.getConnection();
                    java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

                int index = 1;
                if (existingProgress == null) {
                    ps.setInt(index++, memberId);
                }
                ps.setDate(index++, java.sql.Date.valueOf(datePicker.getValue()));
                ps.setBigDecimal(index++, weight);
                ps.setBigDecimal(index++, height);
                ps.setBigDecimal(index++, bmi);
                ps.setBigDecimal(index++, bodyFat);
                ps.setBigDecimal(index++, chest);
                ps.setBigDecimal(index++, waist);
                ps.setBigDecimal(index++, hip);
                ps.setBigDecimal(index++, biceps);
                ps.setBigDecimal(index++, thigh);
                ps.setInt(index++, currentTrainerId);
                ps.setString(index++, notes);

                if (existingProgress != null) {
                    ps.setInt(index, existingProgress.getProgressId());
                }

                int result = ps.executeUpdate();
                if (result > 0) {
                    showAlert(AlertType.INFORMATION, "Thành công",
                            existingProgress == null ? "Đã thêm số đo mới!" : "Đã cập nhật số đo!");
                    return true;
                } else {
                    showAlert(AlertType.ERROR, "Lỗi", "Không thể lưu dữ liệu!");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
            return false;
        }
    }

    private void deleteProgress(model.MemberProgress progress, TableView<model.MemberProgress> table) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa số đo ngày " +
                progress.getMeasurementDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " không?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String sql = "UPDATE MemberProgress SET status = 'Đã xóa' WHERE progressid = ?";

                try (java.sql.Connection conn = utils.DBConnection.getConnection();
                        java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, progress.getProgressId());

                    int result = ps.executeUpdate();
                    if (result > 0) {
                        showAlert(AlertType.INFORMATION, "Thành công", "Đã xóa số đo!");
                        loadMemberProgress(table, progress.getMemberId());
                    } else {
                        showAlert(AlertType.ERROR, "Lỗi", "Không thể xóa số đo!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xóa: " + e.getMessage());
                }
            }
        });
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
        Trainer trainer = trainerController.getTrainerById(currentTrainerId);
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
        trainingController.updateExpiredTrainingSchedules();
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
        Label registrationId = new Label("Registration ID: " + registration.getRegistrationId());

        // Thêm bảng hiển thị các lịch tập của hội viên
        Label scheduleInfo = new Label("Lịch tập của hội viên:");
        scheduleInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<TrainingSchedule> scheduleTable = new TableView<>();

        // Cột ngày
        TableColumn<TrainingSchedule, String> colDate = new TableColumn<>("Ngày");
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDate() != null)
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Cột giờ
        TableColumn<TrainingSchedule, String> colTime = new TableColumn<>("Giờ");
        colTime.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTime());
        });

        // Cột phòng
        TableColumn<TrainingSchedule, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRoomName());
        });

        // Cột trạng thái
        TableColumn<TrainingSchedule, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus());
        });

        // Cột Registration ID
        TableColumn<TrainingSchedule, String> colRegId = new TableColumn<>("Registration ID");
        colRegId.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cellData.getValue().getRegistrationId()));
        });

        scheduleTable.getColumns().addAll(colDate, colTime, colRoom, colStatus, colRegId);

        // Lấy danh sách lịch tập của hội viên
        List<TrainingSchedule> schedules = trainingScheduleController.getSchedulesByMemberId(registration.getMemberId());
        scheduleTable.setItems(FXCollections.observableArrayList(schedules));

        vbox.getChildren().addAll(memberInfo, memberName, memberCode, planName, startDate, sessionsLeft, registrationId,
                new Separator(), scheduleInfo, scheduleTable);

        Scene scene = new Scene(vbox, 600, 500);
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
        colComment.setPrefWidth(280);

        // Cột mô tả
        TableColumn<ExerciseWithDetails, String> colDesc = new TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colDesc.setPrefWidth(180);

        // Cột thao tác - chỉ còn nút xóa
        TableColumn<ExerciseWithDetails, Void> colExerciseAction = new TableColumn<>("Thao tác");
        colExerciseAction.setPrefWidth(80);

        Callback<TableColumn<ExerciseWithDetails, Void>, TableCell<ExerciseWithDetails, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ExerciseWithDetails, Void> call(final TableColumn<ExerciseWithDetails, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Xóa");

                    {
                        deleteBtn.setPrefWidth(70);

                        deleteBtn.setOnAction(event -> {
                            ExerciseWithDetails exercise = getTableView().getItems().get(getIndex());
                            deleteExerciseFromSchedule(schedule, exercise, popupStage);
                        });

                        deleteBtn.setStyle(
                                "-fx-background-color: #F48FB1; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                        }
                    }
                };
            }
        };
        colExerciseAction.setCellFactory(cellFactory);

        exerciseTable.getColumns().addAll(colName, colCode, colCategory, colQuantity, colComment, colDesc,
                colExerciseAction);
        exerciseTable.setItems(javafx.collections.FXCollections.observableArrayList(exerciseDetails));
        exerciseTable.setPrefWidth(960);
        exerciseTable.setPrefHeight(400);

        // Nút thêm bài tập mới
        Button addExerciseBtn = new Button("Thêm bài tập mới");
        addExerciseBtn.setPrefWidth(150);
        addExerciseBtn.setStyle(
                "-fx-background-color: #81C784; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        addExerciseBtn.setOnAction(event -> {
            showAddExercisesDialog(schedule, popupStage);
        });

        // Nút sửa bài tập
        Button editExerciseBtn = new Button("Sửa bài tập");
        editExerciseBtn.setPrefWidth(120);
        editExerciseBtn.setStyle(
                "-fx-background-color: #FFB74D; -fx-text-fill: #232930; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        editExerciseBtn.setOnAction(event -> {
            ExerciseWithDetails selectedExercise = exerciseTable.getSelectionModel().getSelectedItem();
            if (selectedExercise == null) {
                showAlert(AlertType.WARNING, "Thông báo", "Vui lòng chọn bài tập cần sửa!");
                return;
            }
            showEditExerciseDialog(schedule, selectedExercise, popupStage);
        });

        // Nút nhận xét bài tập
        Button commentExerciseBtn = new Button("Nhận xét bài tập");
        commentExerciseBtn.setPrefWidth(130);
        commentExerciseBtn.setStyle(
                "-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");

        // Kiểm tra trạng thái lịch tập để enable/disable nút nhận xét
        boolean isCompleted = "Hoàn thành".equals(schedule.getStatus());
        commentExerciseBtn.setDisable(!isCompleted);

        if (!isCompleted) {
            commentExerciseBtn.setStyle(
                    "-fx-background-color: #CCCCCC; -fx-text-fill: #666666; -fx-font-weight: bold; -fx-background-radius: 8; -fx-font-size: 14px;");
        }

        commentExerciseBtn.setOnAction(event -> {
            showExerciseCommentsDialog(schedule, exerciseDetails, popupStage);
        });

        // HBox chứa các nút
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addExerciseBtn, editExerciseBtn, commentExerciseBtn);

        VBox vbox = new VBox(15);
        vbox.getChildren().addAll(exerciseTable, buttonBox);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-padding: 20;");
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox);
        popupStage.setScene(scene);
        popupStage.show();
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
            trainingController.updateExpiredTrainingSchedules();
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
                        setText(item.getMemberCode() + " - " + item.getUser().getFullName());
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
                        setText(item.getMemberCode() + " - " + item.getUser().getFullName());
                    }
                }
            });

            // ComboBox chọn Registration
            ComboBox<TrainingRegistration> registrationComboBox = new ComboBox<>();
            registrationComboBox.setPromptText("Chọn đăng ký");
            registrationComboBox.setCellFactory(param -> new ListCell<TrainingRegistration>() {
                @Override
                protected void updateItem(TrainingRegistration item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("ID: " + item.getRegistrationId() + " - " +
                                (item.getPlan() != null ? item.getPlan().getPlanName() : "N/A") +
                                " (Còn " + item.getSessionsLeft() + " buổi)");
                    }
                }
            });
            registrationComboBox.setButtonCell(new ListCell<TrainingRegistration>() {
                @Override
                protected void updateItem(TrainingRegistration item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("ID: " + item.getRegistrationId() + " - " +
                                (item.getPlan() != null ? item.getPlan().getPlanName() : "N/A") +
                                " (Còn " + item.getSessionsLeft() + " buổi)");
                    }
                }
            });

            // Khi chọn hội viên, cập nhật danh sách registration
            memberComboBox.setOnAction(e -> {
                Member selectedMember = memberComboBox.getValue();
                if (selectedMember != null) {
                    List<TrainingRegistration> registrations = trainingController
                            .getTrainingRegistrationsByMemberId(selectedMember.getMemberId());
                    // Lọc chỉ lấy các registration có trainerId trùng với currentTrainerId
                    registrations = registrations.stream()
                            .filter(r -> r.getTrainerId() == currentTrainerId)
                            .collect(java.util.stream.Collectors.toList());
                    registrationComboBox.setItems(FXCollections.observableArrayList(registrations));
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

            // ComboBox chọn giờ
            ComboBox<String> timeComboBox = new ComboBox<>();
            timeComboBox.setPromptText("Chọn giờ");
            // Tạo danh sách thời gian từ 7h đến 20h, cách 30 phút
            ObservableList<String> timeList = FXCollections.observableArrayList();
            for (int hour = 7; hour <= 20; hour++) {
                timeList.add(String.format("%02d:00", hour));
                if (hour < 20) { // Không thêm 20:30 vì kết thúc lúc 20:00
                    timeList.add(String.format("%02d:30", hour));
                }
            }
            timeComboBox.setItems(timeList);

            // TextArea nhập ghi chú
            TextArea notesArea = new TextArea();
            notesArea.setPromptText("Ghi chú");
            notesArea.setPrefRowCount(3);

            // Nút thêm lịch tập
            Button addButton = new Button("Thêm lịch tập");
            addButton.setOnAction(e -> {
                Member selectedMember = memberComboBox.getValue();
                TrainingRegistration selectedRegistration = registrationComboBox.getValue();
                Room selectedRoom = roomComboBox.getValue();
                java.time.LocalDate selectedDate = datePicker.getValue();
                String time = timeComboBox.getValue();
                String notes = notesArea.getText();

                if (selectedMember == null || selectedRegistration == null || selectedRoom == null
                        || selectedDate == null || time == null || time.trim().isEmpty()) {
                    showAlert(AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin");
                    return;
                }

                // Kiểm tra số buổi còn lại
                int sessionsLeft = selectedRegistration.getSessionsLeft();
                int scheduledSessions = trainingController
                        .getScheduledSessionsCount(selectedRegistration.getRegistrationId());

                if (scheduledSessions >= sessionsLeft) {
                    showAlert(AlertType.ERROR, "Lỗi", "Bạn đã đặt lịch quá số buổi còn lại");
                    return;
                }

                // Tạo đối tượng TrainingSchedule mới
                TrainingSchedule newSchedule = new TrainingSchedule();
                newSchedule.setRegistrationId(selectedRegistration.getRegistrationId());
                newSchedule.setMemberId(selectedMember.getMemberId());
                newSchedule.setTrainerId(currentTrainerId);
                newSchedule.setMembershipId(selectedRegistration.getPlanId());
                newSchedule.setDate(selectedDate);
                newSchedule.setTime(time);
                newSchedule.setRoomId(selectedRoom.getRoomId());
                newSchedule.setStatus("Đã lên lịch");
                newSchedule.setNotes(notes);

                try {
                    if (trainingScheduleController.addTrainingSchedule(newSchedule)) {
                        showAlert(AlertType.INFORMATION, "Thành công", "Đã thêm lịch tập mới");
                        dialog.close();
                        refreshScheduleTable();
                        // Mở ngay trang thêm bài tập với schedule vừa tạo
                        showExercisesPopup(newSchedule);
                    } else {
                        showAlert(AlertType.ERROR, "Lỗi", "Không thể thêm lịch tập");
                    }
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, "Lỗi", "Lỗi khi thêm lịch tập: " + ex.getMessage());
                }
            });

            // Load danh sách hội viên và phòng
            memberComboBox.setItems(
                    FXCollections.observableArrayList(trainingController.getMembersByTrainerId(currentTrainerId)));
            roomComboBox.setItems(FXCollections.observableArrayList(roomController.getAllRooms()));

            root.getChildren().addAll(
                    new Label("Chọn hội viên:"),
                    memberComboBox,
                    new Label("Chọn đăng ký:"),
                    registrationComboBox,
                    new Label("Chọn phòng:"),
                    roomComboBox,
                    new Label("Chọn ngày:"),
                    datePicker,
                    new Label("Giờ:"),
                    timeComboBox,
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

    private void showExerciseCommentsDialog(TrainingSchedule schedule,
            java.util.List<ExerciseWithDetails> exerciseDetails,
            Stage parentStage) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Nhận xét bài tập - " + schedule.getMemberName());

            VBox root = new VBox(15);
            root.setPadding(new javafx.geometry.Insets(20));

            // Tiêu đề
            Label titleLabel = new Label("Nhận xét cho từng bài tập");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #232930;");

            // Thông tin buổi tập
            Label sessionInfoLabel = new Label("Buổi tập ngày: " +
                    (schedule.getDate() != null ? schedule.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "N/A")
                    +
                    " lúc " + (schedule.getTime() != null ? schedule.getTime() : "N/A"));
            sessionInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

            // ScrollPane cho danh sách bài tập
            ScrollPane scrollPane = new ScrollPane();
            VBox exerciseContainer = new VBox(10);
            exerciseContainer.setPadding(new javafx.geometry.Insets(10));

            // Danh sách TextArea để lưu nhận xét
            java.util.List<TextArea> commentAreas = new ArrayList<>();

            // Tạo form nhận xét cho từng bài tập
            for (ExerciseWithDetails exercise : exerciseDetails) {
                VBox exerciseBox = new VBox(8);
                exerciseBox.setStyle(
                        "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: #f9f9f9; -fx-background-radius: 8;");

                // Thông tin bài tập
                Label exerciseNameLabel = new Label(exercise.getExerciseName());
                exerciseNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #232930;");

                Label exerciseDetailsLabel = new Label("Mã: " + exercise.getExerciseCode() + " | " +
                        "Loại: " + exercise.getCategory() + " | " +
                        "Số lượng: " + exercise.getQuantityFormatted());
                exerciseDetailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

                // Ô nhập nhận xét
                Label commentLabel = new Label("Nhận xét của HLV:");
                commentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #232930;");

                TextArea commentArea = new TextArea();
                commentArea.setPromptText("Nhập nhận xét cho bài tập này...");
                commentArea.setPrefRowCount(3);
                commentArea.setMaxHeight(80);
                commentArea.setStyle("-fx-font-size: 12px;");

                // Lấy nhận xét hiện tại nếu có (có thể từ database)
                String currentComment = getExerciseComment(schedule.getId(), exercise.getExerciseId());
                if (currentComment != null && !currentComment.trim().isEmpty()) {
                    commentArea.setText(currentComment);
                }

                commentAreas.add(commentArea);

                exerciseBox.getChildren().addAll(exerciseNameLabel, exerciseDetailsLabel, commentLabel, commentArea);
                exerciseContainer.getChildren().add(exerciseBox);
            }

            scrollPane.setContent(exerciseContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(400);
            scrollPane.setStyle("-fx-background-color: white;");

            // Nút lưu nhận xét
            Button saveButton = new Button("Lưu nhận xét");
            saveButton.setPrefWidth(120);
            saveButton.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
            saveButton.setOnAction(e -> {
                try {
                    boolean allSaved = true;
                    for (int i = 0; i < exerciseDetails.size(); i++) {
                        ExerciseWithDetails exercise = exerciseDetails.get(i);
                        String comment = commentAreas.get(i).getText().trim();

                        if (!saveExerciseComment(schedule.getId(), exercise.getExerciseId(), comment)) {
                            allSaved = false;
                        }
                    }

                    if (allSaved) {
                        showAlert(AlertType.INFORMATION, "Thành công", "Đã lưu tất cả nhận xét bài tập!");
                        dialog.close();
                    } else {
                        showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi lưu một số nhận xét!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(AlertType.ERROR, "Lỗi", "Lỗi khi lưu nhận xét: " + ex.getMessage());
                }
            });

            // Nút hủy
            Button cancelButton = new Button("Hủy");
            cancelButton.setPrefWidth(80);
            cancelButton.setStyle(
                    "-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
            cancelButton.setOnAction(e -> dialog.close());

            // HBox chứa các nút
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(saveButton, cancelButton);

            root.getChildren().addAll(titleLabel, sessionInfoLabel, scrollPane, buttonBox);

            Scene scene = new Scene(root, 1000, 550);
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Lỗi", "Không thể hiển thị dialog nhận xét bài tập");
        }
    }

    private String getExerciseComment(int scheduleId, int exerciseId) {
        String comment = "";
        String sql = "SELECT TrainerComment FROM TrainingScheduleExercises WHERE scheduleid = ? AND exerciseid = ?";

        try (java.sql.Connection conn = utils.DBConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            ps.setInt(2, exerciseId);

            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                comment = rs.getString("TrainerComment");
                if (comment == null) {
                    comment = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy nhận xét bài tập: " + e.getMessage());
        }

        return comment;
    }

    private boolean saveExerciseComment(int scheduleId, int exerciseId, String comment) {
        String sql = "UPDATE TrainingScheduleExercises SET TrainerComment = ? WHERE scheduleid = ? AND exerciseid = ?";

        try (java.sql.Connection conn = utils.DBConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, comment);
            ps.setInt(2, scheduleId);
            ps.setInt(3, exerciseId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lưu nhận xét bài tập: " + e.getMessage());
            return false;
        }
    }
}