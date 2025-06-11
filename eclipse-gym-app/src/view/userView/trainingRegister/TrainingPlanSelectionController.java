package view.userView.trainingRegister;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import model.User;
import model.TrainingPlan;
import controller.TrainingPlanController;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.List;

public class TrainingPlanSelectionController {
    @FXML
    private Label pageTitle;

    @FXML
    private TableView<TrainingPlan> trainingPlansTable;

    @FXML
    private TableColumn<TrainingPlan, String> planCodeColumn;

    @FXML
    private TableColumn<TrainingPlan, String> planNameColumn;

    @FXML
    private TableColumn<TrainingPlan, String> typeColumn;

    @FXML
    private TableColumn<TrainingPlan, Integer> sessionsColumn;

    @FXML
    private TableColumn<TrainingPlan, String> priceColumn;

    @FXML
    private TableColumn<TrainingPlan, String> descriptionColumn;

    @FXML
    private Button selectButton;

    @FXML
    private Button cancelButton;

    private User currentUser;
    private TrainingPlanController trainingPlanController;
    private final DecimalFormat priceFormatter = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US));

    @FXML
    private void initialize() {
        pageTitle.setText("Chọn gói tập huấn luyện viên");
        trainingPlanController = new TrainingPlanController();
        initializeTable();
        loadTrainingPlans();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void initializeTable() {
        planCodeColumn.setCellValueFactory(new PropertyValueFactory<>("planCode"));
        planNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));

        typeColumn.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getType() != null ? cellData.getValue().getType().getValue() : "";
            return new SimpleStringProperty(type);
        });

        sessionsColumn.setCellValueFactory(new PropertyValueFactory<>("sessionAmount"));

        priceColumn.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getPrice();
            return new SimpleStringProperty(priceFormatter.format(price));
        });

        // Tạo custom cell factory để hiển thị "đ" có gạch chân
        priceColumn.setCellFactory(new Callback<TableColumn<TrainingPlan, String>, TableCell<TrainingPlan, String>>() {
            @Override
            public TableCell<TrainingPlan, String> call(TableColumn<TrainingPlan, String> param) {
                return new TableCell<TrainingPlan, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            // Tạo Text node cho số tiền
                            Text priceText = new Text(item);
                            priceText.setStyle("-fx-font-size: 18px;");

                            // Tạo Text node cho ký hiệu "đ" có gạch chân
                            Text currencyText = new Text("đ");
                            currencyText.setStyle("-fx-font-size: 18px; -fx-underline: true;");

                            // Đặt vào HBox
                            HBox hbox = new HBox(2);
                            hbox.getChildren().addAll(priceText, currencyText);
                            setGraphic(hbox);
                            setText(null);
                        }
                    }
                };
            }
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void loadTrainingPlans() {
        List<TrainingPlan> plans = trainingPlanController.getAllPlans();
        ObservableList<TrainingPlan> trainingPlansList = FXCollections.observableArrayList(plans);
        trainingPlansTable.setItems(trainingPlansList);
    }

    @FXML
    private void handleSelectClick() {
        TrainingPlan selectedPlan = trainingPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Vui lòng chọn một gói tập");
            return;
        }

        try {
            // Mở trang thanh toán training
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/userView/trainingRegister/training_payment.fxml"));
            Parent root = loader.load();

            // Lấy controller và set dữ liệu
            TrainingPaymentController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setSelectedPlan(selectedPlan);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thanh toán gói huấn luyện");
            stage.show();

            // Đóng cửa sổ hiện tại
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở trang thanh toán");
        }
    }

    @FXML
    private void handleCancelClick() {
        closeWindow();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) pageTitle.getScene().getWindow();
        stage.close();
    }
}