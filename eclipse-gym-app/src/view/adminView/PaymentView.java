package view.adminView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import model.User;
import controller.PaymentController;
import controller.InvoiceController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DBConnection;
import view.common.InvoicePopup;
import javafx.scene.layout.HBox;
import model.Invoice;

public class PaymentView {
    @FXML
    private Label pageTitle;

    @FXML
    private TableView<PaymentController.PaymentInfo> paymentTable;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, Integer> paymentIdColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> amountColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> paymentDateColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> paymentMethodColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> statusColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> serviceTypeColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> invoiceCodeColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, String> memberNameColumn;

    @FXML
    private TableColumn<PaymentController.PaymentInfo, Void> actionColumn;

    private User currentUser;
    private PaymentController paymentController;
    private InvoiceController invoiceController;
    private Connection connection;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Payment | User: " + (currentUser != null ? currentUser.getUsername() : "null"));

        paymentController = new PaymentController();
        invoiceController = new InvoiceController();
        connection = DBConnection.getConnection();

        setupTableColumns();
        loadPaymentData();
    }

    private void setupTableColumns() {
        // Thiết lập các cột của bảng
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("paymentId"));

        // Amount column với custom styling
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(column -> new TableCell<PaymentController.PaymentInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-alignment: CENTER-LEFT;");
                }
            }
        });

        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        // Payment method column với custom styling
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentMethodColumn.setCellFactory(column -> new TableCell<PaymentController.PaymentInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3;");
                }
            }
        });

        // Status column với custom styling
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<PaymentController.PaymentInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Thành công".equals(item)) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-alignment: CENTER;");
                    } else if ("Thất bại".equals(item)) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #f44336; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Service type column với custom styling
        serviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        serviceTypeColumn.setCellFactory(column -> new TableCell<PaymentController.PaymentInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-style: italic; -fx-text-fill: #FF9800;");
                }
            }
        });

        invoiceCodeColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceCode"));

        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));

        // Thiết lập cột nút action với custom styling
        actionColumn.setCellFactory(param -> new TableCell<PaymentController.PaymentInfo, Void>() {
            private final Button printButton = new Button("In hóa đơn");
            private final Button viewButton = new Button("Xem hóa đơn");
            private final HBox buttonBox = new HBox(8);

            {
                // Setup nút "In hóa đơn"
                printButton.setOnAction(event -> {
                    PaymentController.PaymentInfo row = getTableView().getItems().get(getIndex());
                    printInvoice(row.getPaymentId());
                });
                printButton.getStyleClass().add("action-button");
                printButton.setStyle(
                        "-fx-font-size: 12px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 8 12;");

                // Setup nút "Xem hóa đơn"
                viewButton.setOnAction(event -> {
                    PaymentController.PaymentInfo row = getTableView().getItems().get(getIndex());
                    viewInvoice(row.getPaymentId());
                });
                viewButton.getStyleClass().add("action-button");
                viewButton.setStyle(
                        "-fx-font-size: 12px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 8 12;");

                buttonBox.getChildren().addAll(printButton, viewButton);
                buttonBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PaymentController.PaymentInfo row = getTableView().getItems().get(getIndex());

                    // Enable/disable nút "Xem hóa đơn" dựa trên việc hóa đơn đã tồn tại
                    boolean invoiceExists = isInvoiceExists(row.getPaymentId());
                    viewButton.setDisable(!invoiceExists);

                    if (!invoiceExists) {
                        viewButton.setStyle(
                                "-fx-font-size: 12px; -fx-background-color: #BDBDBD; -fx-text-fill: #666666; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 12;");
                    } else {
                        viewButton.setStyle(
                                "-fx-font-size: 12px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 8 12;");
                    }

                    setGraphic(buttonBox);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void loadPaymentData() {
        try {
            // Gọi PaymentController để lấy danh sách payments
            ObservableList<PaymentController.PaymentInfo> paymentInfos = FXCollections
                    .observableArrayList(paymentController.getAllPaymentsWithDetails());

            paymentTable.setItems(paymentInfos);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải danh sách thanh toán: " + e.getMessage());
        }
    }

    private void printInvoice(int paymentId) {
        try {
            // Kiểm tra xem đã có hóa đơn cho payment này chưa
            if (isInvoiceExists(paymentId)) {
                showAlert("Thông báo", "Hóa đơn cho thanh toán này đã được tạo rồi!");
                return;
            }

            // Tạo hóa đơn mới
            int invoiceId = invoiceController.createInvoice(paymentId, currentUser.getUserId());

            if (invoiceId > 0) {
                // Lấy hóa đơn vừa tạo để lấy mã hóa đơn
                Invoice newInvoice = invoiceController.getInvoiceById(invoiceId);
                if (newInvoice != null) {
                    showAlert("Thành công", "Đã tạo hóa đơn thành công!\nMã hóa đơn: " + newInvoice.getInvoiceCode());
                    // Load lại data bảng để cập nhật cột mã hóa đơn
                    loadPaymentData();
                } else {
                    showAlert("Thành công", "Đã tạo hóa đơn thành công!\nMã hóa đơn ID: " + invoiceId);
                    loadPaymentData();
                }
            } else {
                showAlert("Lỗi", "Không thể tạo hóa đơn. Vui lòng kiểm tra lại thông tin thanh toán.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi tạo hóa đơn: " + e.getMessage());
        }
    }

    private boolean isInvoiceExists(int paymentId) {
        String query = "SELECT COUNT(*) FROM Invoices WHERE PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void viewInvoice(int paymentId) {
        try {
            // Sử dụng InvoicePopup để hiển thị hóa đơn
            InvoicePopup invoicePopup = new InvoicePopup();
            invoicePopup.showInvoicePopup(paymentId);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi xem hóa đơn: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}