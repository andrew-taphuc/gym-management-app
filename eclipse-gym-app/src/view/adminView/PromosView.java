package view.adminView;

import controller.PromosController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import model.Promotion;
import view.BaseView;
import java.util.List;
import javafx.scene.layout.FlowPane;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.time.LocalDateTime;
import java.util.Optional;

public class PromosView extends BaseView {
    @FXML
    private VBox contentArea;
    @FXML
    private Label pageTitle;
    @FXML
    private FlowPane promoList;
    @FXML
    private Button addPromoButton;

    private PromosController promosController = new PromosController();

    public PromosView() {
        super(null);
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Danh sách mã khuyến mãi hiện có (Dành cho admin)");
        showAllPromotions();
    }

    @FXML
    private void handleAddPromo() {
        showAddEditPromoDialog(null);
    }

    private void showAllPromotions() {
        List<Promotion> promos = promosController.getAllPromotions();
        // Sắp xếp: Còn hạn -> Chưa khả dụng -> Hết hạn
        promos.sort((p1, p2) -> {
            int s1 = getStatusOrder(p1.getStatus());
            int s2 = getStatusOrder(p2.getStatus());
            return Integer.compare(s1, s2);
        });
        promoList.getChildren().clear();
        for (Promotion promo : promos) {
            VBox promoBox = createPromoBox(promo);
            promoList.getChildren().add(promoBox);
        }
    }

    private int getStatusOrder(String status) {
        if (status.equalsIgnoreCase("Còn hạn"))
            return 0;
        if (status.equalsIgnoreCase("Chưa khả dụng"))
            return 1;
        return 2; // Hết hạn
    }

    private VBox createPromoBox(Promotion promo) {
        VBox box = new VBox();
        box.getStyleClass().addAll("promo-box");
        box.setMinWidth(300);
        box.setMaxWidth(300);
        box.setSpacing(6);
        box.setPadding(new javafx.geometry.Insets(18, 14, 18, 14));

        Label name = new Label(promo.getPromotionName());
        name.getStyleClass().add("promo-name");

        String discount = promo.getDiscountType().equals("Phần trăm") ? (int) promo.getDiscountValue() + "%"
                : String.format("%.0f₫", promo.getDiscountValue());
        Label value = new Label("Giảm: " + discount);
        value.getStyleClass().add("promo-value");

        // Container cho các nút action
        HBox actionButtons = new HBox();
        actionButtons.getStyleClass().add("promo-actions");
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button editButton = new Button("Sửa");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> showAddEditPromoDialog(promo));

