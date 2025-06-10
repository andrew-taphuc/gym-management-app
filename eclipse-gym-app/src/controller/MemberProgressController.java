package controller;

import model.MemberProgress;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MemberProgressController {
    
    // Lấy toàn bộ lịch sử số đo cơ thể của một hội viên theo MemberID, sắp xếp mới nhất trước
    public List<MemberProgress> getProgressHistoryByMemberId(int memberId) {
        List<MemberProgress> progressList = new ArrayList<>();
        String sql = """
            SELECT ProgressID, MemberID, MeasurementDate, Weight, Height, BMI, BodyFatPercentage,
                   Chest, Waist, Hip, Biceps, Thigh, TrainerID, Notes
            FROM MemberProgress
            WHERE MemberID = ?
            ORDER BY MeasurementDate DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberProgress progress = new MemberProgress(
                    rs.getInt("ProgressID"),
                    rs.getInt("MemberID"),
                    rs.getDate("MeasurementDate").toLocalDate(),
                    rs.getDouble("Weight"),
                    rs.getDouble("Height"),
                    rs.getDouble("BMI"),
                    rs.getDouble("BodyFatPercentage"),
                    rs.getDouble("Chest"),
                    rs.getDouble("Waist"),
                    rs.getDouble("Hip"),
                    rs.getDouble("Biceps"),
                    rs.getDouble("Thigh"),
                    rs.getInt("TrainerID"),
                    rs.getString("Notes")
                );
                progressList.add(progress);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử số đo cơ thể: " + e.getMessage());
            e.printStackTrace();
        }
        return progressList;
    }

    // Lấy ID của member từ UserID
    public Integer getMemberIdByUserId(int userId) {
        String sql = "SELECT MemberID FROM Members WHERE UserID = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("MemberID");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn MemberID từ UserID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Lấy thông tin gói tập hiện tại
    public MembershipInfo getCurrentMembershipInfo(int memberId) {
        String sql = """
            SELECT m.MembershipID, m.MemberID, m.PlanID, m.StartDate, m.EndDate, m.Status, m.PaymentID,
                   mp.PlanName, mp.Duration, mp.Price,
                   COALESCE(tr.SessionsLeft, 0) as SessionsLeft
            FROM Memberships m
            JOIN MembershipPlans mp ON m.PlanID = mp.PlanID
            LEFT JOIN (
                SELECT MemberID, SessionsLeft
                FROM TrainingRegistrations
                WHERE MemberID = ?
                ORDER BY StartDate DESC
                LIMIT 1
            ) tr ON tr.MemberID = m.MemberID
            WHERE m.MemberID = ? AND m.Status = 'Hoạt động'
            ORDER BY m.EndDate DESC
            LIMIT 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new MembershipInfo(
                    rs.getInt("MembershipID"),
                    rs.getInt("MemberID"),
                    rs.getInt("PlanID"),
                    rs.getDate("StartDate").toLocalDate(),
                    rs.getDate("EndDate").toLocalDate(),
                    rs.getString("Status"),
                    rs.getInt("PaymentID"),
                    rs.getString("PlanName"),
                    rs.getInt("Duration"),
                    rs.getDouble("Price"),
                    rs.getInt("SessionsLeft")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin gói tập: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Lấy số buổi tập trong tháng hiện tại
    public int getMonthlySessions(int memberId) {
        String sql = """
            SELECT COUNT(*) as session_count
            FROM Attendance a
            WHERE a.MemberID = ? 
            AND EXTRACT(MONTH FROM a.CheckInTime) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR FROM a.CheckInTime) = EXTRACT(YEAR FROM CURRENT_DATE)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("session_count");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm số buổi tập trong tháng: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy chuỗi ngày tập liên tục
    public int getStreakDays(int memberId) {
        String sql = """
            WITH RECURSIVE dates AS (
                SELECT CURRENT_DATE::date as check_date
                UNION ALL
                SELECT (check_date - INTERVAL '1 day')::date
                FROM dates
                WHERE check_date > (CURRENT_DATE - INTERVAL '30 days')::date
            ),
            check_ins AS (
                SELECT DISTINCT DATE(CheckInTime)::date as check_date
                FROM Attendance
                WHERE MemberID = ?
                AND CheckInTime >= (CURRENT_DATE - INTERVAL '30 days')::date
            )
            SELECT COUNT(*)
            FROM dates d
            WHERE EXISTS (
                SELECT 1 
                FROM check_ins c 
                WHERE c.check_date = d.check_date
            )
            AND NOT EXISTS (
                SELECT 1 
                FROM dates d2 
                WHERE d2.check_date > d.check_date 
                AND NOT EXISTS (
                    SELECT 1 
                    FROM check_ins c2 
                    WHERE c2.check_date = d2.check_date
                )
            );
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính chuỗi ngày tập: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy lịch tập sắp tới
    public List<TrainingSchedule> getUpcomingSchedules(int memberId) {
        List<TrainingSchedule> schedules = new ArrayList<>();
        String sql = """
            SELECT ts.ScheduleID, ts.RegistrationID, ts.MemberID, ts.TrainerID, ts.MembershipID,
                   ts.ScheduleDate, ts.StartTime, ts.Duration, ts.RoomID, ts.Status, ts.Notes,
                   r.RoomName, u.FullName as TrainerName,
                   STRING_AGG(e.ExerciseName, ', ') as Exercises
            FROM TrainingSchedule ts
            LEFT JOIN Rooms r ON ts.RoomID = r.RoomID
            LEFT JOIN Trainers t ON ts.TrainerID = t.TrainerID
            LEFT JOIN Users u ON t.UserID = u.UserID
            LEFT JOIN TrainingScheduleExercises tse ON ts.ScheduleID = tse.ScheduleID
            LEFT JOIN Exercises e ON tse.ExerciseID = e.ExerciseID
            WHERE ts.MemberID = ? 
            AND ts.ScheduleDate >= CURRENT_DATE
            AND ts.Status = 'Đã lên lịch'
            GROUP BY ts.ScheduleID, ts.RegistrationID, ts.MemberID, ts.TrainerID, ts.MembershipID,
                     ts.ScheduleDate, ts.StartTime, ts.Duration, ts.RoomID, ts.Status, ts.Notes,
                     r.RoomName, u.FullName
            ORDER BY ts.ScheduleDate, ts.StartTime
            LIMIT 5
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                schedules.add(new TrainingSchedule(
                    rs.getInt("ScheduleID"),
                    rs.getInt("RegistrationID"),
                    rs.getInt("MemberID"),
                    rs.getInt("TrainerID"),
                    rs.getInt("MembershipID"),
                    rs.getDate("ScheduleDate").toLocalDate(),
                    rs.getTime("StartTime").toLocalTime(),
                    rs.getInt("Duration"),
                    rs.getInt("RoomID"),
                    rs.getString("Status"),
                    rs.getString("Notes"),
                    rs.getString("RoomName"),
                    rs.getString("TrainerName"),
                    rs.getString("Exercises")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch tập sắp tới: " + e.getMessage());
            e.printStackTrace();
        }
        return schedules;
    }

    // Lấy tổng số buổi tập
    public int getTotalSessions(int memberId) {
        String sql = "SELECT COUNT(*) as total FROM Attendance WHERE MemberID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm tổng số buổi tập: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Inner classes để lưu trữ dữ liệu
    public static class MembershipInfo {
        private final int membershipId;
        private final int memberId;
        private final int planId;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String status;
        private final int paymentId;
        private final String planName;
        private final int duration;
        private final double price;
        private final int sessionsLeft;

        public MembershipInfo(int membershipId, int memberId, int planId, LocalDate startDate, 
                            LocalDate endDate, String status, int paymentId, String planName, 
                            int duration, double price, int sessionsLeft) {
            this.membershipId = membershipId;
            this.memberId = memberId;
            this.planId = planId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.paymentId = paymentId;
            this.planName = planName;
            this.duration = duration;
            this.price = price;
            this.sessionsLeft = sessionsLeft;
        }

        // Getters
        public int getMembershipId() { return membershipId; }
        public int getMemberId() { return memberId; }
        public int getPlanId() { return planId; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getStatus() { return status; }
        public int getPaymentId() { return paymentId; }
        public String getPlanName() { return planName; }
        public int getDuration() { return duration; }
        public double getPrice() { return price; }
        public int getSessionsLeft() { return sessionsLeft; }
    }

    public static class TrainingSchedule {
        private final int scheduleId;
        private final int registrationId;
        private final int memberId;
        private final int trainerId;
        private final int membershipId;
        private final LocalDate scheduleDate;
        private final LocalTime startTime;
        private final int duration;
        private final int roomId;
        private final String status;
        private final String notes;
        private final String roomName;
        private final String trainerName;
        private final String exercises;

        public TrainingSchedule(int scheduleId, int registrationId, int memberId, int trainerId,
                              int membershipId, LocalDate scheduleDate, LocalTime startTime,
                              int duration, int roomId, String status, String notes,
                              String roomName, String trainerName, String exercises) {
            this.scheduleId = scheduleId;
            this.registrationId = registrationId;
            this.memberId = memberId;
            this.trainerId = trainerId;
            this.membershipId = membershipId;
            this.scheduleDate = scheduleDate;
            this.startTime = startTime;
            this.duration = duration;
            this.roomId = roomId;
            this.status = status;
            this.notes = notes;
            this.roomName = roomName;
            this.trainerName = trainerName;
            this.exercises = exercises;
        }

        // Getters
        public int getScheduleId() { return scheduleId; }
        public int getRegistrationId() { return registrationId; }
        public int getMemberId() { return memberId; }
        public int getTrainerId() { return trainerId; }
        public int getMembershipId() { return membershipId; }
        public LocalDate getScheduleDate() { return scheduleDate; }
        public LocalTime getStartTime() { return startTime; }
        public int getDuration() { return duration; }
        public int getRoomId() { return roomId; }
        public String getStatus() { return status; }
        public String getNotes() { return notes; }
        public String getRoomName() { return roomName; }
        public String getTrainerName() { return trainerName; }
        public String getExercises() { return exercises; }
    }
}