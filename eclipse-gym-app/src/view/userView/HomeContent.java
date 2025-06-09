package view.userView;

import controller.MemberProgressController;
import controller.MemberProgressController.MembershipInfo;
import controller.MemberProgressController.TrainingSchedule;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import model.MemberProgress;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeContent {

    @FXML private Label welcomeLabel;
    @FXML private Label membershipStatus;
    @FXML private Label remainingSessions;
    @FXML private Label monthlySessions;
    @FXML private Label streakDays;
    @FXML private Label bmiValue;
    @FXML private Label bmiStatus;
    @FXML private Label bodyFatValue;
    @FXML private Label bodyFatStatus;
    @FXML private Label progressCard;
    @FXML private Label weightGoal;
    @FXML private Label streakAchievement;
    @FXML private Label totalSessions;
    
    @FXML private ComboBox<String> statTypeCombo;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis dateAxis;
    @FXML private NumberAxis valueAxis;
    @FXML private Pane radarChartPane;
    
    @FXML private TableView<TrainingSchedule> scheduleTable;
    @FXML private TableColumn<TrainingSchedule, LocalDate> dateColumn;
    @FXML private TableColumn<TrainingSchedule, LocalTime> timeColumn;
    @FXML private TableColumn<TrainingSchedule, String> trainerColumn;
    @FXML private TableColumn<TrainingSchedule, String> roomColumn;
    @FXML private TableColumn<TrainingSchedule, String> exercisesColumn;

    private User currentUser;
    private final MemberProgressController progressController = new MemberProgressController();
    private List<MemberProgress> history;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            loadUserData();
        }
    }

    private void loadUserData() {
        // Lấy memberId từ userId
        Integer memberId = progressController.getMemberIdByUserId(currentUser.getUserId());
        if (memberId != null) {
            // Load thông tin gói tập
            MembershipInfo membership = progressController.getCurrentMembershipInfo(memberId);
            if (membership != null) {
                updateMembershipInfo(membership);
            }

            // Load thống kê buổi tập
            updateSessionStats(memberId);

            // Load lịch sử số đo
            history = progressController.getProgressHistoryByMemberId(memberId);
            if (history != null && !history.isEmpty()) {
                updateBodyStats();
                updateLineChart();
                drawRadarChart();
            }

            // Load lịch tập sắp tới
            List<TrainingSchedule> schedules = progressController.getUpcomingSchedules(memberId);
            updateScheduleTable(schedules);
        }
    }

    private void updateMembershipInfo(MembershipInfo membership) {
        // Cập nhật thông tin gói tập
        welcomeLabel.setText("Chào mừng trở lại!");
        membershipStatus.setText(String.format("Gói tập: %s (Còn %d ngày)", 
            membership.getPlanName(),
            java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(), 
                membership.getEndDate()
            )
        ));
        remainingSessions.setText(String.valueOf(membership.getSessionsLeft()));
    }

    private void updateSessionStats(int memberId) {
        // Cập nhật số buổi tập trong tháng
        int monthlyCount = progressController.getMonthlySessions(memberId);
        monthlySessions.setText(String.valueOf(monthlyCount));

        // Cập nhật chuỗi ngày tập
        int streak = progressController.getStreakDays(memberId);
        streakDays.setText(String.valueOf(streak));
        streakAchievement.setText(streak + " ngày");

        // Cập nhật tổng số buổi tập
        int total = progressController.getTotalSessions(memberId);
        totalSessions.setText(total + " buổi");
    }

    @FXML
    public void initialize() {
        // Khởi tạo các loại chỉ số cho filter
        statTypeCombo.getItems().addAll("Cân nặng", "BMI", "Body Fat", "Vòng ngực", "Vòng eo", "Vòng mông", "Bắp tay", "Đùi");
        statTypeCombo.getSelectionModel().selectFirst();
        statTypeCombo.setOnAction(e -> updateLineChart());

        // Khởi tạo TableView
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("scheduleDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        trainerColumn.setCellValueFactory(new PropertyValueFactory<>("trainerName"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        exercisesColumn.setCellValueFactory(new PropertyValueFactory<>("exercises"));
    }

    private void updateScheduleTable(List<TrainingSchedule> schedules) {
        scheduleTable.getItems().clear();
        if (schedules != null) {
            scheduleTable.getItems().addAll(schedules);
        }
    }

    private void updateBodyStats() {
        if (history == null || history.isEmpty()) return;
        
        MemberProgress latest = history.get(0);
        
        // Cập nhật BMI
        double bmi = latest.getBmi();
        bmiValue.setText(String.format("%.1f", bmi));
        bmiStatus.setText(getBMIStatus(bmi));
        
        // Cập nhật Body Fat
        double bodyFat = latest.getBodyFatPercentage();
        bodyFatValue.setText(String.format("%.1f%%", bodyFat));
        bodyFatStatus.setText(getBodyFatStatus(bodyFat));
        
        // Cập nhật tiến độ
        if (history.size() > 1) {
            double diff = history.get(0).getWeight() - history.get(history.size() - 1).getWeight();
            if (diff < 0)
                progressCard.setText(String.format("Bạn đã tăng %.1f kg so với lần đầu 👏", -diff));
            else
                progressCard.setText(String.format("Bạn đã giảm %.1f kg so với lần đầu 👏", diff));
        }
    }

    private String getBMIStatus(double bmi) {
        if (bmi < 18.5) return "Thiếu cân";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Thừa cân";
        return "Béo phì";
    }

    private String getBodyFatStatus(double bodyFat) {
        if (bodyFat < 10) return "Rất thấp";
        if (bodyFat < 20) return "Tốt";
        if (bodyFat < 25) return "Bình thường";
        return "Cao";
    }

    private void updateLineChart() {
        if (history == null || history.isEmpty()) return;
        String type = statTypeCombo.getValue();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = history.size() - 1; i >= 0; i--) {
            MemberProgress p = history.get(i);
            String date = p.getMeasurementDate().format(fmt);
            double value = switch (type) {
                case "Cân nặng" -> p.getWeight();
                case "BMI" -> p.getBmi();
                case "Body Fat" -> p.getBodyFatPercentage();
                case "Vòng ngực" -> p.getChest();
                case "Vòng eo" -> p.getWaist();
                case "Vòng mông" -> p.getHip();
                case "Bắp tay" -> p.getBiceps();
                case "Đùi" -> p.getThigh();
                default -> 0;
            };
            series.getData().add(new XYChart.Data<>(date, value));
        }
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    private void drawRadarChart() {
        radarChartPane.getChildren().clear();
        if (history == null || history.isEmpty()) return;
        MemberProgress latest = history.get(0);

        String[] labels = {"Ngực", "Eo", "Mông", "Bắp tay", "Đùi"};
        double[] values = {
            latest.getChest(),
            latest.getWaist(),
            latest.getHip(),
            latest.getBiceps(),
            latest.getThigh()
        };

        double max = 0;
        for (double v : values) if (v > max) max = v;
        if (max == 0) max = 1;

        double centerX = radarChartPane.getPrefWidth() / 2;
        double centerY = radarChartPane.getPrefHeight() / 2;
        double radius = Math.min(centerX, centerY) - 40;
        int n = values.length;

        javafx.scene.shape.Polygon polygon = new javafx.scene.shape.Polygon();
        polygon.setStroke(javafx.scene.paint.Color.web("#1976d2"));
        polygon.setFill(javafx.scene.paint.Color.web("#90caf9", 0.5));
        polygon.setStrokeWidth(2);

        for (int i = 0; i < n; i++) {
            double angle = Math.toRadians(90 + i * 360.0 / n);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY - radius * Math.sin(angle);

            javafx.scene.shape.Line axis = new javafx.scene.shape.Line(centerX, centerY, x, y);
            axis.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
            radarChartPane.getChildren().add(axis);

            Label label = new Label(labels[i]);
            label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            label.setLayoutX(x - 18);
            label.setLayoutY(y - 18);
            radarChartPane.getChildren().add(label);
        }

        for (int i = 0; i < n; i++) {
            double angle = Math.toRadians(90 + i * 360.0 / n);
            double valueRadius = radius * values[i] / max;
            double x = centerX + valueRadius * Math.cos(angle);
            double y = centerY - valueRadius * Math.sin(angle);
            polygon.getPoints().addAll(x, y);

            Label valueLabel = new Label(String.format("%.1f", values[i]));
            valueLabel.setStyle("-fx-background-color: #fff; -fx-padding: 2 6 2 6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12px;");
            valueLabel.setLayoutX(x - 16);
            valueLabel.setLayoutY(y - 24);
            radarChartPane.getChildren().add(valueLabel);
        }
        radarChartPane.getChildren().add(polygon);
    }
}