        Button deleteButton = new Button("Xóa");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> handleDeletePromo(promo));

        box.getChildren().addAll(name, value);

        // Xử lý trạng thái promotion - tất cả đều có thể click và chỉnh sửa
        if (promo.getStatus().equalsIgnoreCase("Hết hạn")) {
            box.getStyleClass().add("expired");
        } else if (promo.getStatus().equalsIgnoreCase("Chưa khả dụng")) {
            box.getStyleClass().add("upcoming");
        }

        // Tất cả promotion đều có thể click vào box để xem chi tiết
        box.setOnMouseClicked(e -> {
            if (e.getTarget() == box || e.getTarget() == name || e.getTarget() == value) {
                showPromoDetailPopup(promo);
            }
        });

        // Tất cả promotion đều có nút Sửa và Xóa
        actionButtons.getChildren().addAll(editButton, deleteButton);
        box.getChildren().add(actionButtons);

        return box;
    }

    private void handleDeletePromo(Promotion promo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa mã khuyến mãi này?");
        alert.setContentText("Mã: " + promo.getPromotionCode() + "\nTên: " + promo.getPromotionName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = promosController.deletePromotion(promo.getPromotionId());
            if (success) {
                showAlert("Thành công", "Đã xóa mã khuyến mãi thành công!", Alert.AlertType.INFORMATION);
                showAllPromotions(); // Refresh danh sách
            } else {
                showAlert("Lỗi", "Không thể xóa mã khuyến mãi. Vui lòng thử lại!", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAddEditPromoDialog(Promotion existingPromo) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existingPromo == null ? "Thêm mã khuyến mãi mới" : "Chỉnh sửa mã khuyến mãi");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Các trường input
        TextField codeField = new TextField();
        codeField.getStyleClass().add("dialog-text-field");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("dialog-text-field");
        TextArea descField = new TextArea();
        descField.getStyleClass().add("dialog-text-area");
        descField.setPrefRowCount(3);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getStyleClass().add("dialog-combo-box");
        typeCombo.getItems().addAll("Phần trăm", "Tiền mặt");
        TextField valueField = new TextField();
        valueField.getStyleClass().add("dialog-text-field");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.getStyleClass().add("dialog-date-picker");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.getStyleClass().add("dialog-date-picker");

        // Nếu là edit, điền dữ liệu có sẵn
        if (existingPromo != null) {
            codeField.setText(existingPromo.getPromotionCode());
            nameField.setText(existingPromo.getPromotionName());
            descField.setText(existingPromo.getDescription());
            typeCombo.setValue(existingPromo.getDiscountType());
            valueField.setText(String.valueOf(existingPromo.getDiscountValue()));
            startDatePicker.setValue(existingPromo.getStartDate().toLocalDate());
            endDatePicker.setValue(existingPromo.getEndDate().toLocalDate());
        } else {
            // Mặc định cho promotion mới
            typeCombo.setValue("Phần trăm");
        }

        // Tạo labels với style
        Label codeLabel = new Label("Mã code:");
        codeLabel.getStyleClass().add("dialog-label");
        Label nameLabel = new Label("Tên mã:");
        nameLabel.getStyleClass().add("dialog-label");
        Label descLabel = new Label("Mô tả:");
        descLabel.getStyleClass().add("dialog-label");
        Label typeLabel = new Label("Loại giảm giá:");
        typeLabel.getStyleClass().add("dialog-label");
        Label valueLabel = new Label("Giá trị:");
        valueLabel.getStyleClass().add("dialog-label");
        Label startLabel = new Label("Ngày bắt đầu:");
        startLabel.getStyleClass().add("dialog-label");
        Label endLabel = new Label("Ngày kết thúc:");
        endLabel.getStyleClass().add("dialog-label");

        // Layout
        grid.add(codeLabel, 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(descLabel, 0, 2);
        grid.add(descField, 1, 2);
        grid.add(typeLabel, 0, 3);
        grid.add(typeCombo, 1, 3);
        grid.add(valueLabel, 0, 4);
        grid.add(valueField, 1, 4);
        grid.add(startLabel, 0, 5);
        grid.add(startDatePicker, 1, 5);
        grid.add(endLabel, 0, 6);
        grid.add(endDatePicker, 1, 6);

        // Nút Save và Cancel
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        Button saveButton = new Button(existingPromo == null ? "Thêm" : "Cập nhật");
        saveButton.getStyleClass().add("dialog-save-button");
        Button cancelButton = new Button("Hủy");
        cancelButton.getStyleClass().add("dialog-cancel-button");

        saveButton.setOnAction(e -> {
            if (validateInput(codeField, nameField, descField, typeCombo, valueField, startDatePicker, endDatePicker)) {
                Promotion promo = new Promotion();
                if (existingPromo != null) {
                    promo.setPromotionId(existingPromo.getPromotionId());
                    promo.setCreatedDate(existingPromo.getCreatedDate());
                } else {
                    promo.setCreatedDate(LocalDateTime.now());
                }

                promo.setPromotionCode(codeField.getText().trim());
                promo.setPromotionName(nameField.getText().trim());
                promo.setDescription(descField.getText().trim());
                promo.setDiscountType(typeCombo.getValue());
                promo.setDiscountValue(Double.parseDouble(valueField.getText().trim()));
                promo.setStartDate(startDatePicker.getValue().atStartOfDay());
                promo.setEndDate(endDatePicker.getValue().atTime(23, 59, 59));
                // Không set status - để database tự động tính toán
                promo.setUpdatedDate(LocalDateTime.now());

                boolean success;
                if (existingPromo == null) {
                    success = promosController.addPromotion(promo);
                } else {
                    success = promosController.updatePromotion(promo);
                }

                if (success) {
                    showAlert("Thành công",
                            existingPromo == null ? "Đã thêm mã khuyến mãi thành công!"
                                    : "Đã cập nhật mã khuyến mãi thành công!",
                            Alert.AlertType.INFORMATION);
                    dialog.close();
                    showAllPromotions(); // Refresh danh sách
                } else {
                    showAlert("Lỗi", "Không thể lưu mã khuyến mãi. Vui lòng thử lại!", Alert.AlertType.ERROR);
                }
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        buttonBox.getChildren().addAll(saveButton, cancelButton);
        grid.add(buttonBox, 0, 7, 2, 1);

        Scene scene = new Scene(grid, 450, 480);
        // Load CSS cho dialog
        try {
            String cssPath = getClass().getResource("promos.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.out.println("Không thể load CSS file cho dialog: " + e.getMessage());
        }

        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private boolean validateInput(TextField codeField, TextField nameField, TextArea descField,
            ComboBox<String> typeCombo, TextField valueField,
            DatePicker startDatePicker, DatePicker endDatePicker) {

        if (codeField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập mã code!", Alert.AlertType.ERROR);
            return false;
        }

        if (nameField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên mã khuyến mãi!", Alert.AlertType.ERROR);
            return false;
        }

        if (descField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập mô tả!", Alert.AlertType.ERROR);
            return false;
        }

        if (typeCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn loại giảm giá!", Alert.AlertType.ERROR);
            return false;
        }

        try {
            double value = Double.parseDouble(valueField.getText().trim());
            if (value <= 0) {
                showAlert("Lỗi", "Giá trị giảm giá phải lớn hơn 0!", Alert.AlertType.ERROR);
                return false;
            }
            if (typeCombo.getValue().equals("Phần trăm") && value > 100) {
                showAlert("Lỗi", "Giá trị phần trăm không được vượt quá 100%!", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Vui lòng nhập giá trị hợp lệ!", Alert.AlertType.ERROR);
            return false;
        }

        if (startDatePicker.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn ngày bắt đầu!", Alert.AlertType.ERROR);
            return false;
        }

        if (endDatePicker.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn ngày kết thúc!", Alert.AlertType.ERROR);
            return false;
        }

        if (endDatePicker.getValue().isBefore(startDatePicker.getValue()) ||
                endDatePicker.getValue().isEqual(startDatePicker.getValue())) {
            showAlert("Lỗi", "Ngày kết thúc phải sau ngày bắt đầu!", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPromoDetailPopup(Promotion promo) {
        // Tạo VBox chính cho popup
        VBox popupContainer = new VBox();
        popupContainer.getStyleClass().add("promo-detail-popup");
        popupContainer.setSpacing(0);
        popupContainer.setAlignment(Pos.TOP_LEFT);

        // Tiêu đề popup
        Label title = new Label("CHI TIẾT MÃ KHUYẾN MÃI");
        title.getStyleClass().add("promo-detail-title");
        popupContainer.getChildren().add(title);

        // Container cho các thông tin chi tiết
        VBox detailContainer = new VBox();
        detailContainer.setSpacing(12);
        detailContainer.setPadding(new Insets(16, 0, 0, 0));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String timeRange = promo.getStartDate().format(fmt) + " đến " + promo.getEndDate().format(fmt);

        // Tạo các hàng thông tin
        detailContainer.getChildren().add(createDetailRow("Tên mã:", promo.getPromotionName(), "bold"));
        detailContainer.getChildren().add(createDetailRow("Mã code:", promo.getPromotionCode(), "code"));
        detailContainer.getChildren().add(createDetailRow("Mô tả:", promo.getDescription(), ""));

        String discount = promo.getDiscountType().equals("Phần trăm") ? (int) promo.getDiscountValue() + "%"
                : String.format("%.0f₫", promo.getDiscountValue());
        detailContainer.getChildren().add(createDetailRow("Giá trị giảm:", discount, "discount"));
        detailContainer.getChildren().add(createDetailRow("Thời gian:", timeRange, ""));

        String statusClass = promo.getStatus().equals("Còn hạn") ? "status-active"
                : (promo.getStatus().equals("Chưa khả dụng") ? "status-upcoming" : "status-inactive");
        detailContainer.getChildren().add(createDetailRow("Trạng thái:", promo.getStatus(), statusClass));

        popupContainer.getChildren().add(detailContainer);

        // Nút đóng
        Button closeButton = new Button("Đóng");
        closeButton.getStyleClass().add("close-button");
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));
        buttonContainer.getChildren().add(closeButton);
        popupContainer.getChildren().add(buttonContainer);

        // Tạo và hiển thị popup
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("");

        Scene scene = new Scene(popupContainer);
        // Đảm bảo CSS được load
        try {
            String cssPath = getClass().getResource("promos.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            try {
                String cssPath = getClass().getResource("/promos.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (Exception ex) {
                System.out.println("Không thể load CSS file: " + ex.getMessage());
            }
        }

        popup.setScene(scene);
        popup.setResizable(false);
        popup.sizeToScene();

        // Đóng popup khi click nút
        closeButton.setOnAction(e -> popup.close());

        popup.showAndWait();
    }

    private HBox createDetailRow(String labelText, String valueText, String valueStyleClass) {
        HBox row = new HBox();
        row.getStyleClass().add("promo-detail-row");
        row.setSpacing(16);
        row.setAlignment(Pos.CENTER_LEFT);

        // Label bên trái
        Label label = new Label(labelText);
        label.getStyleClass().add("promo-detail-label");

        // Giá trị bên phải
        Label value = new Label(valueText);
        value.getStyleClass().add("promo-detail-value");

        // Thêm style class đặc biệt nếu có
        if (!valueStyleClass.isEmpty()) {
            value.getStyleClass().add(valueStyleClass);
        }

        row.getChildren().addAll(label, value);
        return row;
    }
}