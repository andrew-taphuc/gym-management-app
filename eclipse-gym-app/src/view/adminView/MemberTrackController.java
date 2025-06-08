package view.adminView;

import controller.MemberController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Member;
import model.User;
import model.Membership;
import model.Attendance;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

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
        // 1. Kiểm tra còn gói PT hợp lệ không
        List<Membership> memberships = MemberController.getMembershipsByMemberId(member.getMemberId());
        Membership ptMembership = memberships.stream()
            .filter(m -> m.isPersonalTraining() && m.isActive()) // Bạn cần hiện thực 2 hàm này
            .findFirst()
            .orElse(null);

        if (ptMembership == null) {
            showAlert("Bạn không còn gói PT hợp lệ hoặc gói PT đã hết hạn!");
            return;
        }

        // 2. Lấy TrainingScheduleID của buổi PT hôm nay (hoặc cho phép chọn)
        int trainingScheduleId = MemberController.getTodayPTScheduleId(member.getMemberId(), ptMembership.getMembershipId());
        if (trainingScheduleId == -1) {
            showAlert("Không tìm thấy lịch PT hôm nay!");
            return;
        }

        // 3. Thực hiện check-in
        boolean success = MemberController.checkinPT(member.getMemberId(), ptMembership.getMembershipId(), trainingScheduleId);
        if (success) {
            loadAttendance();
            loadSessionCount();
            showAlert("Check-in PT thành công!");
        } else {
            showAlert("Check-in PT thất bại!");
        }
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
