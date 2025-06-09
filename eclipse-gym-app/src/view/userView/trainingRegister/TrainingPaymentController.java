package view.userView.trainingRegister;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import model.TrainingPlan;
import model.TrainingRegistration;
import model.Payment;
import model.User;
import model.Promotion;
import controller.TrainingRegistrationController;
import controller.PaymentController;
import controller.PromosController;
import controller.UserController;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import javafx.scene.layout.HBox;

public class TrainingPaymentController {
    @FXML
    private Label selectedPlanLabel;
    @FXML
    private Text originalAmountText;
    @FXML
    private Text totalAmountText;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cvvField;
    @FXML
    private TextField promoCodeField;
    @FXML
    private Label promoCodeStatusLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private HBox discountedPriceBox;

    private User currentUser;
    private TrainingPlan selectedPlan;
    private TrainingRegistrationController trainingRegistrationController;
    private PaymentController paymentController;
    private UserController userController;
    private double discountedAmount = 0;
    private Promotion appliedPromotion;
    private PromosController promosController = new PromosController();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat priceFormatter = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US));
    private List<String> validCardNumbers;
    private List<String> validCVVs;
    private List<Double> balances;

    public TrainingPaymentController() {
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

    private void updateCardBalanceFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("CREDIT_CARD.txt"))) {
            for (int i = 0; i < validCardNumbers.size(); i++) {
                writer.write(validCardNumbers.get(i) + "," + validCVVs.get(i) + "," + balances.get(i));
                writer.newLine();
            }
        } catch (Exception e) {
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

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.trainingRegistrationController = new TrainingRegistrationController();
        this.paymentController = new PaymentController();
        this.userController = new UserController();
    }

    public void setSelectedPlan(TrainingPlan plan) {
        this.selectedPlan = plan;
        updateLabels();
    }

    private void updateLabels() {
        if (selectedPlan != null) {
            selectedPlanLabel.setText("Gói tập: " + selectedPlan.getPlanName() +
                    " (" + selectedPlan.getSessionAmount() + " buổi)");
            originalAmountText.setText(priceFormatter.format(selectedPlan.getPrice()) + " VNĐ");
            totalAmountText.setText(priceFormatter.format(selectedPlan.getPrice()) + " VNĐ");
            discountedPriceBox.setVisible(false);
        }
    }

    @FXML
    private void handleConfirmPayment() {
        String cardNumber = cardNumberField.getText().trim();
        String cvv = cvvField.getText().trim();

        // Kiểm tra thông tin nhập vào
        if (cardNumber.isEmpty() && cvv.isEmpty()) {
            errorLabel.setText("Vui lòng nhập số thẻ và mã CVV");
            return;
        } else if (cardNumber.isEmpty()) {
            errorLabel.setText("Vui lòng nhập số thẻ");
            return;
        } else if (cvv.isEmpty()) {
            errorLabel.setText("Vui lòng nhập mã CVV");
            return;
        }

        // Kiểm tra thông tin thẻ
        int cardIndex = getCardIndex(cardNumber, cvv);
        if (cardIndex == -1) {
            errorLabel.setText("Thông tin thẻ không hợp lệ");
            return;
        }

        // Kiểm tra số dư
        double balance = balances.get(cardIndex);
        double amount = discountedAmount > 0 ? discountedAmount : selectedPlan.getPrice();
        if (balance < amount) {
            errorLabel.setText("Số dư không đủ để thanh toán");
            return;
        }

        try {
            // Trừ số dư và cập nhật file
            balances.set(cardIndex, balance - amount);
            updateCardBalanceFile();

            // Tạo payment mới
            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod("Credit Card");
            payment.setStatus(model.enums.enum_PaymentStatus.COMPLETED);
            payment.setPromotionId(appliedPromotion != null ? appliedPromotion.getPromotionId() : null);
            payment.setNotes("Thanh toán gói huấn luyện " + selectedPlan.getPlanName());

            // Lưu payment và lấy ID
            int paymentId = paymentController.createPayment(payment);

            // Lấy memberId của user hiện tại
            Integer memberIdInteger = userController.getMemberIDByUserID(currentUser.getUserId());
            if (memberIdInteger == null) {
                errorLabel.setText("Không tìm thấy thông tin hội viên");
                return;
            }
            int memberId = memberIdInteger;

            // Tạo training registration mới
            TrainingRegistration registration = new TrainingRegistration();
            registration.setMemberId(memberId);
            registration.setPlanId(selectedPlan.getPlanId());
            registration.setStartDate(LocalDate.now());
            registration.setSessionsLeft(selectedPlan.getSessionAmount());
            registration.setPaymentId(paymentId);

            // Lưu training registration
            if (trainingRegistrationController.createTrainingRegistration(registration)) {
                showAlert("Thành công", "Đăng ký gói huấn luyện thành công!");
                closeAllWindows();
            } else {
                errorLabel.setText("Không thể tạo đăng ký gói huấn luyện");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Có lỗi xảy ra khi xử lý thanh toán");
        }
    }

    @FXML
    private void handleApplyPromo() {
        String promoCode = promoCodeField.getText().trim();
        if (promoCode.isEmpty()) {
            promoCodeStatusLabel.setText("Vui lòng nhập mã giảm giá");
            promoCodeStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Promotion promo = promosController.getPromotionByCode(promoCode);
        if (promo == null) {
            promoCodeStatusLabel.setText("Mã giảm giá không hợp lệ");
            promoCodeStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Kiểm tra thời hạn của mã giảm giá
        if (promo.getEndDate().isBefore(LocalDateTime.now())) {
            promoCodeStatusLabel.setText("Mã giảm giá đã hết hạn");
            promoCodeStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Áp dụng giảm giá
        appliedPromotion = promo;
        discountedAmount = calculateDiscountedAmount(selectedPlan.getPrice(), promo);
        updateAmountLabel();

        promoCodeStatusLabel.setText("Áp dụng mã giảm giá thành công!");
        promoCodeStatusLabel.setStyle("-fx-text-fill: green;");
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
        double amount = discountedAmount > 0 ? discountedAmount : selectedPlan.getPrice();
        originalAmountText.setStrikethrough(true);
        originalAmountText.setFill(Color.RED);
        totalAmountText.setText(priceFormatter.format(amount) + " VNĐ");
        totalAmountText.setFill(Color.GREEN);
        discountedPriceBox.setVisible(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) selectedPlanLabel.getScene().getWindow();
        stage.close();
    }

    private void closeAllWindows() {
        // Đóng tất cả các cửa sổ liên quan đến thanh toán training
        Stage currentStage = (Stage) selectedPlanLabel.getScene().getWindow();
        Stage parentStage = (Stage) currentStage.getOwner();
        if (parentStage != null) {
            parentStage.close();
        }
        currentStage.close();
    }

    @FXML
    private void handleBack() {
        // Đóng trang thanh toán hiện tại
        closeWindow();

        // Mở lại trang chọn gói tập
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/userView/trainingRegister/training_plan_selection.fxml"));
            Parent root = loader.load();

            // Lấy controller và set dữ liệu
            TrainingPlanSelectionController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Chọn gói huấn luyện");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở lại trang chọn gói tập.");
        }
    }
}