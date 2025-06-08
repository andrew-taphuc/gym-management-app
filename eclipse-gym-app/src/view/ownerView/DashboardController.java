package view.ownerView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import model.User;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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

        // Thiết lập trục y cho biểu đồ cột
        setupBarChartYAxis();
        // Vẽ biểu đồ cột PT vs không PT 6 tháng gần nhất
        drawPTBarChart();
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
}