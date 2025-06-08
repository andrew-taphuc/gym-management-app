package view.userView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import model.Membership;
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

public class PlansRenewalsController {
    @FXML
    private Label pageTitle;

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
    private TableColumn<TrainingRegistration, String> trainerNameColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> regStartDateColumn;

    @FXML
    private TableColumn<TrainingRegistration, String> sessionsLeftColumn;

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
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                membershipsTable.getItems().setAll(memberships);
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
}