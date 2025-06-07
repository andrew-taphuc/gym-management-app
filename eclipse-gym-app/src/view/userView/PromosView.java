package view.userView;

import controller.PromosController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
import java.util.Comparator;

public class PromosView extends BaseView {
    @FXML
    private VBox contentArea;
    @FXML
    private Label pageTitle;
    @FXML
    private FlowPane promoList;

    private PromosController promosController = new PromosController();

    public PromosView() {
        super(null);
    }

    @FXML
    public void initialize() {
        pageTitle.setText("Danh sách mã khuyến mãi hiện có (Dành cho user)");
        showAllPromotions();
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
        box.setSpacing(8);
        box.setPadding(new javafx.geometry.Insets(18, 14, 18, 14));

        Label name = new Label(promo.getPromotionName());
        name.getStyleClass().add("promo-name");

        String discount = promo.getDiscountType().equals("Phần trăm") ? (int) promo.getDiscountValue() + "%"
                : String.format("%.0f₫", promo.getDiscountValue());
        Label value = new Label("Giảm: " + discount);
        value.getStyleClass().add("promo-value");

        box.getChildren().addAll(name, value);
        if (promo.getStatus().equalsIgnoreCase("Hết hạn")) {
            box.getStyleClass().add("expired");
            box.setOnMouseClicked(null);
            box.setDisable(true);
        } else if (promo.getStatus().equalsIgnoreCase("Chưa khả dụng")) {
            box.getStyleClass().add("upcoming");
            box.setOnMouseClicked(e -> showPromoDetailPopup(promo));
            box.setDisable(false);
        } else {
            box.setOnMouseClicked(e -> showPromoDetailPopup(promo));
            box.setDisable(false);
        }
        return box;
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
        // popup.setTitle("Chi tiết mã khuyến mãi");
        popup.setTitle("");

        Scene scene = new Scene(popupContainer);
        // Đảm bảo CSS được load - sử dụng đường dẫn tương đối
        try {
            String cssPath = getClass().getResource("promos.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            // Fallback: thử các đường dẫn khác
            try {
                String cssPath = getClass().getResource("/promos.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (Exception ex) {
                System.out.println("Không thể load CSS file: " + ex.getMessage());
                // Có thể tiếp tục mà không có CSS hoặc áp dụng inline styles
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