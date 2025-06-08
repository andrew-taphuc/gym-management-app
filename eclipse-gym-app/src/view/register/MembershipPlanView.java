package view.register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MembershipPlan;
import model.User;
import view.BaseView;
import controller.MembershipPlanController;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class MembershipPlanView extends BaseView {
    @FXML
    private VBox planContainer;

    @FXML
    private Label errorLabel;

    private MembershipPlanController membershipPlanController;
    private User newUser;
    private int selectedPlanId;
    private VBox selectedCard;

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
        loadMembershipPlans();
    }

    private void loadMembershipPlans() {
        planContainer.getChildren().clear();
        List<MembershipPlan> plans = membershipPlanController.getAllPlans();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        for (MembershipPlan plan : plans) {
            String priceStr = currencyFormat.format(plan.getPrice());
            VBox card = new VBox(
                new Label("Gói: " + plan.getDuration() + " ngày"),
                new Label("Giá: " + priceStr)
                // new Label("Mô tả: " + plan.getDescription())
            );
            card.setSpacing(8);
            card.setAlignment(Pos.CENTER);
            card.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: #2196F3;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 18;" +
                "-fx-effect: dropshadow(gaussian, #888, 6, 0.2, 0, 2);" +
                "-fx-max-width: 400px;" +
                "-fx-min-width: 350px;" +
                "-fx-alignment: center;"
            );
            VBox.setMargin(card, new Insets(12, 0, 12, 0));

            // Sự kiện chọn card
            card.setOnMouseClicked(e -> {
                // Bỏ chọn card cũ
                if (selectedCard != null) {
                    selectedCard.setStyle(
                        "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-color: #2196F3;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 18;" +
                        "-fx-effect: dropshadow(gaussian, #888, 6, 0.2, 0, 2);" +
                        "-fx-max-width: 400px;" +
                        "-fx-min-width: 350px;" +
                        "-fx-alignment: center;"
                    );
                }
                // Chọn card mới
                card.setStyle(
                    "-fx-background-color: #BBDEFB;" + // màu xanh nhạt khi chọn
                    "-fx-background-radius: 18;" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-color: #1976D2;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 18;" +
                    "-fx-effect: dropshadow(gaussian, #888, 6, 0.2, 0, 2);" +
                    "-fx-max-width: 400px;" +
                    "-fx-min-width: 350px;" +
                    "-fx-alignment: center;"
                );
                selectedCard = card;
                selectedPlanId = plan.getPlanId();
                errorLabel.setText("");
                System.out.println("Gói tập với ID: " + selectedPlanId + " đã được chọn");
            });

            planContainer.getChildren().add(card);
        }
    }

    @FXML
    private void handleContinue() {
        if (selectedPlanId == 0) {
            errorLabel.setText("Vui lòng chọn một gói tập");
            return;
        }
        MembershipPlan selectedPlan = membershipPlanController.getPlanByID(selectedPlanId);
        if (selectedPlan == null) {
            errorLabel.setText("Không tìm thấy gói tập đã chọn");
            return;
        }
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