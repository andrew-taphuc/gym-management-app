package view.common;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controller.InvoiceController;
import controller.PaymentController;
import model.Invoice;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class InvoicePopup {

    private InvoiceController invoiceController;
    private PaymentController paymentController;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormatter;
    private Stage popupStage;

    public InvoicePopup() {
        this.invoiceController = new InvoiceController();
        this.paymentController = new PaymentController();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    public void showInvoicePopup(int paymentId) {
        try {
            // Lấy thông tin hóa đơn
            Invoice invoice = invoiceController.getInvoiceByPaymentId(paymentId);
            if (invoice == null) {
                showErrorDialog("Không tìm thấy hóa đơn cho thanh toán này!");
                return;
            }

            // Lấy thông tin payment để có thêm details
            PaymentController.PaymentInfo paymentInfo = getPaymentInfoById(paymentId);

            createInvoicePopup(invoice, paymentInfo);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Lỗi khi hiển thị hóa đơn: " + e.getMessage());
        }
    }

    private PaymentController.PaymentInfo getPaymentInfoById(int paymentId) {
        // Tìm payment info từ danh sách payments
        return paymentController.getAllPaymentsWithDetails()
                .stream()
                .filter(p -> p.getPaymentId() == paymentId)
                .findFirst()
                .orElse(null);
    }

    private void createInvoicePopup(Invoice invoice, PaymentController.PaymentInfo paymentInfo) {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.DECORATED);
        popupStage.setTitle("Hóa đơn - " + invoice.getInvoiceCode());
        popupStage.setResizable(false);

        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle(
                "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 10;");
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Header - Logo và tiêu đề
        VBox header = createHeader();

        // Invoice info section
        VBox invoiceInfo = createInvoiceInfoSection(invoice, paymentInfo);

        // Amount breakdown section
        VBox amountSection = createAmountSection(invoice);

        // Footer section
        VBox footer = createFooterSection();

        // Action buttons
        HBox buttonBox = createButtonSection();

        mainContainer.getChildren().addAll(header, invoiceInfo, amountSection, footer, buttonBox);

        Scene scene = new Scene(mainContainer, 500, 650);

        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        Label gymName = new Label("PHÒNG TẬP TPH FITNESS");
        gymName.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gymName.setStyle("-fx-text-fill: #1976D2;");

        Label address = new Label("Địa chỉ: Số 1 Đại Cồ Việt, P.Bách Khoa, Q.Hai Bà Trưng, Hà Nội");
        address.setFont(Font.font("Arial", 14));
        address.setStyle("-fx-text-fill: #666666;");

        Label phone = new Label("Điện thoại: 0901.240.904 | Email: tph.18cm@fitness.com");
        phone.setFont(Font.font("Arial", 14));
        phone.setStyle("-fx-text-fill: #666666;");

        Label invoiceTitle = new Label("HÓA ĐƠN THANH TOÁN");
        invoiceTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        invoiceTitle.setStyle("-fx-text-fill: #333333; -fx-padding: 10 0 0 0;");

        header.getChildren().addAll(gymName, address, phone, invoiceTitle);

        return header;
    }

    private VBox createInvoiceInfoSection(Invoice invoice, PaymentController.PaymentInfo paymentInfo) {
        VBox section = new VBox(8);
        section.setStyle(
                "-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Invoice details
        HBox invoiceCodeBox = createInfoRow("Mã hóa đơn:", invoice.getInvoiceCode(), true);
        HBox issueDateBox = createInfoRow("Ngày xuất:", invoice.getIssueDate().format(dateFormatter), false);

        // Customer info
        String customerName = paymentInfo != null ? paymentInfo.getMemberName() : "Không xác định";
        HBox customerBox = createInfoRow("Khách hàng:", customerName, false);

        // Service details
        HBox serviceBox = createInfoRow("Loại dịch vụ:", invoice.getServiceType(), false);

        // Payment details
        String paymentMethod = paymentInfo != null ? paymentInfo.getPaymentMethod() : "Không xác định";
        String paymentDate = paymentInfo != null ? paymentInfo.getPaymentDate() : "Không xác định";
        HBox methodBox = createInfoRow("Phương thức TT:", paymentMethod, false);
        HBox paymentDateBox = createInfoRow("Ngày thanh toán:", paymentDate, false);

        section.getChildren().addAll(
                invoiceCodeBox, issueDateBox,
                new Separator(),
                customerBox, serviceBox,
                new Separator(),
                methodBox, paymentDateBox);

        return section;
    }

    private VBox createAmountSection(Invoice invoice) {
        VBox section = new VBox(12);
        section.setStyle(
                "-fx-background-color: #fff3e0; -fx-padding: 15; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label title = new Label("CHI TIẾT THANH TOÁN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #e65100;");
        title.setAlignment(Pos.CENTER);

        HBox totalAmountBox = createAmountRow("Tổng tiền dịch vụ:", invoice.getTotalAmount(), false);
        HBox discountBox = createAmountRow("Giảm giá:", invoice.getDiscountAmount(), false);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ff9800;");

        HBox finalAmountBox = createAmountRow("TỔNG THANH TOÁN:", invoice.getFinalAmount(), true);

        section.getChildren().addAll(title, totalAmountBox, discountBox, separator, finalAmountBox);

        return section;
    }

    private VBox createFooterSection() {
        VBox footer = new VBox(8);
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-padding: 10 0;");

        Label thankYou = new Label("Cảm ơn quý khách đã sử dụng dịch vụ!");
        thankYou.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        thankYou.setStyle("-fx-text-fill: #4CAF50;");

        Label note = new Label("Vui lòng giữ hóa đơn để được hỗ trợ tốt nhất");
        note.setFont(Font.font("Arial", 12));
        note.setStyle("-fx-text-fill: #666666;");

        footer.getChildren().addAll(thankYou, note);

        return footer;
    }

    private HBox createButtonSection() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 10 0 0 0;");

        Button printButton = new Button("In hóa đơn");
        printButton.setStyle(
                "-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        printButton.setOnAction(e -> {
            // Logic in hóa đơn - có thể implement sau
            System.out.println("In hóa đơn...");
        });

        Button closeButton = new Button("Đóng");
        closeButton.setStyle(
                "-fx-font-size: 14px; -fx-background-color: #9E9E9E; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        closeButton.setOnAction(e -> popupStage.close());

        buttonBox.getChildren().addAll(printButton, closeButton);

        return buttonBox;
    }

    private HBox createInfoRow(String label, String value, boolean bold) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelControl = new Label(label);
        labelControl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        labelControl.setStyle("-fx-text-fill: #333333;");
        labelControl.setPrefWidth(130);

        Label valueControl = new Label(value);
        if (bold) {
            valueControl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            valueControl.setStyle("-fx-text-fill: #1976D2;");
        } else {
            valueControl.setFont(Font.font("Arial", 14));
            valueControl.setStyle("-fx-text-fill: #555555;");
        }

        row.getChildren().addAll(labelControl, valueControl);

        return row;
    }

    private HBox createAmountRow(String label, double amount, boolean isTotal) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelControl = new Label(label);
        Label valueControl = new Label(currencyFormat.format(amount));

        if (isTotal) {
            labelControl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            labelControl.setStyle("-fx-text-fill: #e65100;");
            valueControl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            valueControl.setStyle("-fx-text-fill: #e65100;");
        } else {
            labelControl.setFont(Font.font("Arial", 14));
            labelControl.setStyle("-fx-text-fill: #333333;");
            valueControl.setFont(Font.font("Arial", 14));
            valueControl.setStyle("-fx-text-fill: #555555;");
        }

        // Đẩy value sang phải
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(labelControl, spacer, valueControl);

        return row;
    }

    private void showErrorDialog(String message) {
        Stage errorStage = new Stage();
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.setTitle("Lỗi");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f44336;");

        Button okButton = new Button("OK");
        okButton.setStyle(
                "-fx-font-size: 14px; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 20;");
        okButton.setOnAction(e -> errorStage.close());

        content.getChildren().addAll(errorLabel, okButton);

        Scene scene = new Scene(content, 300, 150);
        errorStage.setScene(scene);
        errorStage.showAndWait();
    }
}