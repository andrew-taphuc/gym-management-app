package view.userView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import model.Membership;
import model.enums.enum_MembershipStatus;
import controller.MembershipController;
import controller.UserController;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import model.TrainingRegistration;
import controller.TrainingRegistrationController;
import model.Trainer;
import controller.TrainerController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DBConnection;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import java.time.LocalDate;
import view.userView.renewals.RenewMembershipController;
import view.userView.trainingRegister.TrainingPlanSelectionController;

public class PlansRenewalsController {
    @FXML
    private Label pageTitle;

    @FXML
    private Label expirationNoticeLabel;

    @FXML
    private TableView<Membership> membershipsTable;

    @FXML
    private TableColumn<Membership, Integer> membershipIdColumn;

    @FXML
    private TableColumn<Membership, String> membershipNameColumn;

    @FXML
    private TableColumn<Membership, String> durationColumn;

    @FXML
    private TableColumn<Membership, String> startDateColumn;

    @FXML
    private TableColumn<Membership, String> endDateColumn;

    @FXML
    private TableColumn<Membership, String> membershipTypeColumn;

    @FXML
    private TableColumn<Membership, String> statusColumn;

    @FXML
    private TableView<TrainingRegistration> trainingRegistrationsTable;

    @FXML
    private TableColumn<TrainingRegistration, String> regIdColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> planNameColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> planTypeColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> trainerNameColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> regStartDateColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> sessionsLeftColumn;

    @FXML
    private Button renewButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button buyTrainerButton;

    private User currentUser;
    private MembershipController membershipController;
    private TrainingRegistrationController trainingRegistrationController;
    private UserController userController;
    private boolean isInitialized = false;

    @FXML
    public void initialize() {
        pageTitle.setText("Đây là trang Plans & Renewals user");
        trainingRegistrationController = new TrainingRegistrationController();

        if (currentUser != null) {
            initializeTable();
            loadMemberships();
            loadTrainingRegistrations();
        }
        isInitialized = true;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.membershipController = new MembershipController();
        this.userController = new UserController();

        if (isInitialized && membershipsTable != null) {
            initializeTable();
            loadMemberships();
            loadTrainingRegistrations();
        }
    }

