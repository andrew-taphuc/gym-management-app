package controller;

import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttendanceController {
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
}

