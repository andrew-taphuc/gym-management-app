package view.ownerView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.User;
import model.EquipmentType;
import model.RoomEquipment;
import model.Room;
import controller.EquipmentTypeController;
import controller.RoomEquipmentController;
import controller.RoomController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import javafx.util.StringConverter;

public class EquipmentView implements Initializable {
    @FXML private Label pageTitle;
    @FXML private Label lblTotalRooms;
    @FXML private Label lblTotalEquipmentTypes;
    @FXML private Label lblMaintenanceEquipment;
    
    @FXML private TableView<Room> tblRooms;
    @FXML private TableColumn<Room, String> colRoomCode;
    @FXML private TableColumn<Room, String> colRoomName;
    @FXML private TableColumn<Room, String> colRoomType;
    @FXML private TableColumn<Room, String> colDescription;
    @FXML private TableColumn<Room, String> colStatus;
    @FXML private TableColumn<Room, Void> colActions;

    @FXML private TableView<RoomEquipment> tblMaintenanceEquipment;
    @FXML private TableColumn<RoomEquipment, String> colEquipmentCode;
    @FXML private TableColumn<RoomEquipment, String> colEquipmentType;
    @FXML private TableColumn<RoomEquipment, String> colEquipmentRoom;
    @FXML private TableColumn<RoomEquipment, Integer> colEquipmentQuantity;
    @FXML private TableColumn<RoomEquipment, String> colEquipmentDescription;
    @FXML private TableColumn<RoomEquipment, Void> colMaintenanceActions;

    private User currentUser;
    private RoomEquipmentController roomEquipmentController;
    private EquipmentTypeController equipmentTypeController;
    private RoomController roomController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            roomEquipmentController = new RoomEquipmentController();
            equipmentTypeController = new EquipmentTypeController();
            roomController = new RoomController();
            