    private void initializeTable() {
        if (membershipsTable != null) {
            membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
            membershipNameColumn.setCellValueFactory(cellData -> {
                Membership membership = cellData.getValue();
                if (membership != null && membership.getPlan() != null) {
                    return new SimpleStringProperty(membership.getPlan().getPlanName());
                }
                return new SimpleStringProperty("");
            });
            durationColumn.setCellValueFactory(cellData -> {
                Membership membership = cellData.getValue();
                if (membership != null && membership.getPlan() != null) {
                    return new SimpleStringProperty(membership.getPlan().getDuration() + " ngày");
                }
                return new SimpleStringProperty("");
            });
            startDateColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getStartDate() != null) {
                    return new SimpleStringProperty(
                            cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                return new SimpleStringProperty("");
            });
            endDateColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getEndDate() != null) {
                    return new SimpleStringProperty(
                            cellData.getValue().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                return new SimpleStringProperty("");
            });
            membershipTypeColumn.setCellValueFactory(cellData -> {
                Membership membership = cellData.getValue();
                if (membership != null) {
                    return new SimpleStringProperty(membership.getRenewalTo() == null ? "Đăng ký mới" : "Gia hạn");
                }
                return new SimpleStringProperty("");
            });
            statusColumn.setCellValueFactory(cellData -> {
                Membership membership = cellData.getValue();
                if (membership != null && membership.getStatus() != null) {
                    return new SimpleStringProperty(membership.getStatus().getValue());
                }
                return new SimpleStringProperty("");
            });
        }

        if (trainingRegistrationsTable != null) {
            regIdColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(String.valueOf(cellData.getValue().getRegistrationId()));
            });

            planNameColumn.setCellValueFactory(cellData -> {
                String planName = cellData.getValue().getPlan() != null ? cellData.getValue().getPlan().getPlanName()
                        : "";
                return new SimpleStringProperty(planName);
            });

            planTypeColumn.setCellValueFactory(cellData -> {
                String planType = cellData.getValue().getPlan() != null
                        ? cellData.getValue().getPlan().getType().getValue()
                        : "";
                return new SimpleStringProperty(planType);
            });

            trainerNameColumn.setCellValueFactory(cellData -> {
                String trainerName = cellData.getValue().getTrainerName();
                return new SimpleStringProperty(trainerName != null ? trainerName : "Chưa có HLV");
            });

            regStartDateColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue().getStartDate() != null) {
                    return new SimpleStringProperty(
                            cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                return new SimpleStringProperty("");
            });

            sessionsLeftColumn.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(String.valueOf(cellData.getValue().getSessionsLeft()));
            });
        }
    }

    private void loadMemberships() {
        if (currentUser != null && membershipsTable != null) {
            Integer memberId = userController.getMemberIDByUserID(currentUser.getUserId());
            if (memberId != null) {
                List<Membership> memberships = membershipController.getMembershipsByMemberID(memberId);
                System.out.println("Danh sách gói tập của hội viên:");
                for (Membership membership : memberships) {
                    System.out.println("Membership ID: " + membership.getMembershipId());
                    if (membership.getPlan() != null) {
                        System.out.println("- Tên gói tập: " + membership.getPlan().getPlanName());
                    } else {
                        System.out.println("- Không có thông tin gói tập");
                    }
                }

                // Sắp xếp danh sách: ACTIVE lên trên, sau đó sắp xếp theo ngày bắt đầu
                memberships.sort((m1, m2) -> {
                    // Ưu tiên ACTIVE lên trên
                    if (m1.getStatus() == model.enums.enum_MembershipStatus.ACTIVE &&
                            m2.getStatus() != model.enums.enum_MembershipStatus.ACTIVE) {
                        return -1;
                    }
                    if (m1.getStatus() != model.enums.enum_MembershipStatus.ACTIVE &&
                            m2.getStatus() == model.enums.enum_MembershipStatus.ACTIVE) {
                        return 1;
                    }
                    // Nếu cùng trạng thái, sắp xếp theo ngày bắt đầu
                    return m1.getStartDate().compareTo(m2.getStartDate());
                });

                membershipsTable.getItems().setAll(memberships);

                // Tìm ngày hết hạn cuối cùng của các gói "Hoạt động"
                updateExpirationNotice(memberships);
            }
        }
    }

    private void loadTrainingRegistrations() {
        if (currentUser != null && trainingRegistrationsTable != null) {
            Integer memberId = userController.getMemberIDByUserID(currentUser.getUserId());
            if (memberId != null) {
                List<TrainingRegistration> registrations = trainingRegistrationController
                        .getTrainingRegistrationsByMemberId(memberId);
                trainingRegistrationsTable.getItems().setAll(registrations);
                trainingRegistrationsTable.refresh();
            }
        }
    }

    private void updateExpirationNotice(List<Membership> memberships) {
        if (expirationNoticeLabel == null) {
            return;
        }

        // Tìm ngày hết hạn cuối cùng trong số các gói có trạng thái "Hoạt động"
        LocalDate latestEndDate = null;
        for (Membership membership : memberships) {
            if (membership.getStatus() == model.enums.enum_MembershipStatus.ACTIVE &&
                    membership.getEndDate() != null) {
                if (latestEndDate == null || membership.getEndDate().isAfter(latestEndDate)) {
                    latestEndDate = membership.getEndDate();
                }
            }
        }

        // Hiển thị thông báo
        if (latestEndDate != null) {
            String formattedDate = latestEndDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            expirationNoticeLabel.setText("Thẻ tập của bạn sẽ hết hạn vào ngày " + formattedDate);
        } else {
            expirationNoticeLabel.setText("Bạn chưa có gói tập nào đang hoạt động");
        }
    }

    @FXML
    private void handleRenewClick() {
        // Lấy gói tập hiện tại
        Membership currentMembership = membershipsTable.getSelectionModel().getSelectedItem();
        if (currentMembership == null) {
            showAlert("Vui lòng chọn gói tập cần gia hạn");
            return;
        }

        // Kiểm tra trạng thái gói tập
        if (currentMembership.getStatus() != model.enums.enum_MembershipStatus.ACTIVE) {
            showAlert("Chỉ có thể gia hạn gói tập đang hoạt động");
            return;
        }

        // Kiểm tra xem gói tập đã được gia hạn chưa
        if (membershipController.isMembershipAlreadyRenewed(currentMembership.getMembershipId())) {
            showAlert("Gói tập này đã được gia hạn trước đó và không thể gia hạn lại");
            return;
        }

        // Mở màn hình gia hạn
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("renewals/renew_membership.fxml"));
            Parent root = loader.load();

            RenewMembershipController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setCurrentMembership(currentMembership);

            Stage stage = new Stage();
            stage.setTitle("Gia hạn gói tập");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình gia hạn");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Phương thức để refresh dữ liệu sau khi có thay đổi
    public void refreshData() {
        if (currentUser != null) {
            loadMemberships();
            loadTrainingRegistrations();
        }
    }

    @FXML
    private void handleRefreshClick() {
        refreshData();
    }

    @FXML
    private void handleBuyTrainerClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("trainingRegister/training_plan_selection.fxml"));
            Parent root = loader.load();

            TrainingPlanSelectionController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Chọn gói tập với HLV");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình chọn gói HLV");
        }
    }
}