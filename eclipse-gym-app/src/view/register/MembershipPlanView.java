package view.register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.MembershipPlan;
import model.User;
import view.BaseView;
import controller.MembershipPlanController;
import java.util.List;

public class MembershipPlanView extends BaseView {
    @FXML
    private TableView<MembershipPlan> planTable;
    @FXML
    private TableColumn<MembershipPlan, String> nameColumn;
    @FXML
    private TableColumn<MembershipPlan, Integer> durationColumn;
    @FXML
    private TableColumn<MembershipPlan, Double> priceColumn;
    @FXML
    private TableColumn<MembershipPlan, String> descriptionColumn;
    @FXML
    private Label errorLabel;

    private MembershipPlanController membershipPlanController;
    private User newUser;
    private int selectedPlanId;

    public MembershipPlanView(Stage stage) {
        super(stage);
        membershipPlanController = new MembershipPlanController();
    }

    public void setNewUser(User user) {
        this.newUser = user;
    }

    public int getSelectedPlanId() {
        return selectedPlanId;
    }

    @FXML
    public void initialize() {
        // Thiết lập các cột cho bảng
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Tải danh sách gói tập
        loadMembershipPlans();
    }

    private void loadMembershipPlans() {
        List<MembershipPlan> plans = membershipPlanController.getAllPlans();
        planTable.getItems().clear();
        planTable.getItems().addAll(plans);
    }

    @FXML
    private void handleContinue() {
        MembershipPlan selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            errorLabel.setText("Vui lòng chọn một gói tập");
            return;
        }

        // Lưu lại planID đã chọn
        selectedPlanId = selectedPlan.getPlanId();
        System.out.println("✅ Đã chọn gói tập với ID: " + selectedPlanId);

        // Chuyển đến trang thanh toán
        PaymentView paymentView = new PaymentView(stage);
        paymentView.setMembershipPlan(selectedPlan);
        paymentView.setNewUser(newUser);
        paymentView.loadView("/view/register/payment.fxml");
    }

    @FXML
    private void handleBack() {
        RegisterView registerView = new RegisterView(stage);
        registerView.loadView("/view/register/register.fxml");
    }
}