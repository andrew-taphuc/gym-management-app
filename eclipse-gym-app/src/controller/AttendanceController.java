package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Attendance;
import model.Membership;
import model.TrainingRegistration;
import model.TrainingSchedule;
import utils.DBConnection;

public class AttendanceController {
    
    // Kiểm tra xem hội viên đã check-in trong vòng 1 tiếng chưa
    public static boolean hasCheckedInWithinLastHour(int memberId) {
        String query = "SELECT COUNT(*) FROM Attendance WHERE MemberID = ? AND CheckinTime >= NOW() - INTERVAL '1 hour'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check-in phòng tập (GYM)
    public static boolean checkinGym(int memberId, int membershipId) {
        String query = "INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, TrainingScheduleID) VALUES (?, ?, NOW(), NULL)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, membershipId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check-in dịch vụ PT
    public static boolean checkinPT(int memberId, int membershipId, int registrationId, int trainingScheduleId) {
        String query = "INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, TrainingScheduleID) VALUES (?, ?, NOW(), ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, membershipId);
            stmt.setInt(3, trainingScheduleId);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                // Trừ số buổi còn lại của gói PT
                String updateSql = "UPDATE TrainingRegistrations SET SessionsLeft = SessionsLeft - 1 WHERE RegistrationID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, registrationId);
                    updateStmt.executeUpdate();
                }
                
                // Cập nhật trạng thái lịch tập thành 'Hoàn thành'
                String updateStatusSql = "UPDATE TrainingSchedule SET Status = 'Hoàn thành' WHERE ScheduleID = ?";
                try (PreparedStatement statusStmt = conn.prepareStatement(updateStatusSql)) {
                    statusStmt.setInt(1, trainingScheduleId);
                    statusStmt.executeUpdate();
                }
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách check-in của hội viên
    public static List<Attendance> getMemberAttendance(int memberId) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT a.*, m.PlanID, ts.TrainerID " +
                      "FROM Attendance a " +
                      "LEFT JOIN Memberships m ON a.MembershipID = m.MembershipID " +
                      "LEFT JOIN TrainingSchedule ts ON a.TrainingScheduleID = ts.ScheduleID " +
                      "WHERE a.MemberID = ? " +
                      "ORDER BY a.CheckInTime DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("AttendanceID"));
                attendance.setMemberId(rs.getInt("MemberID"));
                attendance.setMembershipId(rs.getInt("MembershipID"));
                attendance.setCheckInTime(rs.getTimestamp("CheckInTime").toLocalDateTime());
                attendance.setTrainingScheduleId(rs.getInt("TrainingScheduleID"));
                list.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm số buổi đã tập trong tháng này
    public static int countAttendanceThisMonth(int memberId, LocalDate now) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM Attendance WHERE MemberID = ? AND EXTRACT(MONTH FROM CheckinTime) = ? AND EXTRACT(YEAR FROM CheckinTime) = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, now.getMonthValue());
            stmt.setInt(3, now.getYear());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Lấy 5 lần check-in gần nhất
    public static List<Attendance> getRecentAttendance(int memberId, int limit) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT a.*, mp.PlanName, " +
                "CASE WHEN a.TrainingScheduleID IS NOT NULL " +
                "THEN u_trainer.FullName ELSE NULL END AS TrainerName " +
                "FROM Attendance a " +
                "JOIN Memberships m ON a.MembershipID = m.MembershipID " +
                "JOIN MembershipPlans mp ON m.PlanID = mp.PlanID " +
                "LEFT JOIN TrainingSchedule ts ON a.TrainingScheduleID = ts.ScheduleID " +
                "LEFT JOIN Trainers t ON ts.TrainerID = t.TrainerID " +
                "LEFT JOIN Users u_trainer ON t.UserID = u_trainer.UserID " +
                "WHERE a.MemberID = ? ORDER BY a.CheckinTime DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setAttendanceId(rs.getInt("AttendanceID"));
                a.setMemberId(rs.getInt("MemberID"));
                a.setCheckInTime(rs.getTimestamp("CheckinTime").toLocalDateTime());
                a.setMembershipId(rs.getInt("MembershipID"));
                a.setPlanName(rs.getString("PlanName")); // Thêm trường này vào model Attendance
                a.setTrainingScheduleId(
                        rs.getObject("TrainingScheduleID") != null ? rs.getInt("TrainingScheduleID") : null);
                a.setTrainerName(rs.getString("TrainerName")); // Thêm tên trainer
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Số dòng trả về: " + list.size());
        return list;
    }

    // Lấy số buổi tập trong tháng hiện tại
    public static int getMonthlySessions(int memberId) {
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
    public static int getStreakDays(int memberId) {
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

    // Lấy tổng số buổi tập đã hoàn thành
    public static int getTotalSessions(int memberId) {
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
} 