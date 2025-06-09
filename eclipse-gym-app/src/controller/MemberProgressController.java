package controller;

import model.MemberProgress;
import utils.DBConnection;

import java.sql.*;
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

    // Có thể bổ sung thêm các phương thức thêm/sửa/xóa nếu cần cho admin hoặc trainer
}