            setupRoomTable();
            setupMaintenanceEquipmentTable();
            updateStatistics();
            loadRoomData();
            loadMaintenanceEquipmentData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRoomTable() {
        colRoomCode.setCellValueFactory(new PropertyValueFactory<>("roomCode"));
        colRoomName.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Setup action column with button
        colActions.setCellFactory(col -> new TableCell<Room, Void>() {
            private final Button btnDetails = new Button("Chi tiết");
            
            {
                btnDetails.getStyleClass().add("btn-details");
                btnDetails.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    showRoomEquipmentDetails(room);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDetails);
            }
        });
    }

    private void setupMaintenanceEquipmentTable() {
        colEquipmentCode.setCellValueFactory(new PropertyValueFactory<>("equipmentCode"));
        
        colEquipmentType.setCellValueFactory(cellData -> {
            RoomEquipment re = cellData.getValue();
            EquipmentType type = equipmentTypeController.getEquipmentTypeById(re.getEquipmentTypeId());
            return new SimpleStringProperty(type != null ? type.getEquipmentName() : "");
        });
        
        colEquipmentRoom.setCellValueFactory(cellData -> {
            RoomEquipment re = cellData.getValue();
            Room room = roomController.getRoomById(re.getRoomId());
            return new SimpleStringProperty(room != null ? room.getRoomName() : "");
        });
        
        colEquipmentQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colEquipmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Setup maintenance actions column
        colMaintenanceActions.setCellFactory(col -> new TableCell<RoomEquipment, Void>() {
            private final Button btnRepair = new Button("Sửa chữa");
            
            {
                btnRepair.getStyleClass().add("btn-repair");
                btnRepair.setOnAction(event -> {
                    RoomEquipment equipment = getTableView().getItems().get(getIndex());
                    handleRepairEquipment(equipment);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRepair);
            }
        });
    }

    private void loadRoomData() {
        List<Room> rooms = roomController.getAllRooms();
        // Sắp xếp danh sách phòng theo mã phòng
        rooms.sort((r1, r2) -> r1.getRoomCode().compareTo(r2.getRoomCode()));
        ObservableList<Room> roomData = FXCollections.observableArrayList(rooms);
        tblRooms.setItems(roomData);
    }

    private void loadMaintenanceEquipmentData() {
        List<RoomEquipment> maintenanceEquipment = roomEquipmentController.getRoomEquipmentsByStatus("Bảo trì");
        ObservableList<RoomEquipment> data = FXCollections.observableArrayList(maintenanceEquipment);
        tblMaintenanceEquipment.setItems(data);
    }

    public void setCurrentUser(User user) {
        try {
            this.currentUser = user;
            if (currentUser != null && pageTitle != null) {
                pageTitle.setText("Quản lý thiết bị tập - " + currentUser.getFullName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        try {
            // Update total rooms
            List<Room> rooms = roomController.getAllRooms();
            lblTotalRooms.setText(String.valueOf(rooms.size()));

            // Update total equipment types
            List<EquipmentType> equipmentTypes = equipmentTypeController.getAllEquipmentTypes();
            lblTotalEquipmentTypes.setText(String.valueOf(equipmentTypes.size()));

            // Update maintenance equipment count - calculate total quantity
            List<RoomEquipment> maintenanceEquipment = roomEquipmentController.getRoomEquipmentsByStatus("Bảo trì");
            int totalMaintenanceQuantity = maintenanceEquipment.stream()
                .mapToInt(RoomEquipment::getQuantity)
                .sum();
            lblMaintenanceEquipment.setText(String.valueOf(totalMaintenanceQuantity));
            
            // Refresh maintenance equipment table
            loadMaintenanceEquipmentData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRoomEquipmentDetails(Room room) {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Chi tiết thiết bị phòng");
            dialog.setHeaderText("Danh sách thiết bị của phòng: " + room.getRoomName());
            dialog.getDialogPane().getStyleClass().add("details-dialog");

            // Create table
            TableView<RoomEquipment> table = new TableView<>();
            table.getStyleClass().add("details-table");

            // Create columns
            TableColumn<RoomEquipment, String> codeColumn = new TableColumn<>("Mã thiết bị");
            codeColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentCode"));
            codeColumn.setPrefWidth(100);

            TableColumn<RoomEquipment, String> typeColumn = new TableColumn<>("Loại thiết bị");
            typeColumn.setCellValueFactory(cellData -> {
                RoomEquipment re = cellData.getValue();
                EquipmentType type = equipmentTypeController.getEquipmentTypeById(re.getEquipmentTypeId());
                return new SimpleStringProperty(type != null ? type.getEquipmentName() : "");
            });
            typeColumn.setPrefWidth(200);

            TableColumn<RoomEquipment, Integer> quantityColumn = new TableColumn<>("Số lượng");
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            quantityColumn.setPrefWidth(100);

            TableColumn<RoomEquipment, String> statusColumn = new TableColumn<>("Trạng thái");
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusColumn.setPrefWidth(100);

            TableColumn<RoomEquipment, String> descColumn = new TableColumn<>("Mô tả");
            descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            descColumn.setPrefWidth(300);

            // Add actions column
            TableColumn<RoomEquipment, Void> actionsColumn = new TableColumn<>("Thao tác");
            actionsColumn.setPrefWidth(100);
            actionsColumn.setCellFactory(col -> new TableCell<RoomEquipment, Void>() {
                private final Button btnEdit = new Button("Sửa");
                private final Button btnDelete = new Button("Xóa");
                private final HBox buttons = new HBox(5, btnEdit, btnDelete);
                
                {
                    buttons.setAlignment(Pos.CENTER);
                    btnEdit.getStyleClass().add("btn-edit");
                    btnDelete.getStyleClass().add("btn-delete");
                    
                    btnEdit.setOnAction(event -> {
                        RoomEquipment equipment = getTableView().getItems().get(getIndex());
                        handleEditRoomEquipment(equipment, table);
                    });
                    
                    btnDelete.setOnAction(event -> {
                        RoomEquipment equipment = getTableView().getItems().get(getIndex());
                        handleDeleteRoomEquipment(equipment, table);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttons);
                }
            });

            table.getColumns().addAll(codeColumn, typeColumn, quantityColumn, statusColumn, descColumn, actionsColumn);

            // Load data
            List<RoomEquipment> equipments = roomEquipmentController.getRoomEquipmentsByRoomId(room.getRoomId());
            ObservableList<RoomEquipment> data = FXCollections.observableArrayList(equipments);
            table.setItems(data);

            // Create add button
            Button btnAdd = new Button("Thêm thiết bị");
            btnAdd.getStyleClass().add("btn-add");
            btnAdd.setOnAction(event -> handleAddRoomEquipment(room, table));

            // Create layout
            VBox content = new VBox(10);
            content.getChildren().addAll(btnAdd, table);
            dialog.getDialogPane().setContent(content);

            // Add close button
            ButtonType closeButton = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);

            // Show dialog
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể hiển thị chi tiết thiết bị");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleDeleteRoomEquipment(RoomEquipment equipment, TableView<RoomEquipment> table) {
        try {
            // Create quantity input dialog
            Dialog<Integer> quantityDialog = new Dialog<>();
            quantityDialog.setTitle("Xóa thiết bị");
            quantityDialog.setHeaderText("Nhập số lượng thiết bị muốn xóa");

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            Label totalLabel = new Label("Tổng số lượng hiện tại: " + equipment.getQuantity());
            Spinner<Integer> quantitySpinner = new Spinner<>(1, equipment.getQuantity(), 1);
            quantitySpinner.setEditable(true);
            quantitySpinner.setPrefWidth(100);

            grid.add(totalLabel, 0, 0);
            grid.add(new Label("Số lượng muốn xóa:"), 0, 1);
            grid.add(quantitySpinner, 1, 1);

            quantityDialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Xóa", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            quantityDialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            quantityDialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    return quantitySpinner.getValue();
                }
                return null;
            });

            Optional<Integer> quantityResult = quantityDialog.showAndWait();
            quantityResult.ifPresent(quantityToRemove -> {
                try {
                    if (quantityToRemove >= equipment.getQuantity()) {
                        // Nếu số lượng xóa >= số lượng hiện tại, xóa bản ghi
                        if (roomEquipmentController.deleteRoomEquipment(equipment.getRoomEquipmentId())) {
                            table.getItems().remove(equipment);
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Thành công");
                            successAlert.setHeaderText("Xóa thiết bị thành công");
                            successAlert.setContentText("Đã xóa thiết bị khỏi phòng.");
                            successAlert.showAndWait();
                        } else {
                            throw new Exception("Không thể xóa thiết bị");
                        }
                    } else {
                        // Nếu số lượng xóa < số lượng hiện tại, cập nhật số lượng
                        int newQuantity = equipment.getQuantity() - quantityToRemove;
                        equipment.setQuantity(newQuantity);
                        if (!roomEquipmentController.updateRoomEquipmentQuantity(equipment.getRoomEquipmentId(), newQuantity)) {
                            throw new Exception("Không thể cập nhật số lượng thiết bị");
                        }
                        table.refresh(); // Refresh để hiển thị số lượng mới
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Thành công");
                        successAlert.setHeaderText("Cập nhật số lượng thành công");
                        successAlert.setContentText("Đã giảm " + quantityToRemove + " thiết bị khỏi phòng.");
                        successAlert.showAndWait();
                    }
                    
                    updateStatistics();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể xóa thiết bị");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể xóa thiết bị");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleAddRoomEquipment(Room room, TableView<RoomEquipment> table) {
        try {
            Dialog<RoomEquipment> dialog = new Dialog<>();
            dialog.setTitle("Thêm thiết bị mới");
            dialog.setHeaderText("Thêm thiết bị mới vào phòng: " + room.getRoomName());

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Equipment type selection
            ComboBox<EquipmentType> typeComboBox = new ComboBox<>();
            typeComboBox.setPromptText("Chọn loại thiết bị");
            typeComboBox.setItems(FXCollections.observableArrayList(
                equipmentTypeController.getAllEquipmentTypes().stream()
                    .filter(type -> type.getStatus() == null || !type.getStatus().equals("Đã xóa"))
                    .collect(Collectors.toList())
            ));
            
            // Cấu hình hiển thị cho ComboBox
            typeComboBox.setConverter(new StringConverter<EquipmentType>() {
                @Override
                public String toString(EquipmentType type) {
                    return type == null ? "" : type.getEquipmentName();
                }

                @Override
                public EquipmentType fromString(String string) {
                    return null;
                }
            });
            
            typeComboBox.setCellFactory(param -> new ListCell<EquipmentType>() {
                @Override
                protected void updateItem(EquipmentType type, boolean empty) {
                    super.updateItem(type, empty);
                    if (empty || type == null) {
                        setText(null);
                    } else {
                        setText(type.getEquipmentName());
                    }
                }
            });

            // Quantity input
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
            quantitySpinner.setEditable(true);
            quantitySpinner.setPrefWidth(100);

            // Equipment code
            TextField txtEquipmentCode = new TextField();
            txtEquipmentCode.setPromptText("Mã thiết bị");

            // Description
            TextArea txtDescription = new TextArea();
            txtDescription.setPromptText("Mô tả");
            txtDescription.setPrefRowCount(3);

            // Status
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setPromptText("Chọn trạng thái");
            statusComboBox.getItems().addAll("Hoạt động", "Bảo trì", "Tạm ngừng");

            grid.add(new Label("Loại thiết bị:"), 0, 0);
            grid.add(typeComboBox, 1, 0);
            grid.add(new Label("Số lượng:"), 0, 1);
            grid.add(quantitySpinner, 1, 1);
            grid.add(new Label("Mã thiết bị:"), 0, 2);
            grid.add(txtEquipmentCode, 1, 2);
            grid.add(new Label("Mô tả:"), 0, 3);
            grid.add(txtDescription, 1, 3);
            grid.add(new Label("Trạng thái:"), 0, 4);
            grid.add(statusComboBox, 1, 4);

            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    try {
                        if (typeComboBox.getValue() == null) {
                            throw new IllegalArgumentException("Vui lòng chọn loại thiết bị");
                        }
                        if (txtEquipmentCode.getText().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng nhập mã thiết bị");
                        }
                        if (roomEquipmentController.isEquipmentCodeExists(txtEquipmentCode.getText())) {
                            throw new IllegalArgumentException("Mã thiết bị đã tồn tại");
                        }

                        RoomEquipment equipment = new RoomEquipment();
                        equipment.setRoomId(room.getRoomId());
                        equipment.setEquipmentTypeId(typeComboBox.getValue().getEquipmentTypeId());
                        equipment.setEquipmentCode(txtEquipmentCode.getText());
                        equipment.setQuantity(quantitySpinner.getValue());
                        equipment.setDescription(txtDescription.getText());
                        equipment.setStatus(statusComboBox.getValue());
                        
                        // Set created and updated dates
                        LocalDateTime now = LocalDateTime.now();
                        equipment.setCreatedDate(now);
                        equipment.setUpdatedDate(now);
                        
                        return equipment;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Dữ liệu không hợp lệ");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<RoomEquipment> result = dialog.showAndWait();
            result.ifPresent(equipment -> {
                try {
                    if (roomEquipmentController.addRoomEquipment(equipment)) {
                        table.getItems().add(equipment);
                        updateStatistics();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText("Thêm thiết bị thành công");
                        alert.setContentText("Thiết bị đã được thêm vào phòng.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("Không thể thêm thiết bị");
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể thêm thiết bị");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể thêm thiết bị");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleViewEquipmentTypes() {
        try {
            // Create dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Danh sách loại thiết bị");
            dialog.setHeaderText("Chi tiết các loại thiết bị trong phòng tập");
            dialog.getDialogPane().getStyleClass().add("details-dialog");

            // Create table
            TableView<EquipmentType> table = new TableView<>();
            table.getStyleClass().add("details-table");

            // Create columns
            TableColumn<EquipmentType, String> nameColumn = new TableColumn<>("Tên thiết bị");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
            nameColumn.setPrefWidth(200);

            TableColumn<EquipmentType, String> descColumn = new TableColumn<>("Mô tả");
            descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            descColumn.setPrefWidth(300);

            // Create actions column
            TableColumn<EquipmentType, Void> actionsColumn = new TableColumn<>("Thao tác");
            actionsColumn.setPrefWidth(150);
            actionsColumn.setCellFactory(col -> new TableCell<EquipmentType, Void>() {
                private final Button btnEdit = new Button("Sửa");
                private final Button btnDelete = new Button("Xóa");
                private final HBox buttons = new HBox(5, btnEdit, btnDelete);

                {
                    buttons.setAlignment(Pos.CENTER);
                    btnEdit.getStyleClass().add("btn-edit");
                    btnDelete.getStyleClass().add("btn-delete");

                    btnEdit.setOnAction(event -> {
                        EquipmentType equipmentType = getTableView().getItems().get(getIndex());
                        handleEditEquipmentType(equipmentType, table);
                    });

                    btnDelete.setOnAction(event -> {
                        EquipmentType equipmentType = getTableView().getItems().get(getIndex());
                        handleDeleteEquipmentType(equipmentType, table);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttons);
                }
            });

            table.getColumns().addAll(nameColumn, descColumn, actionsColumn);

            // Load data (excluding deleted items)
            List<EquipmentType> equipmentTypes = equipmentTypeController.getAllEquipmentTypes().stream()
                .filter(type -> type.getStatus() == null || !type.getStatus().equals("Đã xóa"))
                .collect(Collectors.toList());
            ObservableList<EquipmentType> data = FXCollections.observableArrayList(equipmentTypes);
            table.setItems(data);

            // Create add button
            Button btnAdd = new Button("Thêm thiết bị mới");
            btnAdd.getStyleClass().add("btn-add");
            btnAdd.setOnAction(event -> handleAddEquipmentType(table));

            // Create layout
            VBox content = new VBox(10);
            content.getChildren().addAll(btnAdd, table);
            dialog.getDialogPane().setContent(content);

            // Add close button
            ButtonType closeButton = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);

            // Show dialog
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể hiển thị danh sách thiết bị");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleAddEquipmentType(TableView<EquipmentType> table) {
        try {
            Dialog<EquipmentType> dialog = new Dialog<>();
            dialog.setTitle("Thêm thiết bị mới");
            dialog.setHeaderText("Nhập thông tin thiết bị mới");

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField txtName = new TextField();
            txtName.setPromptText("Tên thiết bị");
            TextArea txtDescription = new TextArea();
            txtDescription.setPromptText("Mô tả");
            txtDescription.setPrefRowCount(3);

            grid.add(new Label("Tên thiết bị:"), 0, 0);
            grid.add(txtName, 1, 0);
            grid.add(new Label("Mô tả:"), 0, 1);
            grid.add(txtDescription, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    try {
                        if (txtName.getText().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng nhập tên thiết bị");
                        }

                        EquipmentType equipmentType = new EquipmentType();
                        equipmentType.setEquipmentName(txtName.getText());
                        equipmentType.setDescription(txtDescription.getText());
                        return equipmentType;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Dữ liệu không hợp lệ");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<EquipmentType> result = dialog.showAndWait();
            result.ifPresent(equipmentType -> {
                try {
                    if (equipmentTypeController.addEquipmentType(equipmentType)) {
                        refreshTable(table);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText("Thêm thiết bị thành công");
                        alert.setContentText("Thiết bị đã được thêm vào hệ thống.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("Không thể thêm thiết bị");
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể thêm thiết bị");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEditEquipmentType(EquipmentType equipmentType, TableView<EquipmentType> table) {
        try {
            Dialog<EquipmentType> dialog = new Dialog<>();
            dialog.setTitle("Sửa thiết bị");
            dialog.setHeaderText("Cập nhật thông tin thiết bị");

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField txtName = new TextField(equipmentType.getEquipmentName());
            txtName.setPromptText("Tên thiết bị");
            TextArea txtDescription = new TextArea(equipmentType.getDescription());
            txtDescription.setPromptText("Mô tả");
            txtDescription.setPrefRowCount(3);

            grid.add(new Label("Tên thiết bị:"), 0, 0);
            grid.add(txtName, 1, 0);
            grid.add(new Label("Mô tả:"), 0, 1);
            grid.add(txtDescription, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    try {
                        if (txtName.getText().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng nhập tên thiết bị");
                        }

                        equipmentType.setEquipmentName(txtName.getText());
                        equipmentType.setDescription(txtDescription.getText());
                        return equipmentType;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Dữ liệu không hợp lệ");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<EquipmentType> result = dialog.showAndWait();
            result.ifPresent(updatedEquipmentType -> {
                try {
                    if (equipmentTypeController.updateEquipmentType(updatedEquipmentType)) {
                        refreshTable(table);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText("Cập nhật thiết bị thành công");
                        alert.setContentText("Thông tin thiết bị đã được cập nhật.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("Không thể cập nhật thiết bị");
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể cập nhật thiết bị");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteEquipmentType(EquipmentType equipmentType, TableView<EquipmentType> table) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Xóa thiết bị");
            alert.setContentText("Bạn có chắc chắn muốn xóa thiết bị này?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                equipmentType.setStatus("Đã xóa");
                if (equipmentTypeController.updateEquipmentType(equipmentType)) {
                    refreshTable(table);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText("Xóa thiết bị thành công");
                    successAlert.setContentText("Thiết bị đã được xóa khỏi hệ thống.");
                    successAlert.showAndWait();
                } else {
                    throw new Exception("Không thể xóa thiết bị");
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể xóa thiết bị");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void refreshTable(TableView<EquipmentType> table) {
        List<EquipmentType> equipmentTypes = equipmentTypeController.getAllEquipmentTypes().stream()
            .filter(type -> type.getStatus() == null || !type.getStatus().equals("Đã xóa"))
            .collect(Collectors.toList());
        table.setItems(FXCollections.observableArrayList(equipmentTypes));
    }

    @FXML
    private void handleAddRoom() {
        try {
            Dialog<Room> dialog = new Dialog<>();
            dialog.setTitle("Thêm phòng tập mới");
            dialog.setHeaderText("Nhập thông tin phòng tập");

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField txtRoomCode = new TextField();
            txtRoomCode.setPromptText("Mã phòng");
            TextField txtRoomName = new TextField();
            txtRoomName.setPromptText("Tên phòng");
            TextField txtRoomType = new TextField();
            txtRoomType.setPromptText("Loại phòng");
            TextArea txtDescription = new TextArea();
            txtDescription.setPromptText("Mô tả");
            txtDescription.setPrefRowCount(3);

            grid.add(new Label("Mã phòng:"), 0, 0);
            grid.add(txtRoomCode, 1, 0);
            grid.add(new Label("Tên phòng:"), 0, 1);
            grid.add(txtRoomName, 1, 1);
            grid.add(new Label("Loại phòng:"), 0, 2);
            grid.add(txtRoomType, 1, 2);
            grid.add(new Label("Mô tả:"), 0, 3);
            grid.add(txtDescription, 1, 3);

            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    try {
                        if (txtRoomCode.getText().isEmpty() || txtRoomName.getText().isEmpty() || txtRoomType.getText().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin bắt buộc");
                        }

                        Room room = new Room();
                        room.setRoomCode(txtRoomCode.getText());
                        room.setRoomName(txtRoomName.getText());
                        room.setRoomType(txtRoomType.getText());
                        room.setDescription(txtDescription.getText());
                        room.setStatus("Hoạt động");
                        return room;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Dữ liệu không hợp lệ");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<Room> result = dialog.showAndWait();
            result.ifPresent(room -> {
                try {
                    if (roomController.addRoom(room)) {
                        loadRoomData();
                        updateStatistics();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText("Thêm phòng tập thành công");
                        alert.setContentText("Phòng tập đã được thêm vào hệ thống.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("Không thể thêm phòng tập");
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể thêm phòng tập");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditRoom() {
        try {
            Dialog<Room> dialog = new Dialog<>();
            dialog.setTitle("Sửa thông tin phòng tập");
            dialog.setHeaderText("Chọn và cập nhật thông tin phòng tập");

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Room selection combo box
            ComboBox<Room> roomComboBox = new ComboBox<>();
            roomComboBox.setPromptText("Chọn phòng tập");
            
            // Lấy danh sách phòng và sắp xếp theo mã phòng
            List<Room> rooms = roomController.getAllRooms();
            rooms.sort((r1, r2) -> r1.getRoomCode().compareTo(r2.getRoomCode()));
            roomComboBox.setItems(FXCollections.observableArrayList(rooms));
            
            // Cấu hình hiển thị cho ComboBox
            roomComboBox.setConverter(new StringConverter<Room>() {
                @Override
                public String toString(Room room) {
                    return room == null ? "" : room.getRoomCode() + " - " + room.getRoomName();
                }

                @Override
                public Room fromString(String string) {
                    return null;
                }
            });
            
            roomComboBox.setCellFactory(param -> new ListCell<Room>() {
                @Override
                protected void updateItem(Room room, boolean empty) {
                    super.updateItem(room, empty);
                    if (empty || room == null) {
                        setText(null);
                    } else {
                        setText(room.getRoomCode() + " - " + room.getRoomName());
                    }
                }
            });

            // Form fields
            TextField txtRoomName = new TextField();
            txtRoomName.setPromptText("Tên phòng");
            TextField txtRoomType = new TextField();
            txtRoomType.setPromptText("Loại phòng");
            TextArea txtDescription = new TextArea();
            txtDescription.setPromptText("Mô tả");
            txtDescription.setPrefRowCount(3);

            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setPromptText("Chọn trạng thái");
            statusComboBox.getItems().addAll("Hoạt động", "Bảo trì", "Tạm ngừng");

            // Add fields to grid
            grid.add(new Label("Chọn phòng:"), 0, 0);
            grid.add(roomComboBox, 1, 0);
            grid.add(new Label("Tên phòng:"), 0, 1);
            grid.add(txtRoomName, 1, 1);
            grid.add(new Label("Loại phòng:"), 0, 2);
            grid.add(txtRoomType, 1, 2);
            grid.add(new Label("Mô tả:"), 0, 3);
            grid.add(txtDescription, 1, 3);
            grid.add(new Label("Trạng thái:"), 0, 4);
            grid.add(statusComboBox, 1, 4);

            // Update form fields when a room is selected
            roomComboBox.setOnAction(e -> {
                Room selectedRoom = roomComboBox.getValue();
                if (selectedRoom != null) {
                    txtRoomName.setText(selectedRoom.getRoomName());
                    txtRoomType.setText(selectedRoom.getRoomType());
                    txtDescription.setText(selectedRoom.getDescription());
                    statusComboBox.setValue(selectedRoom.getStatus());
                }
            });

            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeSave = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeSave, buttonTypeCancel);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeSave) {
                    try {
                        if (roomComboBox.getValue() == null) {
                            throw new IllegalArgumentException("Vui lòng chọn phòng cần sửa");
                        }
                        if (txtRoomName.getText().isEmpty() || txtRoomType.getText().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin bắt buộc");
                        }

                        Room room = roomComboBox.getValue();
                        room.setRoomName(txtRoomName.getText());
                        room.setRoomType(txtRoomType.getText());
                        room.setDescription(txtDescription.getText());
                        room.setStatus(statusComboBox.getValue());
                        return room;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Dữ liệu không hợp lệ");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<Room> result = dialog.showAndWait();
            result.ifPresent(updatedRoom -> {
                try {
                    if (roomController.updateRoom(updatedRoom)) {
                        loadRoomData();
                        updateStatistics();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText("Cập nhật phòng tập thành công");
                        alert.setContentText("Thông tin phòng tập đã được cập nhật.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("Không thể cập nhật phòng tập");
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể cập nhật phòng tập");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRepairEquipment(RoomEquipment equipment) {
        try {
            // Update equipment status to "Hoạt động"
            if (roomEquipmentController.updateRoomEquipmentStatus(equipment.getRoomEquipmentId(), "Hoạt động")) {
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText("Thông báo sửa chữa");
                alert.setContentText("Đã thông báo tới nhân viên sửa chữa");
                alert.showAndWait();

                // Refresh the maintenance equipment table
                loadMaintenanceEquipmentData();
                updateStatistics();
            } else {
                throw new Exception("Không thể cập nhật trạng thái thiết bị");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể xử lý yêu cầu sửa chữa");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleEditRoomEquipment(RoomEquipment equipment, TableView<RoomEquipment> table) {
        try {
            Dialog<RoomEquipment> dialog = new Dialog<>();
            dialog.setTitle("Sửa thiết bị");
            dialog.setHeaderText("Cập nhật thông tin thiết bị");

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Equipment type selection
            ComboBox<EquipmentType> typeComboBox = new ComboBox<>();
            typeComboBox.setItems(FXCollections.observableArrayList(
                equipmentTypeController.getAllEquipmentTypes().stream()
                    .filter(type -> type.getStatus() == null || !type.getStatus().equals("Đã xóa"))
                    .collect(Collectors.toList())
            ));
            
            // Cấu hình hiển thị cho ComboBox
            typeComboBox.setConverter(new StringConverter<EquipmentType>() {
                @Override
                public String toString(EquipmentType type) {
                    return type == null ? "" : type.getEquipmentName();
                }

                @Override
                public EquipmentType fromString(String string) {
                    return null;
                }
            });
            
            typeComboBox.setCellFactory(param -> new ListCell<EquipmentType>() {
                @Override
                protected void updateItem(EquipmentType type, boolean empty) {
                    super.updateItem(type, empty);
                    if (empty || type == null) {
                        setText(null);
                    } else {
                        setText(type.getEquipmentName());
                    }
                }
            });

            // Set current equipment type
            EquipmentType currentType = equipmentTypeController.getEquipmentTypeById(equipment.getEquipmentTypeId());
            typeComboBox.setValue(currentType);

            // Quantity input
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, equipment.getQuantity());
            quantitySpinner.setEditable(true);
            quantitySpinner.setPrefWidth(100);

            // Description
            TextArea txtDescription = new TextArea(equipment.getDescription());
            txtDescription.setPromptText("Mô tả");
            txtDescription.setPrefRowCount(3);

            // Status
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.setPromptText("Chọn trạng thái");
            statusComboBox.getItems().addAll("Hoạt động", "Bảo trì", "Tạm ngừng");
            statusComboBox.setValue(equipment.getStatus());

            grid.add(new Label("Loại thiết bị:"), 0, 0);
            grid.add(typeComboBox, 1, 0);
            grid.add(new Label("Số lượng:"), 0, 1);
            grid.add(quantitySpinner, 1, 1);
            grid.add(new Label("Mô tả:"), 0, 2);
            grid.add(txtDescription, 1, 2);
            grid.add(new Label("Trạng thái:"), 0, 3);
            grid.add(statusComboBox, 1, 3);

            dialog.getDialogPane().setContent(grid);

            ButtonType buttonTypeOk = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    try {
                        if (typeComboBox.getValue() == null) {
                            throw new IllegalArgumentException("Vui lòng chọn loại thiết bị");
                        }

                        equipment.setEquipmentTypeId(typeComboBox.getValue().getEquipmentTypeId());
                        equipment.setQuantity(quantitySpinner.getValue());
                        equipment.setDescription(txtDescription.getText());
                        equipment.setStatus(statusComboBox.getValue());
                        equipment.setUpdatedDate(LocalDateTime.now());
                        
                        return equipment;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi");
                        alert.setHeaderText("Dữ liệu không hợp lệ");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<RoomEquipment> result = dialog.showAndWait();
            result.ifPresent(updatedEquipment -> {
                try {
                    if (roomEquipmentController.updateRoomEquipment(updatedEquipment)) {
                        table.refresh();
                        updateStatistics();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText("Cập nhật thiết bị thành công");
                        alert.setContentText("Thông tin thiết bị đã được cập nhật.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("Không thể cập nhật thiết bị");
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText("Không thể cập nhật thiết bị");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể cập nhật thiết bị");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}