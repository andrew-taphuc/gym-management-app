package view.ownerView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import model.User;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.text.NumberFormat;

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

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        Connection conn = DBConnection.getConnection();

        // Số phòng tập
        roomCountLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Rooms")));

        // Số khách hàng 
        customerCountLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 'Hội viên'")));

        // Số nhân viên 
        staffCountLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 'Nhân viên quản lý'")));

        // Số HLV 
        trainerCountLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM Users WHERE Role = 'Huấn luyện viên'")));

        // Số gói tập
        planCountLabel.setText(String.valueOf(getCount(conn, "SELECT COUNT(*) FROM MembershipPlans")));

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
        for (int m = 1; m <= 12; m++) monthComboBox.getItems().add(m);
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
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Thống kê số người gia hạn trong tháng/năm
    private int getMonthRenewMemberCount(Connection conn, int month, int year) {
        String sql = "SELECT COUNT(*) FROM Memberships WHERE EXTRACT(MONTH FROM RenewalDate) = ? AND EXTRACT(YEAR FROM RenewalDate) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Doanh thu theo tháng/năm
    private int getMonthRevenue(Connection conn, int month, int year) {
        String sql = "SELECT COALESCE(SUM(Amount),0) FROM Payments WHERE EXTRACT(MONTH FROM PaymentDate) = ? AND EXTRACT(YEAR FROM PaymentDate) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
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
            String monthLabel = month.getMonth().getDisplayName(TextStyle.SHORT, new Locale("vi")) + " " + month.getYear();

            int ptCount = getPlanCountByMonth(conn, month.getYear(), month.getMonthValue(), true);
            int nonPtCount = getPlanCountByMonth(conn, month.getYear(), month.getMonthValue(), false);

            ptSeries.getData().add(new XYChart.Data<>(monthLabel, ptCount));
            nonPtSeries.getData().add(new XYChart.Data<>(monthLabel, nonPtCount));
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
            // Đếm gói có PT 
            sql = "SELECT COUNT(*) FROM TrainingRegistrations tr " +
                "WHERE EXTRACT(YEAR FROM tr.StartDate) = ? AND EXTRACT(MONTH FROM tr.StartDate) = ?";
        } else {
            // Đếm gói thường (không có trong TrainingRegistrations)
            sql = "SELECT COUNT(*) FROM Memberships m " +
                "WHERE EXTRACT(YEAR FROM m.StartDate) = ? " +
                "AND EXTRACT(MONTH FROM m.StartDate) = ? " +
                "AND m.MemberID NOT IN (SELECT tr.MemberID FROM TrainingRegistrations tr)";
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
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
                if (dob.plusYears(age).isAfter(today)) age--;
                if (age < 18) under18++;
                else if (age <= 45) from18to45++;
                else above45++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        agePieChart.getData().clear();
        PieChart.Data d1 = new PieChart.Data("Dưới 18", under18);
        PieChart.Data d2 = new PieChart.Data("18-45", from18to45);
        PieChart.Data d3 = new PieChart.Data("Trên 45", above45);
        agePieChart.getData().addAll(d1, d2, d3);

        // Đổi màu
        d1.getNode().setStyle("-fx-pie-color: #2196f3;"); 
        d2.getNode().setStyle("-fx-pie-color:rgb(231, 130, 130);"); 
        d3.getNode().setStyle("-fx-pie-color: #4caf50;"); 
    }
}