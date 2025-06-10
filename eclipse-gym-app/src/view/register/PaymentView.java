package view.register;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MembershipPlan;
import model.User;
import model.Payment;
import model.Promotion;
import model.Membership;
import controller.PaymentController;
import controller.UserController;
import controller.MembershipController;
import controller.MemberController;
import controller.PromosController;
import model.enums.enum_PaymentStatus;
import view.BaseView;
import view.adminView.PlansRenewalsController;
import view.userView.HomeView;
import model.enums.enum_MembershipStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
    @FXML
    private Label planInfoLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private TextField promoCodeField;
    @FXML
    private Label promoStatusLabel;

    private MembershipPlan selectedPlan;
    private User newUser;
    private PaymentController paymentController;
    private UserController userController;
    private MembershipController membershipController;
    private MemberController memberController;
    private List<String> validCardNumbers;
    private List<String> validCVVs;
    private List<Double> balances; // Thêm biến lưu số dư
    private double discountedAmount = 0;
    private Promotion appliedPromotion;
    private PromosController promosController = new PromosController();

    public PaymentView(Stage stage) {
        super(stage);
        paymentController = new PaymentController();
        userController = new UserController();
        membershipController = new MembershipController();
        memberController = new MemberController();
        loadValidCards();
    }

    // Đọc danh sách thẻ và số dư
    private void loadValidCards() {
        validCardNumbers = new ArrayList<>();
        validCVVs = new ArrayList<>();
        balances = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("CREDIT_CARD.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    validCardNumbers.add(parts[0].trim());
                    validCVVs.add(parts[1].trim());
                    balances.add(Double.parseDouble(parts[2].trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    public void setMembershipPlan(MembershipPlan plan) {
        this.selectedPlan = plan;
        discountedAmount = selectedPlan.getPrice();
        // Không gọi updateAmountLabel() ở đây!
        // Để updateAmountLabel() trong initialize()
    }

    public void setNewUser(User user) {
        this.newUser = user;
    }

    @FXML
    private void handlePayment() {
        String cardNumber = cardNumberField.getText().trim();
        String cvv = cvvField.getText().trim();

        // Kiểm tra thông tin thẻ
        int cardIndex = getCardIndex(cardNumber, cvv);
        if (cardIndex == -1) {
            errorLabel.setText("Thông tin thẻ không hợp lệ");
            return;
        }

        double balance = balances.get(cardIndex);
        double priceToPay = discountedAmount > 0 ? discountedAmount : selectedPlan.getPrice();
        if (balance < priceToPay) {
            errorLabel.setText("Số dư không đủ để thanh toán");
            return;
        }

        try {
            // Trừ số dư và cập nhật file
            balances.set(cardIndex, balance - priceToPay);
            updateCardBalanceFile();

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
            payment.setAmount(priceToPay); // Sử dụng số tiền đã giảm giá (nếu có)
            payment.setPaymentMethod("Credit Card");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(enum_PaymentStatus.COMPLETED);
            payment.setPromotionId(appliedPromotion != null ? appliedPromotion.getPromotionId() : null);
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

            // Chuyển đến trang đăng nhập
            if(UserController.getCurrentUser() == null) {
                // Hiển thị dialog thành công
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Đăng ký thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đăng kí thành công, vui lòng đăng nhập để sử dụng dịch vụ.");
                alert.getDialogPane().getStylesheets().clear();
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/view/dialog.css").toExternalForm());
                alert.showAndWait();
                view.LoginView loginView = new view.LoginView(stage);
                loginView.loadView("/view/login.fxml");

            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Đăng ký thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đăng kí tài khoản cho người dùng mới thành công.");
                alert.getDialogPane().getStylesheets().clear();
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/view/dialog.css").toExternalForm());
                alert.showAndWait();
                stage.close();
            }

        } catch (Exception e) {
            errorLabel.setText("Lỗi thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getCardIndex(String cardNumber, String cvv) {
        for (int i = 0; i < validCardNumbers.size(); i++) {
            if (validCardNumbers.get(i).equals(cardNumber) && validCVVs.get(i).equals(cvv)) {
                return i;
            }
        }
        return -1;
    }

    private void updateCardBalanceFile() {
        // Cập nhật lại file CREDIT_CARD.txt với danh sách thẻ và số dư mới
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("CREDIT_CARD.txt"))) {
            for (int i = 0; i < validCardNumbers.size(); i++) {
                writer.write(validCardNumbers.get(i) + "," + validCVVs.get(i) + "," + balances.get(i));
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        MembershipPlanView planView = new MembershipPlanView(stage);
        planView.setNewUser(newUser);
        planView.loadView("/view/register/membership_plan.fxml");
    }

    // Đảm bảo cập nhật lại khi view đã load xong
    @FXML
    public void initialize() {
        if (selectedPlan != null) {
            discountedAmount = selectedPlan.getPrice();
            updateAmountLabel();
            planInfoLabel.setText("Bạn đã chọn gói " + selectedPlan.getDuration() + " ngày");
        }
    }

    @FXML
    private void handleApplyPromo() {
        String code = promoCodeField.getText().trim();
        if (code.isEmpty()) {
            promoStatusLabel.setText("Vui lòng nhập mã giảm giá nếu có");
            promoStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        Promotion promo = promosController.getPromotionByCode(code);
        if (promo == null) {
            promoStatusLabel.setText("Mã không tồn tại");
            promoStatusLabel.setStyle("-fx-text-fill: red;");
            appliedPromotion = null;
            discountedAmount = selectedPlan.getPrice();
            updateAmountLabel();
            return;
        }
        // Kiểm tra trạng thái và thời gian
        if (!"Còn hạn".equalsIgnoreCase(promo.getStatus())) {
            promoStatusLabel.setText("Mã không còn hiệu lực hoặc chưa khả dụng");
            promoStatusLabel.setStyle("-fx-text-fill: red;");
            appliedPromotion = null;
            discountedAmount = selectedPlan.getPrice();
            updateAmountLabel();
            return;
        }
        // Kiểm tra thời gian
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.isBefore(promo.getStartDate()) || now.isAfter(promo.getEndDate())) {
            promoStatusLabel.setText("Mã đã hết hạn");
            promoStatusLabel.setStyle("-fx-text-fill: red;");
            appliedPromotion = null;
            discountedAmount = selectedPlan.getPrice();
            updateAmountLabel();
            return;
        }
        // Áp dụng giảm giá
        appliedPromotion = promo;
        discountedAmount = calculateDiscountedAmount(selectedPlan.getPrice(), promo);
        promoStatusLabel.setText("Áp dụng thành công!");
        promoStatusLabel.setStyle("-fx-text-fill: green;");
        updateAmountLabel();
    }

    private double calculateDiscountedAmount(double original, Promotion promo) {
        if ("Phần trăm".equalsIgnoreCase(promo.getDiscountType())) {
            return Math.max(0, original - (original * promo.getDiscountValue() / 100.0));
        } else if ("Tiền mặt".equalsIgnoreCase(promo.getDiscountType())) {
            return Math.max(0, original - promo.getDiscountValue());
        }
        return original;
    }

    private void updateAmountLabel() {
        java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        amountLabel.setText("Số tiền cần đóng: " + currencyFormat.format(discountedAmount));
    }
}