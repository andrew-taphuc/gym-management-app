package view.ownerView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;
import model.User;
import model.MembershipPlan;
import model.TrainingPlan;
import model.enums.enum_TrainerSpecialization;
import controller.MembershipPlanController;
import controller.TrainingPlanController;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PlansRenewalsController {
    @FXML
    private Label pageTitle;

    // Membership Plans Table
    @FXML
    private TableView<MembershipPlan> membershipPlansTable;

    @FXML
    private TableColumn<MembershipPlan, Integer> membershipPlanIdColumn;

    @FXML
    private TableColumn<MembershipPlan, String> membershipPlanCodeColumn;

    @FXML
    private TableColumn<MembershipPlan, String> membershipPlanNameColumn;

    @FXML
    private TableColumn<MembershipPlan, Integer> membershipDurationColumn;

    @FXML
    private TableColumn<MembershipPlan, String> membershipPriceColumn;

    @FXML
    private TableColumn<MembershipPlan, String> membershipDescriptionColumn;

    @FXML
    private TableColumn<MembershipPlan, Void> membershipActionColumn;

    // Training Plans Table
    @FXML
    private TableView<TrainingPlan> trainingPlansTable;

    @FXML
    private TableColumn<TrainingPlan, Integer> trainingPlanIdColumn;

    @FXML
    private TableColumn<TrainingPlan, String> trainingPlanCodeColumn;

    @FXML
    private TableColumn<TrainingPlan, String> trainingPlanNameColumn;

    @FXML
    private TableColumn<TrainingPlan, String> trainingTypeColumn;

    @FXML
    private TableColumn<TrainingPlan, Integer> trainingSessionsColumn;

    @FXML
    private TableColumn<TrainingPlan, String> trainingPriceColumn;

    @FXML
    private TableColumn<TrainingPlan, String> trainingDescriptionColumn;

    @FXML
    private TableColumn<TrainingPlan, Void> trainingActionColumn;

    // Buttons
    @FXML
    private Button refreshButton;

    @FXML
    private Button addMembershipButton;

    @FXML
    private Button editMembershipButton;

    @FXML
    private Button deleteMembershipButton;

    @FXML
    private Button addTrainingButton;

    @FXML
    private Button editTrainingButton;

    @FXML
    private Button deleteTrainingButton;

    private User currentUser;
    private MembershipPlanController membershipPlanController;
    private TrainingPlanController trainingPlanController;
    private NumberFormat currencyFormat;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println(
                "Trang: Plans & Renewals | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Quản lý gói tập - Membership Plans & Training Plans");

        // Khởi tạo controllers
        membershipPlanController = new MembershipPlanController();
        trainingPlanController = new TrainingPlanController();

        // Khởi tạo format tiền tệ
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Khởi tạo tables
        initializeMembershipPlansTable();
        initializeTrainingPlansTable();

        // Load dữ liệu
        loadMembershipPlans();
        loadTrainingPlans();
    }

    private void initializeMembershipPlansTable() {
        membershipPlanIdColumn.setCellValueFactory(new PropertyValueFactory<>("planId"));
        membershipPlanCodeColumn.setCellValueFactory(new PropertyValueFactory<>("planCode"));
        membershipPlanNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        membershipDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        membershipPriceColumn.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getPrice();
            return new SimpleStringProperty(currencyFormat.format(price));
        });

        membershipDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Cột thao tác với nút lịch sử
        membershipActionColumn
                .setCellFactory(new Callback<TableColumn<MembershipPlan, Void>, TableCell<MembershipPlan, Void>>() {
                    @Override
                    public TableCell<MembershipPlan, Void> call(TableColumn<MembershipPlan, Void> param) {
                        return new TableCell<MembershipPlan, Void>() {
                            private final Button historyButton = new Button("Lịch sử");

                            {
                                historyButton.setStyle(
                                        "-fx-font-size: 14px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
                                historyButton.setOnAction(event -> {
                                    MembershipPlan plan = getTableView().getItems().get(getIndex());
                                    showMembershipPlanHistory(plan);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(historyButton);
                                }
                            }
                        };
                    }
                });
    }

    private void initializeTrainingPlansTable() {
        trainingPlanIdColumn.setCellValueFactory(new PropertyValueFactory<>("planId"));
        trainingPlanCodeColumn.setCellValueFactory(new PropertyValueFactory<>("planCode"));
        trainingPlanNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));

        trainingTypeColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getType() != null) {
                return new SimpleStringProperty(cellData.getValue().getType().getValue());
            }
            return new SimpleStringProperty("");
        });

        trainingSessionsColumn.setCellValueFactory(new PropertyValueFactory<>("sessionAmount"));

        trainingPriceColumn.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getPrice();
            return new SimpleStringProperty(currencyFormat.format(price));
        });

        trainingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Cột thao tác với nút lịch sử
        trainingActionColumn
                .setCellFactory(new Callback<TableColumn<TrainingPlan, Void>, TableCell<TrainingPlan, Void>>() {
                    @Override
                    public TableCell<TrainingPlan, Void> call(TableColumn<TrainingPlan, Void> param) {
                        return new TableCell<TrainingPlan, Void>() {
                            private final Button historyButton = new Button("Lịch sử");

                            {
                                historyButton.setStyle(
                                        "-fx-font-size: 14px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
                                historyButton.setOnAction(event -> {
                                    TrainingPlan plan = getTableView().getItems().get(getIndex());
                                    showTrainingPlanHistory(plan);
                                });
                            }

                            @Override
                            protected void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    setGraphic(historyButton);
                                }
                            }
                        };
                    }
                });
    }

    private void loadMembershipPlans() {
        try {
            List<MembershipPlan> plans = membershipPlanController.getAllPlans();
            membershipPlansTable.getItems().setAll(plans);
            System.out.println("Đã tải " + plans.size() + " gói tập membership");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Lỗi khi tải danh sách gói tập membership: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadTrainingPlans() {
        try {
            List<TrainingPlan> plans = trainingPlanController.getAllPlans();
            trainingPlansTable.getItems().setAll(plans);
            System.out.println("Đã tải " + plans.size() + " gói tập training");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Lỗi khi tải danh sách gói tập training: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefreshClick() {
        loadMembershipPlans();
        loadTrainingPlans();
        showAlert("Thành công", "Đã làm mới dữ liệu", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleAddMembershipClick() {
        Dialog<MembershipPlan> dialog = new Dialog<>();
        dialog.setTitle("Thêm gói tập Membership mới");
        dialog.setHeaderText("Nhập thông tin gói tập Membership");

        // Tạo layout cho form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Các trường nhập liệu
        TextField planCodeField = new TextField();
        planCodeField.setPromptText("Mã gói tập (VD: MP001)");

        TextField planNameField = new TextField();
        planNameField.setPromptText("Tên gói tập");

        Spinner<Integer> durationSpinner = new Spinner<>();
        durationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, 30));
        durationSpinner.setEditable(true);

        TextField priceField = new TextField();
        priceField.setPromptText("Giá tiền (VNĐ)");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Mô tả gói tập");
        descriptionArea.setPrefRowCount(3);

        // Thêm vào grid
        grid.add(new Label("Mã gói:"), 0, 0);
        grid.add(planCodeField, 1, 0);
        grid.add(new Label("Tên gói:"), 0, 1);
        grid.add(planNameField, 1, 1);
        grid.add(new Label("Thời hạn (ngày):"), 0, 2);
        grid.add(durationSpinner, 1, 2);
        grid.add(new Label("Giá:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Mô tả:"), 0, 4);
        grid.add(descriptionArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Thêm buttons
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Validation và tạo result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validation
                    if (planCodeField.getText().trim().isEmpty()) {
                        showAlert("Lỗi", "Mã gói không được để trống", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Kiểm tra mã gói đã tồn tại
                    if (membershipPlanController.checkPlanCodeExists(planCodeField.getText().trim())) {
                        showAlert("Lỗi", "Mã gói đã tồn tại. Vui lòng nhập mã gói khác.", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (planNameField.getText().trim().isEmpty()) {
                        showAlert("Lỗi", "Tên gói không được để trống", Alert.AlertType.ERROR);
                        return null;
                    }

                    double price = Double.parseDouble(priceField.getText().trim());
                    if (price <= 0) {
                        showAlert("Lỗi", "Giá phải lớn hơn 0", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Tạo MembershipPlan object
                    MembershipPlan plan = new MembershipPlan();
                    plan.setPlanCode(planCodeField.getText().trim());
                    plan.setPlanName(planNameField.getText().trim());
                    plan.setDuration(durationSpinner.getValue());
                    plan.setPrice(price);
                    plan.setDescription(descriptionArea.getText().trim());

                    return plan;
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Giá phải là số hợp lệ", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        // Hiển thị dialog và xử lý kết quả
        Optional<MembershipPlan> result = dialog.showAndWait();
        result.ifPresent(plan -> {
            try {
                boolean success = membershipPlanController.createPlan(plan);
                if (success) {
                    loadMembershipPlans(); // Refresh table
                    showAlert("Thành công", "Đã thêm gói tập membership thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Lỗi", "Không thể thêm gói tập. Mã gói có thể đã tồn tại.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Lỗi khi thêm gói tập: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleEditMembershipClick() {
        MembershipPlan selectedPlan = membershipPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Cảnh báo", "Vui lòng chọn gói tập membership cần sửa", Alert.AlertType.WARNING);
            return;
        }

        Dialog<MembershipPlan> dialog = new Dialog<>();
        dialog.setTitle("Sửa gói tập Membership");
        dialog.setHeaderText("Chỉnh sửa thông tin gói tập Membership");

        // Tạo layout cho form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Các trường nhập liệu với giá trị hiện tại
        TextField planCodeField = new TextField(selectedPlan.getPlanCode());
        planCodeField.setPromptText("Mã gói tập (VD: MP001)");
        planCodeField.setEditable(false);
        planCodeField.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666666;");

        TextField planNameField = new TextField(selectedPlan.getPlanName());
        planNameField.setPromptText("Tên gói tập");

        Spinner<Integer> durationSpinner = new Spinner<>();
        durationSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, selectedPlan.getDuration()));
        durationSpinner.setEditable(true);

        TextField priceField = new TextField(String.valueOf(selectedPlan.getPrice()));
        priceField.setPromptText("Giá tiền (VNĐ)");

        TextArea descriptionArea = new TextArea(selectedPlan.getDescription());
        descriptionArea.setPromptText("Mô tả gói tập");
        descriptionArea.setPrefRowCount(3);

        // Thêm vào grid
        grid.add(new Label("Mã gói:"), 0, 0);
        grid.add(planCodeField, 1, 0);
        grid.add(new Label("Tên gói:"), 0, 1);
        grid.add(planNameField, 1, 1);
        grid.add(new Label("Thời hạn (ngày):"), 0, 2);
        grid.add(durationSpinner, 1, 2);
        grid.add(new Label("Giá:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Mô tả:"), 0, 4);
        grid.add(descriptionArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Thêm buttons
        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Validation và tạo result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validation
                    if (planNameField.getText().trim().isEmpty()) {
                        showAlert("Lỗi", "Tên gói không được để trống", Alert.AlertType.ERROR);
                        return null;
                    }

                    double price = Double.parseDouble(priceField.getText().trim());
                    if (price <= 0) {
                        showAlert("Lỗi", "Giá phải lớn hơn 0", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Tạo MembershipPlan object với thông tin đã cập nhật
                    MembershipPlan updatedPlan = new MembershipPlan();
                    updatedPlan.setPlanId(selectedPlan.getPlanId()); // Giữ nguyên ID
                    updatedPlan.setPlanCode(selectedPlan.getPlanCode()); // Giữ nguyên mã gói cũ
                    updatedPlan.setPlanName(planNameField.getText().trim());
                    updatedPlan.setDuration(durationSpinner.getValue());
                    updatedPlan.setPrice(price);
                    updatedPlan.setDescription(descriptionArea.getText().trim());
                    updatedPlan.setUpdateFrom(selectedPlan.getUpdateFrom()); // Giữ nguyên UpdateFrom

                    return updatedPlan;
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Giá phải là số hợp lệ", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        // Hiển thị dialog và xử lý kết quả
        Optional<MembershipPlan> result = dialog.showAndWait();
        result.ifPresent(plan -> {
            try {
                boolean success = membershipPlanController.updatePlan(plan);
                if (success) {
                    loadMembershipPlans(); // Refresh table
                    showAlert("Thành công", "Đã cập nhật gói tập membership thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Lỗi", "Không thể cập nhật gói tập", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Lỗi khi cập nhật gói tập: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleDeleteMembershipClick() {
        MembershipPlan selectedPlan = membershipPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Cảnh báo", "Vui lòng chọn gói tập membership cần xóa", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa gói tập này?");
        confirmAlert.setContentText("Gói tập: " + selectedPlan.getPlanName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = membershipPlanController.deletePlan(selectedPlan.getPlanId());
                if (success) {
                    loadMembershipPlans();
                    showAlert("Thành công", "Đã xóa gói tập membership thành công", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Lỗi", "Không thể xóa gói tập membership", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Lỗi khi xóa gói tập: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleAddTrainingClick() {
        Dialog<TrainingPlan> dialog = new Dialog<>();
        dialog.setTitle("Thêm gói tập với Huấn luyện viên mới");
        dialog.setHeaderText("Nhập thông tin gói tập Training");

        // Tạo layout cho form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Các trường nhập liệu
        TextField planCodeField = new TextField();
        planCodeField.setPromptText("Mã gói tập (VD: TP001)");

        TextField planNameField = new TextField();
        planNameField.setPromptText("Tên gói tập");

        ComboBox<enum_TrainerSpecialization> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().setAll(enum_TrainerSpecialization.values());
        typeComboBox.setPromptText("Chọn loại huấn luyện");

        Spinner<Integer> sessionsSpinner = new Spinner<>();
        sessionsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 8));
        sessionsSpinner.setEditable(true);

        TextField priceField = new TextField();
        priceField.setPromptText("Giá tiền (VNĐ)");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Mô tả gói tập");
        descriptionArea.setPrefRowCount(3);

        // Thêm vào grid
        grid.add(new Label("Mã gói:"), 0, 0);
        grid.add(planCodeField, 1, 0);
        grid.add(new Label("Tên gói:"), 0, 1);
        grid.add(planNameField, 1, 1);
        grid.add(new Label("Loại huấn luyện:"), 0, 2);
        grid.add(typeComboBox, 1, 2);
        grid.add(new Label("Số buổi:"), 0, 3);
        grid.add(sessionsSpinner, 1, 3);
        grid.add(new Label("Giá:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Mô tả:"), 0, 5);
        grid.add(descriptionArea, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Thêm buttons
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Validation và tạo result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validation
                    if (planCodeField.getText().trim().isEmpty()) {
                        showAlert("Lỗi", "Mã gói không được để trống", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Kiểm tra mã gói đã tồn tại
                    if (trainingPlanController.checkPlanCodeExists(planCodeField.getText().trim())) {
                        showAlert("Lỗi", "Mã gói đã tồn tại. Vui lòng nhập mã gói khác.", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (planNameField.getText().trim().isEmpty()) {
                        showAlert("Lỗi", "Tên gói không được để trống", Alert.AlertType.ERROR);
                        return null;
                    }
                    if (typeComboBox.getValue() == null) {
                        showAlert("Lỗi", "Vui lòng chọn loại huấn luyện", Alert.AlertType.ERROR);
                        return null;
                    }

                    double price = Double.parseDouble(priceField.getText().trim());
                    if (price <= 0) {
                        showAlert("Lỗi", "Giá phải lớn hơn 0", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Tạo TrainingPlan object
                    TrainingPlan plan = new TrainingPlan();
                    plan.setPlanCode(planCodeField.getText().trim());
                    plan.setPlanName(planNameField.getText().trim());
                    plan.setType(typeComboBox.getValue());
                    plan.setSessionAmount(sessionsSpinner.getValue());
                    plan.setPrice(price);
                    plan.setDescription(descriptionArea.getText().trim());

                    return plan;
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Giá phải là số hợp lệ", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        // Hiển thị dialog và xử lý kết quả
        Optional<TrainingPlan> result = dialog.showAndWait();
        result.ifPresent(plan -> {
            try {
                boolean success = trainingPlanController.createTrainingPlan(plan);
                if (success) {
                    loadTrainingPlans(); // Refresh table
                    showAlert("Thành công", "Đã thêm gói tập training thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Lỗi", "Không thể thêm gói tập. Mã gói có thể đã tồn tại.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Lỗi khi thêm gói tập: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleEditTrainingClick() {
        TrainingPlan selectedPlan = trainingPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Cảnh báo", "Vui lòng chọn gói tập training cần sửa", Alert.AlertType.WARNING);
            return;
        }

        Dialog<TrainingPlan> dialog = new Dialog<>();
        dialog.setTitle("Sửa gói tập với Huấn luyện viên");
        dialog.setHeaderText("Chỉnh sửa thông tin gói tập Training");

        // Tạo layout cho form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Các trường nhập liệu với giá trị hiện tại
        TextField planCodeField = new TextField(selectedPlan.getPlanCode());
        planCodeField.setPromptText("Mã gói tập (VD: TP001)");
        planCodeField.setEditable(false);
        planCodeField.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666666;");

        TextField planNameField = new TextField(selectedPlan.getPlanName());
        planNameField.setPromptText("Tên gói tập");

        ComboBox<enum_TrainerSpecialization> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().setAll(enum_TrainerSpecialization.values());
        typeComboBox.setValue(selectedPlan.getType()); // Set giá trị hiện tại
        typeComboBox.setPromptText("Chọn loại huấn luyện");

        Spinner<Integer> sessionsSpinner = new Spinner<>();
        sessionsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, selectedPlan.getSessionAmount()));
        sessionsSpinner.setEditable(true);

        TextField priceField = new TextField(String.valueOf(selectedPlan.getPrice()));
        priceField.setPromptText("Giá tiền (VNĐ)");

        TextArea descriptionArea = new TextArea(selectedPlan.getDescription());
        descriptionArea.setPromptText("Mô tả gói tập");
        descriptionArea.setPrefRowCount(3);

        // Thêm vào grid
        grid.add(new Label("Mã gói:"), 0, 0);
        grid.add(planCodeField, 1, 0);
        grid.add(new Label("Tên gói:"), 0, 1);
        grid.add(planNameField, 1, 1);
        grid.add(new Label("Loại huấn luyện:"), 0, 2);
        grid.add(typeComboBox, 1, 2);
        grid.add(new Label("Số buổi:"), 0, 3);
        grid.add(sessionsSpinner, 1, 3);
        grid.add(new Label("Giá:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Mô tả:"), 0, 5);
        grid.add(descriptionArea, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Thêm buttons
        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Validation và tạo result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validation
                    if (planNameField.getText().trim().isEmpty()) {
                        showAlert("Lỗi", "Tên gói không được để trống", Alert.AlertType.ERROR);
                        return null;
                    }
                    if (typeComboBox.getValue() == null) {
                        showAlert("Lỗi", "Vui lòng chọn loại huấn luyện", Alert.AlertType.ERROR);
                        return null;
                    }

                    double price = Double.parseDouble(priceField.getText().trim());
                    if (price <= 0) {
                        showAlert("Lỗi", "Giá phải lớn hơn 0", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Tạo TrainingPlan object với thông tin đã cập nhật
                    TrainingPlan updatedPlan = new TrainingPlan();
                    updatedPlan.setPlanId(selectedPlan.getPlanId()); // Giữ nguyên ID
                    updatedPlan.setPlanCode(selectedPlan.getPlanCode()); // Giữ nguyên mã gói cũ
                    updatedPlan.setPlanName(planNameField.getText().trim());
                    updatedPlan.setType(typeComboBox.getValue());
                    updatedPlan.setSessionAmount(sessionsSpinner.getValue());
                    updatedPlan.setPrice(price);
                    updatedPlan.setDescription(descriptionArea.getText().trim());
                    updatedPlan.setUpdateFrom(selectedPlan.getUpdateFrom()); // Giữ nguyên UpdateFrom

                    return updatedPlan;
                } catch (NumberFormatException e) {
                    showAlert("Lỗi", "Giá phải là số hợp lệ", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        // Hiển thị dialog và xử lý kết quả
        Optional<TrainingPlan> result = dialog.showAndWait();
        result.ifPresent(plan -> {
            try {
                boolean success = trainingPlanController.updateTrainingPlan(plan);
                if (success) {
                    loadTrainingPlans(); // Refresh table
                    showAlert("Thành công", "Đã cập nhật gói tập training thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Lỗi", "Không thể cập nhật gói tập", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Lỗi khi cập nhật gói tập: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleDeleteTrainingClick() {
        TrainingPlan selectedPlan = trainingPlansTable.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            showAlert("Cảnh báo", "Vui lòng chọn gói tập training cần xóa", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa gói tập này?");
        confirmAlert.setContentText("Gói tập: " + selectedPlan.getPlanName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = trainingPlanController.deleteTrainingPlan(selectedPlan.getPlanId());
                if (success) {
                    loadTrainingPlans();
                    showAlert("Thành công", "Đã xóa gói tập training thành công", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Lỗi", "Không thể xóa gói tập training", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi", "Lỗi khi xóa gói tập: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMembershipPlanHistory(MembershipPlan plan) {
        try {
            // Gọi trực tiếp method đã xử lý logic tìm lịch sử
            System.out.println("=== DEBUG: Membership Plan History ===");
            System.out.println("Plan hiện tại - ID: " + plan.getPlanId() + ", UpdateFrom: " + plan.getUpdateFrom());

            List<MembershipPlan> historyList = membershipPlanController.getPlanUpdateHistory(plan.getPlanId());

            System.out.println("Số lượng records trong lịch sử: " + historyList.size());
            for (int i = 0; i < historyList.size(); i++) {
                MembershipPlan historyPlan = historyList.get(i);
                System.out.println("History " + (i + 1) + " - ID: " + historyPlan.getPlanId() +
                        ", UpdateFrom: " + historyPlan.getUpdateFrom() +
                        ", Status: " + (historyPlan.getStatus() != null ? historyPlan.getStatus().getValue() : "null") +
                        ", CreatedDate: " + historyPlan.getCreatedDate() +
                        ", UpdatedDate: " + historyPlan.getUpdatedDate());
            }

            // Lọc bỏ plan hiện tại khỏi danh sách lịch sử để tránh trùng lặp
            historyList.removeIf(historyPlan -> historyPlan.getPlanId() == plan.getPlanId());
            System.out.println("Số lượng records sau khi lọc: " + historyList.size());
            System.out.println("=====================================");

            if (historyList.isEmpty()) {
                showAlert("Thông báo", "Gói tập này chưa có lịch sử cập nhật", Alert.AlertType.INFORMATION);
                return;
            }

            // Tạo dialog hiển thị lịch sử
            Dialog<Void> historyDialog = new Dialog<>();
            historyDialog.setTitle("Lịch sử cập nhật - " + plan.getPlanName());
            historyDialog.setHeaderText("Danh sách các phiên bản của gói tập (ID hiện tại: " + plan.getPlanId() + ")");

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Thêm thông tin gói hiện tại (active) vào đầu
            VBox currentPlanBox = new VBox(5);
            currentPlanBox.setStyle(
                    "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: #E8F5E8;");

            Label currentLabel = new Label("PHIÊN BẢN HIỆN TẠI (ID: " + plan.getPlanId() + ")");
            currentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #4CAF50;");

            Label currentDetailsLabel = new Label(String.format(
                    "Mã: %s | Tên: %s | Thời hạn: %d ngày | Giá: %s\nMô tả: %s\nTrạng thái: %s | Tạo: %s",
                    plan.getPlanCode(),
                    plan.getPlanName(),
                    plan.getDuration(),
                    currencyFormat.format(plan.getPrice()),
                    plan.getDescription(),
                    plan.getStatus() != null ? plan.getStatus().getValue() : "Hoạt động",
                    plan.getCreatedDate() != null ? plan.getCreatedDate().format(formatter) : "Không xác định"));
            currentDetailsLabel.setStyle("-fx-font-size: 14px;");

            currentPlanBox.getChildren().addAll(currentLabel, currentDetailsLabel);
            content.getChildren().add(currentPlanBox);

            // Hiển thị lịch sử (không bao gồm plan hiện tại)
            for (int i = 0; i < historyList.size(); i++) {
                MembershipPlan historyPlan = historyList.get(i);

                VBox planBox = new VBox(5);
                String statusValue = historyPlan.getStatus() != null ? historyPlan.getStatus().getValue()
                        : "Không xác định";

                // Phân biệt màu sắc theo status - chỉ còn "Đã cập nhật" vì đã lọc bỏ "Hoạt
                // động"
                if ("Đã cập nhật".equals(statusValue)) {
                    planBox.setStyle(
                            "-fx-border-color: #2196F3; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #E3F2FD;");
                } else {
                    planBox.setStyle(
                            "-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
                }

                String versionText;
                if (historyPlan.getUpdateFrom() == 0) {
                    versionText = "BẢN GỐC (ID: " + historyPlan.getPlanId() + ")";
                } else {
                    versionText = "Phiên bản cũ " + (i) + " (ID: " + historyPlan.getPlanId() + ")";
                }

                Label versionLabel = new Label(versionText);
                versionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                String timeInfo;
                if (historyPlan.getUpdatedDate() != null) {
                    timeInfo = "Cập nhật: " + historyPlan.getUpdatedDate().format(formatter);
                } else if (historyPlan.getCreatedDate() != null) {
                    timeInfo = "Tạo: " + historyPlan.getCreatedDate().format(formatter);
                } else {
                    timeInfo = "Không xác định thời gian";
                }

                Label detailsLabel = new Label(String.format(
                        "Mã: %s | Tên: %s | Thời hạn: %d ngày | Giá: %s\nMô tả: %s\nTrạng thái: %s | %s",
                        historyPlan.getPlanCode(),
                        historyPlan.getPlanName(),
                        historyPlan.getDuration(),
                        currencyFormat.format(historyPlan.getPrice()),
                        historyPlan.getDescription(),
                        statusValue,
                        timeInfo));
                detailsLabel.setStyle("-fx-font-size: 14px;");

                planBox.getChildren().addAll(versionLabel, detailsLabel);
                content.getChildren().add(planBox);
            }

            historyDialog.getDialogPane().setContent(content);
            historyDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            historyDialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Lỗi khi tải lịch sử cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showTrainingPlanHistory(TrainingPlan plan) {
        try {
            // Gọi trực tiếp method đã xử lý logic tìm lịch sử
            System.out.println("=== DEBUG: Training Plan History ===");
            System.out.println("Plan hiện tại - ID: " + plan.getPlanId() + ", UpdateFrom: " + plan.getUpdateFrom());

            List<TrainingPlan> historyList = trainingPlanController.getPlanUpdateHistory(plan.getPlanId());

            System.out.println("Số lượng records trong lịch sử: " + historyList.size());
            for (int i = 0; i < historyList.size(); i++) {
                TrainingPlan historyPlan = historyList.get(i);
                System.out.println("History " + (i + 1) + " - ID: " + historyPlan.getPlanId() +
                        ", UpdateFrom: " + historyPlan.getUpdateFrom() +
                        ", Status: " + (historyPlan.getStatus() != null ? historyPlan.getStatus().getValue() : "null") +
                        ", CreatedDate: " + historyPlan.getCreatedDate() +
                        ", UpdatedDate: " + historyPlan.getUpdatedDate());
            }

            // Lọc bỏ plan hiện tại khỏi danh sách lịch sử để tránh trùng lặp
            historyList.removeIf(historyPlan -> historyPlan.getPlanId() == plan.getPlanId());
            System.out.println("Số lượng records sau khi lọc: " + historyList.size());
            System.out.println("=====================================");

            if (historyList.isEmpty()) {
                showAlert("Thông báo", "Gói tập này chưa có lịch sử cập nhật", Alert.AlertType.INFORMATION);
                return;
            }

            // Tạo dialog hiển thị lịch sử
            Dialog<Void> historyDialog = new Dialog<>();
            historyDialog.setTitle("Lịch sử cập nhật - " + plan.getPlanName());
            historyDialog.setHeaderText("Danh sách các phiên bản của gói tập (ID hiện tại: " + plan.getPlanId() + ")");

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Thêm thông tin gói hiện tại (active) vào đầu
            VBox currentPlanBox = new VBox(5);
            currentPlanBox.setStyle(
                    "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: #E8F5E8;");

            Label currentLabel = new Label("PHIÊN BẢN HIỆN TẠI (ID: " + plan.getPlanId() + ")");
            currentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #4CAF50;");

            Label currentDetailsLabel = new Label(String.format(
                    "Mã: %s | Tên: %s | Loại: %s | Số buổi: %d | Giá: %s\nMô tả: %s\nTrạng thái: %s | Tạo: %s",
                    plan.getPlanCode(),
                    plan.getPlanName(),
                    plan.getType() != null ? plan.getType().getValue() : "Không xác định",
                    plan.getSessionAmount(),
                    currencyFormat.format(plan.getPrice()),
                    plan.getDescription(),
                    plan.getStatus() != null ? plan.getStatus().getValue() : "Hoạt động",
                    plan.getCreatedDate() != null ? plan.getCreatedDate().format(formatter) : "Không xác định"));
            currentDetailsLabel.setStyle("-fx-font-size: 14px;");

            currentPlanBox.getChildren().addAll(currentLabel, currentDetailsLabel);
            content.getChildren().add(currentPlanBox);

            // Hiển thị lịch sử (không bao gồm plan hiện tại)
            for (int i = 0; i < historyList.size(); i++) {
                TrainingPlan historyPlan = historyList.get(i);

                VBox planBox = new VBox(5);
                String statusValue = historyPlan.getStatus() != null ? historyPlan.getStatus().getValue()
                        : "Không xác định";

                // Phân biệt màu sắc theo status - chỉ còn "Đã cập nhật" vì đã lọc bỏ "Hoạt
                // động"
                if ("Đã cập nhật".equals(statusValue)) {
                    planBox.setStyle(
                            "-fx-border-color: #2196F3; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #E3F2FD;");
                } else {
                    planBox.setStyle(
                            "-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
                }

                String versionText;
                if (historyPlan.getUpdateFrom() == 0) {
                    versionText = "BẢN GỐC (ID: " + historyPlan.getPlanId() + ")";
                } else {
                    versionText = "Phiên bản cũ " + (i) + " (ID: " + historyPlan.getPlanId() + ")";
                }

                Label versionLabel = new Label(versionText);
                versionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                String timeInfo;
                if (historyPlan.getUpdatedDate() != null) {
                    timeInfo = "Cập nhật: " + historyPlan.getUpdatedDate().format(formatter);
                } else if (historyPlan.getCreatedDate() != null) {
                    timeInfo = "Tạo: " + historyPlan.getCreatedDate().format(formatter);
                } else {
                    timeInfo = "Không xác định thời gian";
                }

                Label detailsLabel = new Label(String.format(
                        "Mã: %s | Tên: %s | Loại: %s | Số buổi: %d | Giá: %s\nMô tả: %s\nTrạng thái: %s | %s",
                        historyPlan.getPlanCode(),
                        historyPlan.getPlanName(),
                        historyPlan.getType() != null ? historyPlan.getType().getValue() : "Không xác định",
                        historyPlan.getSessionAmount(),
                        currencyFormat.format(historyPlan.getPrice()),
                        historyPlan.getDescription(),
                        statusValue,
                        timeInfo));
                detailsLabel.setStyle("-fx-font-size: 14px;");

                planBox.getChildren().addAll(versionLabel, detailsLabel);
                content.getChildren().add(planBox);
            }

            historyDialog.getDialogPane().setContent(content);
            historyDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            historyDialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Lỗi khi tải lịch sử cập nhật: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshData() {
        loadMembershipPlans();
        loadTrainingPlans();
    }
}