package view.adminView;

import controller.TrainingRegistrationController;
import controller.TrainingScheduleController;
import controller.TrainingController;
import controller.MemberController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Member;
import model.User;
import model.Membership;
import model.TrainingRegistration;
import model.Attendance;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import model.enums.enum_MembershipStatus;
import model.enums.enum_MemberStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberTrackController {
    @FXML private Label lblFullName;
    @FXML private Label lblPhone;
    @FXML private Label lblMemberCode;
    @FXML private Label lblJoinDate;
    @FXML private Label lblStatus;
    @FXML private Label lblSessionCount;
    @FXML private TableView<Membership> tblMemberships;
    @FXML private TableView<Attendance> tblAttendance;
    @FXML private Button btnCheckinGym;
    @FXML private Button btnCheckinPT;

    @FXML private TableColumn<Membership, String> colPlanName;
    @FXML private TableColumn<Membership, String> colStartDate;
    @FXML private TableColumn<Membership, String> colEndDate;
    @FXML private TableColumn<Membership, String> colStatus;

    @FXML private TableColumn<Attendance, String> colCheckinDate;
    @FXML private TableColumn<Attendance, String> colCheckinTime;
    @FXML private TableColumn<Attendance, String> colMembership;
    @FXML private TableColumn<Attendance, String> colType;

    private User currentUser;
    private Member member;
    private TrainingController trainingController = new TrainingController();

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setMember(Member member) {
        this.member = member;
        loadMemberInfo();
        loadMemberships();
        loadAttendance();
        loadSessionCount();
        trainingController.updateExpiredTrainingSchedules();
    }

    private void loadMemberInfo() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (member != null) {
            MemberController memberController = new MemberController();
            User user = memberController.getUserById(member.getUserId());
            if (user != null) {
                lblFullName.setText(user.getFullName());
                lblPhone.setText(user.getPhoneNumber());
            } else {
                lblFullName.setText("");
                lblPhone.setText("");
            }
            lblMemberCode.setText(member.getMemberCode());
            lblJoinDate.setText(member.getJoinDate() != null ? member.getJoinDate().format(dateFormatter) : "");
            lblStatus.setText(member.getStatus() != null
                ? (member.getStatus() == enum_MemberStatus.ACTIVE ? "Hoạt động" : "Chưa kích hoạt")
                : "");
        } else {
            lblFullName.setText("");
            lblPhone.setText("");
            lblMemberCode.setText("");
            lblJoinDate.setText("");
            lblStatus.setText("");
        }
    }

    private void loadMemberships() {
        // Lấy danh sách các gói tập của hội viên
        List<Membership> memberships = MemberController.getMembershipsByMemberId(member.getMemberId());
        tblMemberships.getItems().setAll(memberships);
    }

    private void loadAttendance() {
        // Lấy 5 lần check-in gần nhất
        List<Attendance> attendanceList = MemberController.getRecentAttendance(member.getMemberId(), 5);
        tblAttendance.getItems().setAll(attendanceList);
    }

    private void loadSessionCount() {
        // Đếm số buổi đã tập trong tháng này
        int count = MemberController.countAttendanceThisMonth(member.getMemberId(), LocalDate.now());
        lblSessionCount.setText(String.valueOf(count));
    }

    @FXML
    public void handleCheckinGym() {
        // Lấy gói GYM hợp lệ (không phải PT, còn hạn)
        List<Membership> memberships = MemberController.getMembershipsByMemberId(member.getMemberId());
        Membership gymMembership = memberships.stream()
            .filter(m -> !m.isPersonalTraining() && m.isActive()) // isPersonalTraining() == false là GYM
            .findFirst()
            .orElse(null);

        if (gymMembership == null) {
            showAlert("Bạn không còn gói GYM hợp lệ hoặc gói đã hết hạn!");
            return;
        }

        boolean success = MemberController.checkinGym(member.getMemberId(), gymMembership.getMembershipId());
        if (success) {
            loadAttendance();
            loadSessionCount();
            showAlert("Check-in phòng tập thành công!");
        } else {
            showAlert("Check-in thất bại!");
        }
    }

    @FXML
    public void handleCheckinPT() {
        // Lấy danh sách gói PT còn hiệu lực
        List<TrainingRegistration> ptRegistrations = TrainingRegistrationController.getActivePTRegistrationsByMemberId(member.getMemberId());
        TrainingRegistration ptRegistration = ptRegistrations.stream()
            .filter(r -> r.isActive())
            .findFirst()
            .orElse(null);

        if (ptRegistration == null) {
            showAlert("Bạn không còn gói PT hợp lệ hoặc gói PT đã hết hạn!");
            return;
        }

        // Lấy gói Membership đang hoạt động (để lấy membershipId cho Attendance)
        List<Membership> memberships = MemberController.getMembershipsByMemberId(member.getMemberId());
        Membership activeMembership = memberships.stream()
            .filter(Membership::isActive)
            .findFirst()
            .orElse(null);

        if (activeMembership == null) {
            showAlert("Bạn không còn gói tập hợp lệ!");
            return;
        }

        // Lấy danh sách tất cả các buổi PT đã lên lịch của hội viên
        List<model.TrainingSchedule> schedules = TrainingScheduleController.getScheduledPTByMemberId(member.getMemberId());
        if (schedules.isEmpty()) {
            showAlert("Không có buổi PT nào đã lên lịch!");
            return;
        }

        showScheduleTableDialog(schedules, activeMembership, ptRegistration);
    }

    private void showScheduleTableDialog(List<model.TrainingSchedule> schedules, Membership activeMembership, TrainingRegistration ptRegistration) {
        Dialog<model.TrainingSchedule> dialog = new Dialog<>();
        dialog.setTitle("Chọn buổi PT để check-in");
        dialog.setHeaderText("Chọn một buổi tập PT đã lên lịch để check-in:");
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(400);
        dialog.getDialogPane().setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-radius: 16px;" +
            "-fx-background-radius: 16px;" +
            "-fx-font-size: 16px;"
        );

        ButtonType checkinButtonType = new ButtonType("Check-in", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(checkinButtonType, ButtonType.CANCEL);

        TableView<model.TrainingSchedule> tableView = new TableView<>();
        TableColumn<model.TrainingSchedule, String> colDate = new TableColumn<>("Ngày");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        colDate.setPrefWidth(120);

        TableColumn<model.TrainingSchedule, String> colTime = new TableColumn<>("Giờ");
        colTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
        colTime.setPrefWidth(120);

        TableColumn<model.TrainingSchedule, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRoomId())));
        colRoom.setPrefWidth(100);

        TableColumn<model.TrainingSchedule, String> colNotes = new TableColumn<>("Ghi chú");
        colNotes.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));
        colNotes.setPrefWidth(235);

        tableView.getColumns().addAll(colDate, colTime, colRoom, colNotes);
        tableView.getItems().setAll(schedules);
        tableView.setPrefHeight(250);

        // CSS cho TableView trong dialog
        tableView.setStyle(
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-font-size: 15px;"
        );

        dialog.getDialogPane().setContent(tableView);
        // CSS cho các button trong dialog
        dialog.getDialogPane().lookupButton(checkinButtonType).setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4caf50, #388e3c);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;"
        );
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color:rgb(255, 199, 16);" +
            "-fx-text-fill: black;" +
            "-fx-background-radius: 8px;" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;"
        );

        // Chỉ enable nút Check-in khi có chọn dòng
        Node checkinButton = dialog.getDialogPane().lookupButton(checkinButtonType);
        checkinButton.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            checkinButton.setDisable(newSel == null);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == checkinButtonType) {
                return tableView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedSchedule -> {
            // Chỉ cho phép check-in nếu ScheduleDate là hôm nay
            if (!selectedSchedule.getDate().equals(java.time.LocalDate.now())) {
                showAlert("Chỉ được phép check-in buổi tập trong ngày hôm nay!");
                return;
            }
            boolean success = TrainingRegistrationController.checkinPT(
                member.getMemberId(),
                activeMembership.getMembershipId(),
                ptRegistration.getRegistrationId(),
                selectedSchedule.getId()
            );
            if (success) {
                loadAttendance();
                loadSessionCount();
                showAlert("Check-in PT thành công!");
            } else {
                showAlert("Check-in PT thất bại!");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        // Memberships
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        colPlanName.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getPlanName())
        );
        colStartDate.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getStartDate() != null
                    ? cellData.getValue().getStartDate().format(dateFormatter)
                    : ""
            )
        );
        colEndDate.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getEndDate() != null
                    ? cellData.getValue().getEndDate().format(dateFormatter)
                    : ""
            )
        );
        colStatus.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getStatus() == enum_MembershipStatus.ACTIVE ? "Hoạt động" : "Chưa kích hoạt"
            )
        );

        // Attendance
        colCheckinDate.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getCheckInTime() != null
                    ? cellData.getValue().getCheckInTime().toLocalDate().format(dateFormatter)
                    : ""
            )
        );
        colCheckinTime.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getCheckInTime() != null
                    ? cellData.getValue().getCheckInTime().toLocalTime().format(timeFormatter)
                    : ""
            )
        );
        colMembership.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getPlanName())
        );
        colType.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getTrainingScheduleId() != null ? "Dịch vụ PT" : "Thông thường"
            )
        );
    }
}
