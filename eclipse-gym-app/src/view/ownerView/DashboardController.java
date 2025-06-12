package view.ownerView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import model.User;
import model.Trainer;
import controller.TrainerController;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class DashboardController {
    @FXML
    private Label roomCountLabel;
    @FXML
    private Label customerCountLabel;
    @FXML
    private Label staffCountLabel;
    @FXML
    private Label trainerCountLabel;
    @FXML
    private Label planCountLabel;
    @FXML
    private BarChart<String, Number> ptBarChart;
    @FXML
    private javafx.scene.chart.PieChart agePieChart;
    @FXML
    private HBox trainersContainer;

    // Thêm các biến cho thống kê chung
    @FXML
    private Label todayTitleLabel;
    @FXML
    private Label todayRevenueLabel;
    @FXML
    private Label todayNewMemberLabel;
    @FXML
    private Label todayRenewMemberLabel;
    @FXML
    private ComboBox<Integer> monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private Label monthNewMemberLabel;
    @FXML
    private Label monthRenewMemberLabel;
    @FXML
    private Label monthRevenueLabel;

    private User currentUser;
    private TrainerController trainerController;

    public DashboardController() {
        this.trainerController = new TrainerController();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        Connection conn = DBConnection.getConnection();

        // Số phòng tập
        roomCountLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Rooms")));

        // Số khách hàng
        customerCountLabel
                .setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 'Hội viên'")));

        // Số nhân viên
        staffCountLabel
                .setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 'Nhân viên quản lý'")));

        // Số HLV
        trainerCountLabel
                .setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 'Huấn luyện viên'")));

        // Số gói tập
        planCountLabel.setText(
                String.valueOf(getCount(conn, "SELECT COUNT(*) FROM MembershipPlans Where status = 'Hoạt động'")));

        // Thống kê trong ngày hôm nay
        LocalDate today = LocalDate.now();
        todayTitleLabel.setText("Thống kê ngày " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        todayRevenueLabel.setText("Doanh thu: " + formatCurrency(getTodayRevenue(conn)));
        todayNewMemberLabel.setText("Đăng ký mới: " + getTodayNewMemberCount(conn));
        todayRenewMemberLabel.setText("Gia hạn thêm: " + getTodayRenewMemberCount(conn));

        // Thống kê trong tháng/năm hiện tại
        monthComboBox.getItems().clear();
        yearComboBox.getItems().clear();
        LocalDate now = LocalDate.now();
        for (int m = 1; m <= 12; m++)
            monthComboBox.getItems().add(m);
        int currentYear = now.getYear();
        for (int y = currentYear; y >= currentYear - 3; y--) {
            yearComboBox.getItems().add(y);
        }

        monthComboBox.setValue(now.getMonthValue());
        yearComboBox.setValue(currentYear);

        // Lắng nghe thay đổi để cập nhật thống kê
        monthComboBox.setOnAction(e -> updateMonthStats());
        yearComboBox.setOnAction(e -> updateMonthStats());

        updateMonthStats();

        // Thiết lập trục y cho biểu đồ cột
        setupBarChartYAxis();
        // Vẽ biểu đồ cột PT vs không PT 6 tháng gần nhất
        drawPTBarChart();
        // Vẽ biểu đồ pie phân bố độ tuổi
        drawAgePieChart();
        // Load trainer cards
        loadTrainerCards();
    }

    private void loadTrainerCards() {
        try {
            List<Trainer> trainers = trainerController.getAllTrainersWithRating();
            trainersContainer.getChildren().clear();

            for (Trainer trainer : trainers) {
                VBox trainerCard = createTrainerCard(trainer);
                trainersContainer.getChildren().add(trainerCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createTrainerCard(Trainer trainer) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPrefWidth(160);
        card.setPrefHeight(200);
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1;");

        // Avatar image
        ImageView avatar = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/view/resources/pt.png"));
            avatar.setImage(image);
        } catch (Exception e) {
            // If image not found, create a simple colored circle placeholder
            System.out.println("Could not load pt.png");
        }
        avatar.setFitWidth(70);
        avatar.setFitHeight(70);
        avatar.setPreserveRatio(true);

        // Name
        Label nameLabel = new Label(trainer.getUser().getFullName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(140);

        // Specialization
        String specialization = trainer.getSpecialization() != null ? trainer.getSpecialization().getValue()
                : "Chưa xác định";
        Label specializationLabel = new Label(specialization);
        specializationLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        specializationLabel.setAlignment(Pos.CENTER);

        // Rating with stars
        HBox ratingBox = new HBox();
        ratingBox.setAlignment(Pos.CENTER);
        ratingBox.setSpacing(5);

        // Format rating to 1 decimal place
        DecimalFormat df = new DecimalFormat("#.#");
        double rating = trainer.getRating();

        // If no rating exists, show "Chưa có đánh giá"
        if (rating == 0) {
            Label noRatingLabel = new Label("Chưa có đánh giá");
            noRatingLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
            ratingBox.getChildren().add(noRatingLabel);
        } else {
            Label ratingLabel = new Label(df.format(rating) + "/5");
            ratingLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

            // Star icon (using Unicode star)
            Label starLabel = new Label("★");
            starLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #f39c12;");

            ratingBox.getChildren().addAll(ratingLabel, starLabel);
        }

        card.getChildren().addAll(avatar, nameLabel, specializationLabel, ratingBox);

        // Add click event to show trainer statistics
        card.setOnMouseClicked(e -> {
            showTrainerStatistics(trainer);
        });

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3); " +
                    "-fx-border-color: #3498db; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 2; " +
                    "-fx-scale-x: 1.02; " +
                    "-fx-scale-y: 1.02; " +
                    "-fx-cursor: hand;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 1; " +
                    "-fx-scale-x: 1.0; " +
                    "-fx-scale-y: 1.0;");
        });

        return card;
    }

    private int getCount(Connection conn, String sql) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Thống kê thành viên mới trong ngày hôm nay
    private int getTodayNewMemberCount(Connection conn) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Role = 'Hội viên' AND DATE(CreatedAt) = CURRENT_DATE";
        return getCount(conn, sql);
    }

    // Thống kê doanh thu trong ngày hôm nay
    private int getTodayRevenue(Connection conn) {
        String sql = "SELECT COALESCE(SUM(Amount),0) FROM Payments WHERE DATE(PaymentDate) = CURRENT_DATE";
        return getCount(conn, sql);
    }

    // Thống kê thành viên mới trong tháng hiện tại
    private int getMonthNewMemberCount(Connection conn, int month, int year) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Role = 'Hội viên' AND EXTRACT(MONTH FROM CreatedAt) = ? AND EXTRACT(YEAR FROM CreatedAt) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Thống kê số người gia hạn trong tháng/năm
    private int getMonthRenewMemberCount(Connection conn, int month, int year) {
        String sql = "SELECT COUNT(*) FROM Memberships WHERE EXTRACT(MONTH FROM RenewalDate) = ? AND EXTRACT(YEAR FROM RenewalDate) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Doanh thu theo tháng/năm
    private int getMonthRevenue(Connection conn, int month, int year) {
        String sql = "SELECT COALESCE(SUM(Amount),0) FROM Payments WHERE EXTRACT(MONTH FROM PaymentDate) = ? AND EXTRACT(YEAR FROM PaymentDate) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void drawPTBarChart() {
        Connection conn = DBConnection.getConnection();
        XYChart.Series<String, Number> ptSeries = new XYChart.Series<>();
        ptSeries.setName("Gói có HLV");
        XYChart.Series<String, Number> nonPtSeries = new XYChart.Series<>();
        nonPtSeries.setName("Gói thường");

        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthLabel = month.getMonth().getDisplayName(TextStyle.SHORT, new Locale("vi")) + " "
                    + month.getYear();

            int ptCount = getPlanCountByMonth(conn, month.getYear(), month.getMonthValue(), true);
            int nonPtCount = getPlanCountByMonth(conn, month.getYear(), month.getMonthValue(), false);

            ptSeries.getData().add(new XYChart.Data<>(monthLabel, ptCount));
            nonPtSeries.getData().add(new XYChart.Data<>(monthLabel, nonPtCount));
            System.out.println(monthLabel + ": PT=" + ptCount + ", NonPT=" + nonPtCount);
        }

        ptBarChart.getData().clear();
        @SuppressWarnings("unchecked")
        final XYChart.Series<String, Number>[] seriesArray = new XYChart.Series[] { ptSeries, nonPtSeries };
        ptBarChart.getData().addAll(seriesArray);
    }

    private void setupBarChartYAxis() {
        // Đảm bảo trục y chỉ hiển thị số nguyên
        NumberAxis yAxis = (NumberAxis) ptBarChart.getYAxis();
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(true);
        yAxis.setAutoRanging(true);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });
    }

    private int getPlanCountByMonth(Connection conn, int year, int month, boolean isPT) {
        String sql;

        if (isPT) {
            sql = "SELECT COUNT(*) FROM TrainingRegistrations tr " +
                    "WHERE EXTRACT(YEAR FROM tr.StartDate) = ? AND EXTRACT(MONTH FROM tr.StartDate) = ?";
        } else {
            // Sửa lại để tính chính xác gói thường trong tháng đó
            sql = "SELECT COUNT(*) FROM Memberships m " +
                    "WHERE EXTRACT(YEAR FROM m.StartDate) = ? " +
                    "AND EXTRACT(MONTH FROM m.StartDate) = ? " +
                    "AND m.MemberID NOT IN (" +
                    "SELECT tr.MemberID FROM TrainingRegistrations tr " +
                    "WHERE EXTRACT(YEAR FROM tr.StartDate) = ? " +
                    "AND EXTRACT(MONTH FROM tr.StartDate) = ?" +
                    ")";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            if (!isPT) {
                stmt.setInt(3, year); // Thêm tham số cho subquery
                stmt.setInt(4, month);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Thống kê số lượng thành viên gia hạn trong ngày hôm nay
    private int getTodayRenewMemberCount(Connection conn) {
        String sql = "SELECT COUNT(*) FROM Memberships WHERE DATE(RenewalDate) = CURRENT_DATE";
        return getCount(conn, sql);
    }

    private void updateMonthStats() {
        int month = monthComboBox.getValue();
        int year = yearComboBox.getValue();
        Connection conn = DBConnection.getConnection();

        monthNewMemberLabel.setText("Thành viên mới: " + getMonthNewMemberCount(conn, month, year));
        monthRenewMemberLabel.setText("Gia hạn thêm: " + getMonthRenewMemberCount(conn, month, year));
        monthRevenueLabel.setText("Doanh thu: " + formatCurrency(getMonthRevenue(conn, month, year)));
    }

    private String formatCurrency(int amount) {
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vnFormat.format(amount);
    }

    private void drawAgePieChart() {
        Connection conn = DBConnection.getConnection();
        int under18 = 0, from18to45 = 0, above45 = 0;

        String sql = "SELECT DateOfBirth FROM Users WHERE Role = 'Hội viên'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            LocalDate today = LocalDate.now();
            while (rs.next()) {
                LocalDate dob = rs.getDate(1).toLocalDate();
                int age = today.getYear() - dob.getYear();
                if (dob.plusYears(age).isAfter(today))
                    age--; // Điều chỉnh nếu chưa đến sinh nhật
                if (age < 18)
                    under18++;
                else if (age <= 45)
                    from18to45++;
                else
                    above45++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        agePieChart.getData().clear();
        agePieChart.getData().add(new PieChart.Data("Dưới 18 (" + under18 + ")", under18));
        agePieChart.getData().add(new PieChart.Data("18-45 (" + from18to45 + ")", from18to45));
        agePieChart.getData().add(new PieChart.Data("Trên 45 (" + above45 + ")", above45));
    }

    private void showTrainerStatistics(Trainer trainer) {
        // Lấy thống kê từ database
        Connection conn = DBConnection.getConnection();

        // Số hội viên đang quản lý (hội viên có training registration active với hlv
        // này)
        int managingMembers = getTrainerManagedMembers(conn, trainer.getTrainerId());

        // Số buổi đã dạy tháng này
        int sessionsThisMonth = getTrainerSessionsThisMonth(conn, trainer.getTrainerId());

        // Số buổi đã dạy tổng cộng
        int totalSessions = getTrainerTotalSessions(conn, trainer.getTrainerId());

        // Tạo popup window
        Stage popupStage = new Stage();
        popupStage.setTitle("Thống kê huấn luyện viên - " + trainer.getUser().getFullName());
        popupStage.setResizable(false);

        // Tạo layout
        VBox mainLayout = new VBox();
        mainLayout.setSpacing(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: #f8f9fa;");

        // Header với thông tin cơ bản
        VBox headerSection = new VBox();
        headerSection.setSpacing(10);
        headerSection.setAlignment(Pos.CENTER);

        // Avatar
        ImageView avatar = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/view/resources/pt.png"));
            avatar.setImage(image);
        } catch (Exception e) {
            System.out.println("Could not load pt.png");
        }
        avatar.setFitWidth(80);
        avatar.setFitHeight(80);
        avatar.setPreserveRatio(true);

        // Tên và thông tin cơ bản
        Label nameLabel = new Label(trainer.getUser().getFullName());
        nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        String specialization = trainer.getSpecialization() != null ? trainer.getSpecialization().getValue()
                : "Chưa xác định";
        Label specializationLabel = new Label("Chuyên môn: " + specialization);
        specializationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Rating
        DecimalFormat df = new DecimalFormat("#.#");
        String ratingText = trainer.getRating() == 0 ? "Chưa có đánh giá" : df.format(trainer.getRating()) + "/5 ★";
        Label ratingLabel = new Label("Đánh giá: " + ratingText);
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12; -fx-font-weight: bold;");

        headerSection.getChildren().addAll(avatar, nameLabel, specializationLabel, ratingLabel);

        // Separator
        Label separator = new Label("─────────────────────────────");
        separator.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 16px;");
        separator.setAlignment(Pos.CENTER);

        // Statistics section
        VBox statsSection = new VBox();
        statsSection.setSpacing(15);

        // Thống kê cards
        HBox statsCards = new HBox();
        statsCards.setSpacing(20);
        statsCards.setAlignment(Pos.CENTER);

        // Card số hội viên đang quản lý
        VBox membersCard = createStatCard("Hội viên quản lý", String.valueOf(managingMembers), "#3498db");

        // Card số buổi tháng này
        VBox sessionsCard = createStatCard("Buổi dạy tháng này", String.valueOf(sessionsThisMonth), "#e74c3c");

        // Card tổng số buổi
        VBox totalSessionsCard = createStatCard("Tổng buổi đã dạy", String.valueOf(totalSessions), "#27ae60");

        statsCards.getChildren().addAll(membersCard, sessionsCard, totalSessionsCard);

        // Thông tin chi tiết
        Label detailTitle = new Label("Thông tin chi tiết:");
        detailTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox detailsBox = new VBox();
        detailsBox.setSpacing(8);
        detailsBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; " +
                "-fx-border-color: #ecf0f1; -fx-border-radius: 8; -fx-border-width: 1;");

        Label phoneLabel = new Label("Số điện thoại: " +
                (trainer.getUser().getPhoneNumber() != null ? trainer.getUser().getPhoneNumber() : "Chưa có"));
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        Label emailLabel = new Label("Email: " +
                (trainer.getUser().getEmail() != null ? trainer.getUser().getEmail() : "Chưa có"));
        emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        Label statusLabel = new Label("Trạng thái: " + trainer.getStatus().getValue());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");

        detailsBox.getChildren().addAll(phoneLabel, emailLabel, statusLabel);

        statsSection.getChildren().addAll(statsCards, detailTitle, detailsBox);

        // Close button
        javafx.scene.control.Button closeButton = new javafx.scene.control.Button("Đóng");
        closeButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        closeButton.setOnAction(e -> popupStage.close());

        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);

        // Combine all sections
        mainLayout.getChildren().addAll(headerSection, separator, statsSection, buttonBox);

        // Create scene and show
        Scene scene = new Scene(mainLayout, 500, 600);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPrefWidth(140);
        card.setPrefHeight(80);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8; -fx-padding: 15;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: white;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private int getTrainerManagedMembers(Connection conn, int trainerId) {
        String sql = "SELECT COUNT(DISTINCT tr.MemberID) FROM TrainingRegistrations tr " +
                "WHERE tr.TrainerID = ? AND tr.SessionsLeft > 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTrainerSessionsThisMonth(Connection conn, int trainerId) {
        String sql = "SELECT COUNT(*) FROM TrainingSchedule ts " +
                "WHERE ts.TrainerID = ? " +
                "AND EXTRACT(MONTH FROM ts.ScheduleDate) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                "AND EXTRACT(YEAR FROM ts.ScheduleDate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                "AND ts.Status = 'Hoàn thành'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getTrainerTotalSessions(Connection conn, int trainerId) {
        String sql = "SELECT COUNT(*) FROM TrainingSchedule ts " +
                "WHERE ts.TrainerID = ? AND ts.Status = 'Hoàn thành'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}