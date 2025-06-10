package view.ownerView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
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
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class EquipmentView implements Initializable {
    @FXML private Label pageTitle;
    @FXML private Button btnAddEquipment;
    @FXML private ComboBox<String> cboRoomFilter;
    @FXML private ComboBox<String> cboStatusFilter;
    @FXML private TextField txtSearch;
    @FXML private TableView<RoomEquipment> tblEquipment;
    @FXML private TableColumn<RoomEquipment, String> colEquipmentCode;
    @FXML private TableColumn<RoomEquipment, String> colEquipmentName;
    @FXML private TableColumn<RoomEquipment, String> colRoom;
    @FXML private TableColumn<RoomEquipment, Integer> colQuantity;
    @FXML private TableColumn<RoomEquipment, String> colStatus;
    @FXML private TableColumn<RoomEquipment, String> colDescription;
    @FXML private TableColumn<RoomEquipment, Void> colActions;

    private User currentUser;
    private ObservableList<RoomEquipment> equipmentList;
    private FilteredList<RoomEquipment> filteredData;
    private RoomEquipmentController roomEquipmentController;
    private EquipmentTypeController equipmentTypeController;
    private RoomController roomController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            roomEquipmentController = new RoomEquipmentController();
            equipmentTypeController = new EquipmentTypeController();
            roomController = new RoomController();
            
            setupTableColumns();
            //setupFilters();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void setupTableColumns() {
        try {
            // Setup basic columns
            colEquipmentCode.setCellValueFactory(new PropertyValueFactory<>("equipmentCode"));
            colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

            // Setup equipment name column
            colEquipmentName.setCellValueFactory(cellData -> {
                RoomEquipment equipment = cellData.getValue();
                EquipmentType type = equipmentTypeController.getEquipmentTypeById(equipment.getEquipmentTypeId());
                return new SimpleStringProperty(type != null ? type.getEquipmentName() : "");
            });

            // Setup room column
            colRoom.setCellValueFactory(cellData -> {
                RoomEquipment equipment = cellData.getValue();
                List<Room> rooms = roomController.getAllRooms();
                Room room = rooms.stream()
                    .filter(r -> r.getRoomId() == equipment.getRoomId())
                    .findFirst()
                    .orElse(null);
                return new SimpleStringProperty(room != null ? room.getRoomName() : "");
            });

            // Setup status column with custom cell factory
            colStatus.setCellFactory(column -> new TableCell<RoomEquipment, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);
                        switch (status) {
                            case "Hoạt động":
                                setStyle("-fx-text-fill: green;");
                                break;
                            case "Bảo trì":
                                setStyle("-fx-text-fill: orange;");
                                break;
                            case "Hỏng":
                                setStyle("-fx-text-fill: red;");
                                break;
                            default:
                                setStyle("");
                        }
                    }
                }
            });

            // Setup actions column
            //colActions.setCellFactory(createActionButtonCellFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private Callback<TableColumn<RoomEquipment, Void>, TableCell<RoomEquipment, Void>> createActionButtonCellFactory() {
    //     return new Callback<TableColumn<RoomEquipment, Void>, TableCell<RoomEquipment, Void>>() {
    //         @Override
    //         public TableCell<RoomEquipment, Void> call(TableColumn<RoomEquipment, Void> param) {
    //             return new TableCell<RoomEquipment, Void>() {
    //                 private final Button btnEdit = new Button("Sửa");
    //                 private final Button btnDelete = new Button("Xóa");
    //                 private final HBox buttons = new HBox(5, btnEdit, btnDelete);

    //                 {
    //                     buttons.setAlignment(Pos.CENTER);
    //                     btnEdit.getStyleClass().add("btn-secondary");
    //                     btnDelete.getStyleClass().add("btn-danger");

    //                     btnEdit.setOnAction(event -> {
    //                         RoomEquipment equipment = getTableView().getItems().get(getIndex());
    //                         handleEditEquipment(equipment);
    //                     });

    //                     btnDelete.setOnAction(event -> {
    //                         RoomEquipment equipment = getTableView().getItems().get(getIndex());
    //                         handleDeleteEquipment(equipment);
    //                     });
    //                 }

    //                 @Override
    //                 protected void updateItem(Void item, boolean empty) {
    //                     super.updateItem(item, empty);
    //                     setGraphic(empty ? null : buttons);
    //                 }
    //             };
    //         }
    //     };
    // }

    // private void setupFilters() {
    //     try {
    //         // Setup room filter
    //         List<Room> rooms = roomController.getAllRooms();
    //         cboRoomFilter.setItems(FXCollections.observableArrayList(
    //             rooms.stream().map(Room::getRoomName).collect(Collectors.toList())
    //         ));
    //         cboRoomFilter.getItems().add(0, "Tất cả phòng");
    //         cboRoomFilter.setValue("Tất cả phòng");

    //         // Setup status filter
    //         cboStatusFilter.setItems(FXCollections.observableArrayList(
    //             "Tất cả trạng thái", "Hoạt động", "Bảo trì", "Hỏng"
    //         ));
    //         cboStatusFilter.setValue("Tất cả trạng thái");

    //         // Setup search filter
    //         txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
    //             applyFilters();
    //         });

    //         cboRoomFilter.setOnAction(event -> applyFilters());
    //         cboStatusFilter.setOnAction(event -> applyFilters());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    private void loadData() {
        try {
            List<RoomEquipment> equipment = roomEquipmentController.getAllRoomEquipments();
            equipmentList = FXCollections.observableArrayList(equipment);
            filteredData = new FilteredList<>(equipmentList);
            SortedList<RoomEquipment> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tblEquipment.comparatorProperty());
            tblEquipment.setItems(sortedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private void applyFilters() {
    //     try {
    //         filteredData.setPredicate(equipment -> {
    //             boolean matchesRoom = cboRoomFilter.getValue() == null || 
    //                                 cboRoomFilter.getValue().equals("Tất cả phòng") ||
    //                                 roomController.getAllRooms().stream()
    //                                     .filter(r -> r.getRoomId() == equipment.getRoomId())
    //                                     .findFirst()
    //                                     .map(Room::getRoomName)
    //                                     .orElse("")
    //                                     .equals(cboRoomFilter.getValue());

    //             boolean matchesStatus = cboStatusFilter.getValue() == null || 
    //                                   cboStatusFilter.getValue().equals("Tất cả trạng thái") ||
    //                                   equipment.getStatus().equals(cboStatusFilter.getValue());

    //             boolean matchesSearch = txtSearch.getText().isEmpty() ||
    //                                   equipment.getEquipmentCode().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
    //                                   equipmentTypeController.getEquipmentTypeById(equipment.getEquipmentTypeId()).getEquipmentName().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
    //                                   equipment.getDescription().toLowerCase().contains(txtSearch.getText().toLowerCase());

    //             return matchesRoom && matchesStatus && matchesSearch;
    //         });
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // @FXML
    // private void handleAddEquipment() {
    //     try {
    //         Dialog<RoomEquipment> dialog = new Dialog<>();
    //         dialog.setTitle("Thêm thiết bị mới");
    //         dialog.setHeaderText("Nhập thông tin thiết bị");

    //         // Create the custom dialog content
    //         GridPane grid = new GridPane();
    //         grid.setHgap(10);
    //         grid.setVgap(10);
    //         grid.setPadding(new Insets(20, 150, 10, 10));

    //         ComboBox<EquipmentType> cboEquipmentType = new ComboBox<>();
    //         cboEquipmentType.setItems(FXCollections.observableArrayList(equipmentTypeController.getAllEquipmentTypes()));
    //         cboEquipmentType.setPromptText("Chọn loại thiết bị");
    //         cboEquipmentType.setCellFactory(param -> new ListCell<EquipmentType>() {
    //             @Override
    //             protected void updateItem(EquipmentType item, boolean empty) {
    //                 super.updateItem(item, empty);
    //                 if (empty || item == null) {
    //                     setText(null);
    //                 } else {
    //                     setText(item.getEquipmentName());
    //                 }
    //             }
    //         });
    //         cboEquipmentType.setButtonCell(new ListCell<EquipmentType>() {
    //             @Override
    //             protected void updateItem(EquipmentType item, boolean empty) {
    //                 super.updateItem(item, empty);
    //                 if (empty || item == null) {
    //                     setText(null);
    //                 } else {
    //                     setText(item.getEquipmentName());
    //                 }
    //             }
    //         });

    //         ComboBox<Room> cboRoom = new ComboBox<>();
    //         cboRoom.setItems(FXCollections.observableArrayList(roomController.getAllRooms()));
    //         cboRoom.setPromptText("Chọn phòng");
    //         cboRoom.setCellFactory(param -> new ListCell<Room>() {
    //             @Override
    //             protected void updateItem(Room item, boolean empty) {
    //                 super.updateItem(item, empty);
    //                 if (empty || item == null) {
    //                     setText(null);
    //                 } else {
    //                     setText(item.getRoomName());
    //                 }
    //             }
    //         });
    //         cboRoom.setButtonCell(new ListCell<Room>() {
    //             @Override
    //             protected void updateItem(Room item, boolean empty) {
    //                 super.updateItem(item, empty);
    //                 if (empty || item == null) {
    //                     setText(null);
    //                 } else {
    //                     setText(item.getRoomName());
    //                 }
    //             }
    //         });

    //         TextField txtQuantity = new TextField();
    //         txtQuantity.setPromptText("Số lượng");

    //         ComboBox<String> cboStatus = new ComboBox<>();
    //         cboStatus.setItems(FXCollections.observableArrayList("Hoạt động", "Bảo trì", "Hỏng"));
    //         cboStatus.setPromptText("Trạng thái");

    //         TextArea txtDescription = new TextArea();
    //         txtDescription.setPromptText("Mô tả");
    //         txtDescription.setPrefRowCount(3);

    //         grid.add(new Label("Loại thiết bị:"), 0, 0);
    //         grid.add(cboEquipmentType, 1, 0);
    //         grid.add(new Label("Phòng:"), 0, 1);
    //         grid.add(cboRoom, 1, 1);
    //         grid.add(new Label("Số lượng:"), 0, 2);
    //         grid.add(txtQuantity, 1, 2);
    //         grid.add(new Label("Trạng thái:"), 0, 3);
    //         grid.add(cboStatus, 1, 3);
    //         grid.add(new Label("Mô tả:"), 0, 4);
    //         grid.add(txtDescription, 1, 4);

    //         dialog.getDialogPane().setContent(grid);

    //         ButtonType buttonTypeOk = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
    //         ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
    //         dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

    //         dialog.setResultConverter(dialogButton -> {
    //             if (dialogButton == buttonTypeOk) {
    //                 try {
    //                     // Validate input
    //                     if (cboEquipmentType.getValue() == null) {
    //                         throw new IllegalArgumentException("Vui lòng chọn loại thiết bị");
    //                     }
    //                     if (cboRoom.getValue() == null) {
    //                         throw new IllegalArgumentException("Vui lòng chọn phòng");
    //                     }
    //                     if (txtQuantity.getText().isEmpty()) {
    //                         throw new IllegalArgumentException("Vui lòng nhập số lượng");
    //                     }
    //                     if (cboStatus.getValue() == null) {
    //                         throw new IllegalArgumentException("Vui lòng chọn trạng thái");
    //                     }

    //                     // Generate equipment code
    //                     String equipmentCode = generateEquipmentCode(cboEquipmentType.getValue(), cboRoom.getValue());
                        
    //                     // Check if equipment code exists
    //                     if (roomEquipmentController.isEquipmentCodeExists(equipmentCode)) {
    //                         throw new IllegalArgumentException("Mã thiết bị đã tồn tại");
    //                     }

    //                     RoomEquipment equipment = new RoomEquipment();
    //                     equipment.setEquipmentTypeId(cboEquipmentType.getValue().getEquipmentTypeId());
    //                     equipment.setRoomId(cboRoom.getValue().getRoomId());
    //                     equipment.setEquipmentCode(equipmentCode);
    //                     equipment.setQuantity(Integer.parseInt(txtQuantity.getText()));
    //                     equipment.setStatus(cboStatus.getValue());
    //                     equipment.setDescription(txtDescription.getText());
    //                     equipment.setCreatedDate(LocalDateTime.now());
    //                     equipment.setUpdatedDate(LocalDateTime.now());
    //                     return equipment;
    //                 } catch (NumberFormatException e) {
    //                     Alert alert = new Alert(Alert.AlertType.ERROR);
    //                     alert.setTitle("Lỗi");
    //                     alert.setHeaderText("Số lượng không hợp lệ");
    //                     alert.setContentText("Vui lòng nhập số lượng là một số nguyên.");
    //                     alert.showAndWait();
    //                     return null;
    //                 } catch (IllegalArgumentException e) {
    //                     Alert alert = new Alert(Alert.AlertType.ERROR);
    //                     alert.setTitle("Lỗi");
    //                     alert.setHeaderText("Dữ liệu không hợp lệ");
    //                     alert.setContentText(e.getMessage());
    //                     alert.showAndWait();
    //                     return null;
    //                 }
    //             }
    //             return null;
    //         });

    //         Optional<RoomEquipment> result = dialog.showAndWait();
    //         result.ifPresent(equipment -> {
    //             try {
    //                 if (roomEquipmentController.addRoomEquipment(equipment)) {
    //                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //                     alert.setTitle("Thành công");
    //                     alert.setHeaderText("Thêm thiết bị thành công");
    //                     alert.setContentText("Thiết bị đã được thêm vào hệ thống.");
    //                     alert.showAndWait();
    //                     loadData();
    //                 } else {
    //                     throw new Exception("Không thể thêm thiết bị");
    //                 }
    //             } catch (Exception e) {
    //                 Alert alert = new Alert(Alert.AlertType.ERROR);
    //                 alert.setTitle("Lỗi");
    //                 alert.setHeaderText("Không thể thêm thiết bị");
    //                 alert.setContentText(e.getMessage());
    //                 alert.showAndWait();
    //             }
    //         });
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    private String generateEquipmentCode(EquipmentType equipmentType, Room room) {
        // Format: ET-{EquipmentTypeID}-{RoomID}-{Timestamp}
        return String.format("ET-%03d-%03d-%d",
            equipmentType.getEquipmentTypeId(),
            room.getRoomId(),
            System.currentTimeMillis() % 1000);
    }

    // @FXML
    // private void handleEditEquipment(RoomEquipment equipment) {
    //     try {
    //         Dialog<RoomEquipment> dialog = new Dialog<>();
    //         dialog.setTitle("Sửa thiết bị");
    //         dialog.setHeaderText("Cập nhật thông tin thiết bị");

    //         // Create the custom dialog content
    //         GridPane grid = new GridPane();
    //         grid.setHgap(10);
    //         grid.setVgap(10);
    //         grid.setPadding(new Insets(20, 150, 10, 10));

    //         ComboBox<EquipmentType> cboEquipmentType = new ComboBox<>();
    //         cboEquipmentType.setItems(FXCollections.observableArrayList(equipmentTypeController.getAllEquipmentTypes()));
    //         cboEquipmentType.setValue(equipmentTypeController.getEquipmentTypeById(equipment.getEquipmentTypeId()));

    //         ComboBox<Room> cboRoom = new ComboBox<>();
    //         cboRoom.setItems(FXCollections.observableArrayList(roomController.getAllRooms()));
    //         cboRoom.setValue(roomController.getAllRooms().stream()
    //             .filter(r -> r.getRoomId() == equipment.getRoomId())
    //             .findFirst()
    //             .orElse(null));

    //         TextField txtQuantity = new TextField(String.valueOf(equipment.getQuantity()));
    //         ComboBox<String> cboStatus = new ComboBox<>();
    //         cboStatus.setItems(FXCollections.observableArrayList("Hoạt động", "Bảo trì", "Hỏng"));
    //         cboStatus.setValue(equipment.getStatus());

    //         TextArea txtDescription = new TextArea(equipment.getDescription());
    //         txtDescription.setPrefRowCount(3);

    //         grid.add(new Label("Loại thiết bị:"), 0, 0);
    //         grid.add(cboEquipmentType, 1, 0);
    //         grid.add(new Label("Phòng:"), 0, 1);
    //         grid.add(cboRoom, 1, 1);
    //         grid.add(new Label("Số lượng:"), 0, 2);
    //         grid.add(txtQuantity, 1, 2);
    //         grid.add(new Label("Trạng thái:"), 0, 3);
    //         grid.add(cboStatus, 1, 3);
    //         grid.add(new Label("Mô tả:"), 0, 4);
    //         grid.add(txtDescription, 1, 4);

    //         dialog.getDialogPane().setContent(grid);

    //         ButtonType buttonTypeOk = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
    //         ButtonType buttonTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
    //         dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

    //         dialog.setResultConverter(dialogButton -> {
    //             if (dialogButton == buttonTypeOk) {
    //                 try {
    //                     // Validate input
    //                     if (cboEquipmentType.getValue() == null) {
    //                         throw new IllegalArgumentException("Vui lòng chọn loại thiết bị");
    //                     }
    //                     if (cboRoom.getValue() == null) {
    //                         throw new IllegalArgumentException("Vui lòng chọn phòng");
    //                     }
    //                     if (txtQuantity.getText().isEmpty()) {
    //                         throw new IllegalArgumentException("Vui lòng nhập số lượng");
    //                     }
    //                     if (cboStatus.getValue() == null) {
    //                         throw new IllegalArgumentException("Vui lòng chọn trạng thái");
    //                     }

    //                     equipment.setEquipmentTypeId(cboEquipmentType.getValue().getEquipmentTypeId());
    //                     equipment.setRoomId(cboRoom.getValue().getRoomId());
    //                     equipment.setQuantity(Integer.parseInt(txtQuantity.getText()));
    //                     equipment.setStatus(cboStatus.getValue());
    //                     equipment.setDescription(txtDescription.getText());
    //                     equipment.setUpdatedDate(LocalDateTime.now());
    //                     return equipment;
    //                 } catch (NumberFormatException e) {
    //                     Alert alert = new Alert(Alert.AlertType.ERROR);
    //                     alert.setTitle("Lỗi");
    //                     alert.setHeaderText("Số lượng không hợp lệ");
    //                     alert.setContentText("Vui lòng nhập số lượng là một số nguyên.");
    //                     alert.showAndWait();
    //                     return null;
    //                 } catch (IllegalArgumentException e) {
    //                     Alert alert = new Alert(Alert.AlertType.ERROR);
    //                     alert.setTitle("Lỗi");
    //                     alert.setHeaderText("Dữ liệu không hợp lệ");
    //                     alert.setContentText(e.getMessage());
    //                     alert.showAndWait();
    //                     return null;
    //                 }
    //             }
    //             return null;
    //         });

    //         Optional<RoomEquipment> result = dialog.showAndWait();
    //         result.ifPresent(updatedEquipment -> {
    //             try {
    //                 if (roomEquipmentController.updateRoomEquipment(updatedEquipment)) {
    //                     Alert alert = new Alert(Alert.AlertType.INFORMATION);
    //                     alert.setTitle("Thành công");
    //                     alert.setHeaderText("Cập nhật thiết bị thành công");
    //                     alert.setContentText("Thông tin thiết bị đã được cập nhật.");
    //                     alert.showAndWait();
    //                     loadData();
    //                 } else {
    //                     throw new Exception("Không thể cập nhật thiết bị");
    //                 }
    //             } catch (Exception e) {
    //                 Alert alert = new Alert(Alert.AlertType.ERROR);
    //                 alert.setTitle("Lỗi");
    //                 alert.setHeaderText("Không thể cập nhật thiết bị");
    //                 alert.setContentText(e.getMessage());
    //                 alert.showAndWait();
    //             }
    //         });
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // @FXML
    // private void handleDeleteEquipment(RoomEquipment equipment) {
    //     try {
    //         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    //         alert.setTitle("Xác nhận xóa");
    //         alert.setHeaderText("Xóa thiết bị");
    //         alert.setContentText("Bạn có chắc chắn muốn xóa thiết bị này?");

    //         Optional<ButtonType> result = alert.showAndWait();
    //         if (result.isPresent() && result.get() == ButtonType.OK) {
    //             if (roomEquipmentController.deleteRoomEquipment(equipment.getRoomEquipmentId())) {
    //                 Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
    //                 successAlert.setTitle("Thành công");
    //                 successAlert.setHeaderText("Xóa thiết bị thành công");
    //                 successAlert.setContentText("Thiết bị đã được xóa khỏi hệ thống.");
    //                 successAlert.showAndWait();
    //                 loadData();
    //             } else {
    //                 throw new Exception("Không thể xóa thiết bị");
    //             }
    //         }
    //     } catch (Exception e) {
    //         Alert alert = new Alert(Alert.AlertType.ERROR);
    //         alert.setTitle("Lỗi");
    //         alert.setHeaderText("Không thể xóa thiết bị");
    //         alert.setContentText(e.getMessage());
    //         alert.showAndWait();
    //     }
    // }

    // @FXML
    // private void handleSearch() {
    //     applyFilters();
    // }

    @FXML
    private void handleRefresh() {
        loadData();
        cboRoomFilter.setValue("Tất cả phòng");
        cboStatusFilter.setValue("Tất cả trạng thái");
        txtSearch.clear();
    }
}