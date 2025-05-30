package view.register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MembershipPlan;
import model.User;
import model.Payment;
import model.Membership;
import controller.PaymentController;
import controller.UserController;
import controller.MembershipController;
import controller.MemberController;
import model.enums.enum_PaymentStatus;
import view.BaseView;
import view.userView.HomeView;
import model.enums.enum_MembershipStatus;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentView extends BaseView {
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cvvField;
    @FXML
    private Label errorLabel;

    private MembershipPlan selectedPlan;
    private User newUser;
    private PaymentController paymentController;
    private UserController userController;
    private MembershipController membershipController;
    private MemberController memberController;
    private List<String> validCardNumbers;
    private List<String> validCVVs;

    public PaymentView(Stage stage) {
        super(stage);
        paymentController = new PaymentController();
        userController = new UserController();
        membershipController = new MembershipController();
        memberController = new MemberController();
        loadValidCards();
    }

    private void loadValidCards() {
        validCardNumbers = new ArrayList<>();
        validCVVs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("CREDIT_CARD.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    validCardNumbers.add(parts[0].trim());
                    validCVVs.add(parts[1].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMembershipPlan(MembershipPlan plan) {
        this.selectedPlan = plan;
    }

    public void setNewUser(User user) {
        this.newUser = user;
    }

    @FXML
    private void handlePayment() {
        String cardNumber = cardNumberField.getText().trim();
        String cvv = cvvField.getText().trim();

        // Kiểm tra thông tin thẻ
        if (!validateCard(cardNumber, cvv)) {
            errorLabel.setText("Thông tin thẻ không hợp lệ");
            return;
        }

        try {
            // 1. Tạo user
            int userId = userController.createUser(newUser);
            if (userId == -1) {
                errorLabel.setText("Lỗi khi tạo tài khoản");
                return;
            }
            System.out.println("✅ Tạo tài khoản thành công! UserID: " + userId);

            // 2. Tạo member với UserID vừa tạo
            int memberId = memberController.createMember(userId);
            if (memberId == -1) {
                errorLabel.setText("Lỗi khi tạo hội viên");
                return;
            }
            System.out.println("✅ Tạo hội viên thành công! MemberID: " + memberId);

            // 3. Tạo payment
            Payment payment = new Payment();
            payment.setAmount(selectedPlan.getPrice());
            payment.setPaymentMethod("Credit Card");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(enum_PaymentStatus.COMPLETED);
            payment.setNotes("Thanh toán gói tập " + selectedPlan.getPlanName());

            int paymentId = paymentController.createPayment(payment);
            if (paymentId == -1) {
                errorLabel.setText("Lỗi khi tạo thanh toán");
                return;
            }
            System.out.println("✅ Thanh toán thành công! PaymentID: " + paymentId);

            // 4. Tạo membership với UserID, MemberID và PaymentID vừa tạo
            Membership membership = new Membership();
            membership.setUserId(userId);
            membership.setMemberId(memberId);
            membership.setPlanId(selectedPlan.getPlanId());
            membership.setStartDate(LocalDate.now());
            membership.setEndDate(LocalDate.now().plusDays(selectedPlan.getDuration()));
            // membership.setStatus(enum_MembershipStatus.INACTIVE);
            membership.setPaymentId(paymentId);

            boolean membershipCreated = membershipController.createMembership(membership);
            if (!membershipCreated) {
                errorLabel.setText("Lỗi khi tạo gói tập");
                return;
            }
            System.out.println("✅ Tạo gói tập thành công!");

            // Hiển thị dialog thành công
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng ký thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đăng kí thành công, vui lòng đăng nhập để sử dụng dịch vụ.");
            alert.getDialogPane().getStylesheets().clear();
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/view/dialog.css").toExternalForm());
            alert.showAndWait();

            // Chuyển đến trang đăng nhập
            view.LoginView loginView = new view.LoginView(stage);
            loginView.loadView("/view/login.fxml");

        } catch (Exception e) {
            errorLabel.setText("Lỗi thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateCard(String cardNumber, String cvv) {
        for (int i = 0; i < validCardNumbers.size(); i++) {
            if (validCardNumbers.get(i).equals(cardNumber) && validCVVs.get(i).equals(cvv)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void handleBack() {
        MembershipPlanView planView = new MembershipPlanView(stage);
        planView.setNewUser(newUser);
        planView.loadView("/view/register/membership_plan.fxml");
    }
}