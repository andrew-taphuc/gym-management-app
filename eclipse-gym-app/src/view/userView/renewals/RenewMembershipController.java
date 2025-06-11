package view.userView.renewals;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import model.User;
import model.Membership;
import model.MembershipPlan;
import controller.MembershipController;
import controller.MembershipPlanController;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class RenewMembershipController {
    @FXML
    private Label currentMembershipLabel;

    @FXML
    private TableView<MembershipPlan> plansTable;

    @FXML
    private TableColumn<MembershipPlan, String> planIdColumn;

    @FXML
    private TableColumn<MembershipPlan, String> planNameColumn;

    @FXML
    private TableColumn<MembershipPlan, String> planDurationColumn;

    @FXML
    private TableColumn<MembershipPlan, String> planPriceColumn;

    @FXML
    private TableColumn<MembershipPlan, String> planDescriptionColumn;

    @FXML
    private HBox buttonContainer;

    private User currentUser;
    private Membership currentMembership;
    private MembershipController membershipController;
    private MembershipPlanController membershipPlanController;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.membershipController = new MembershipController();
        this.membershipPlanController = new MembershipPlanController();
        loadData();
    }

    public void setCurrentMembership(Membership membership) {
        this.currentMembership = membership;
        updateCurrentMembershipLabel();
    }

    private void updateCurrentMembershipLabel() {
        if (currentMembership != null && currentMembership.getEndDate() != null) {
            String endDate = currentMembership.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            currentMembershipLabel.setText("Gói tập hiện tại của bạn còn hạn đến ngày " + endDate);
        }
    }

    private void loadData() {
        initializeTable();
        loadPlans();
    }

    private void initializeTable() {
        planIdColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPlanId())));

        planNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlanName()));

        planDurationColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDuration() + " ngày"));

        planPriceColumn.setCellValueFactory(cellData -> {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return new SimpleStringProperty(formatter.format(cellData.getValue().getPrice()));
        });

        planDescriptionColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }

    private void loadPlans() {
        List<MembershipPlan> plans = membershipPlanController.getAllPlans();
        plansTable.getItems().setAll(plans);
    }

    @FXML
    private void handleRenew() {
        MembershipPlan selectedPlan = plansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Vui lòng chọn gói tập cần gia hạn");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("renew_payment.fxml"));
            Parent root = loader.load();

            RenewPaymentController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setCurrentMembership(currentMembership);
            controller.setSelectedPlan(selectedPlan);

            Stage stage = new Stage();
            stage.setTitle("Thanh toán gia hạn gói tập");
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng cửa sổ chọn gói tập hiện tại
            Stage currentStage = (Stage) plansTable.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình thanh toán");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}