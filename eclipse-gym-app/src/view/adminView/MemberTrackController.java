package view.adminView;

import controller.TrainingRegistrationController;
import controller.TrainingScheduleController;
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

import java.time.LocalDate;
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
    @FXML private TableColumn<Membership, LocalDate> colStartDate;
    @FXML private TableColumn<Membership, LocalDate> colEndDate;
    @FXML private TableColumn<Membership, String> colStatus;

    @FXML private TableColumn<Attendance, String> colCheckinTime;
    @FXML private TableColumn<Attendance, String> colType;

    private User currentUser;
    private Member member;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setMember(Member member) {
        this.member = member;
        loadMemberInfo();
        loadMemberships();
        loadAttendance();
        loadSessionCount();
    }

    private void loadMemberInfo() {
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
            lblJoinDate.setText(member.getJoinDate() != null ? member.getJoinDate().toString() : "");
            lblStatus.setText(member.getStatus() != null ? member.getStatus().toString() : "");
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

        ButtonType checkinButtonType = new ButtonType("Check-in", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(checkinButtonType, ButtonType.CANCEL);

        TableView<model.TrainingSchedule> tableView = new TableView<>();
        TableColumn<model.TrainingSchedule, String> colDate = new TableColumn<>("Ngày");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        TableColumn<model.TrainingSchedule, String> colTime = new TableColumn<>("Giờ");
        colTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
        TableColumn<model.TrainingSchedule, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRoomId())));
        TableColumn<model.TrainingSchedule, String> colNotes = new TableColumn<>("Ghi chú");
        colNotes.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotes()));

        tableView.getColumns().addAll(colDate, colTime, colRoom, colNotes);
        tableView.getItems().setAll(schedules);
        tableView.setPrefHeight(250);

        dialog.getDialogPane().setContent(tableView);

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
        colPlanName.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPlanName()) // hoặc getPlanId() nếu chưa có getPlanName()
        );
        colStartDate.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getStartDate())
        );
        colEndDate.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getEndDate())
        );
        colStatus.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString())
        );

        // Attendance
        colCheckinTime.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCheckInTime().toString())
        );
        colType.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getType())
        );
    }
}
