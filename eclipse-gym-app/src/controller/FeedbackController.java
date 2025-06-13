package controller;

import model.Feedback;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackController {
    // Thêm feedback mới vào DB
    public boolean insertFeedback(int memberID, String feedbackType, String comment, Integer equipmentId) {
        String sql = "INSERT INTO Feedback (MemberID, FeedbackType, Comment, EquipmentID) VALUES (?, ?::feedback_type, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberID);
            pstmt.setString(2, feedbackType);
            pstmt.setString(3, comment);
            if (equipmentId != null) {
                pstmt.setInt(4, equipmentId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Thêm feedback mới vào DB (overload method cũ để tương thích)
    public boolean insertFeedback(int memberID, String feedbackType, String comment) {
        return insertFeedback(memberID, feedbackType, comment, null);
    }

    // Lấy danh sách feedback của một member
    public List<Feedback> getFeedbacksByMemberID(int memberID) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
                    SELECT FeedbackType, Comment, Status, ResponseComment,
                           TO_CHAR(FeedbackDate, 'YYYY-MM-DD') as FeedbackDate,
                           EquipmentID
                    FROM Feedback
                    WHERE MemberID = ?
                    ORDER BY FeedbackDate DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("FeedbackType");
                String comment = rs.getString("Comment");
                String status = rs.getString("Status");
                String date = rs.getString("FeedbackDate");
                String responseComment = rs.getString("ResponseComment");
                int equipmentId = rs.getInt("EquipmentID");

                feedbacks.add(new Feedback(type, comment, status, date, responseComment, equipmentId));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách feedback: " + e.getMessage());
            e.printStackTrace();
        }

        return feedbacks;
    }

    // Lấy tất cả feedback (cho admin)
    public List<Feedback> getAllFeedbacks() {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
                    SELECT
                        f.FeedbackID,
                        u.FullName AS MemberName,
                        u.Username,
                        f.FeedbackType,
                        f.Comment,
                        f.FeedbackDate,
                        f.Status,
                        f.ResponseComment,
                        f.ResponseDate,
                        f.EquipmentID
                    FROM Feedback f
                    JOIN Members m ON f.MemberID = m.MemberID
                    JOIN Users u ON m.UserID = u.UserID
                    ORDER BY f.FeedbackDate DESC;
                """;

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("FeedbackType");
                String comment = rs.getString("Comment");
                String status = rs.getString("Status");
                String date = rs.getString("FeedbackDate");
                String username = rs.getString("Username");
                String memberName = rs.getString("MemberName");
                String responseComment = rs.getString("ResponseComment");
                String responseDate = rs.getString("ResponseDate");
                int feedbackID = rs.getInt("FeedbackID");
                int equipmentId = rs.getInt("EquipmentID");

                // Có thể tạo FeedbackWithUser class hoặc dùng comment để hiển thị user
                feedbacks.add(new Feedback(feedbackID, memberName, type, comment, status, date, responseComment,
                        responseDate, equipmentId));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả feedback: " + e.getMessage());
            e.printStackTrace();
        }

        return feedbacks;
    }

    // Cập nhật status feedback (cho admin)
    public boolean updateFeedbackStatus(int feedbackID, String status, String responseComment, int responderID) {
        String sql = """
                    UPDATE Feedback
                    SET Status = ?::feedback_status, ResponseComment = ?,
                        ResponseDate = CURRENT_TIMESTAMP, ResponderID = ?
                    WHERE FeedbackID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, responseComment);
            pstmt.setInt(3, responderID);
            pstmt.setInt(4, feedbackID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean replyFeedback(int feedbackID, String responseComment, int responderID) {
        String status = "Đã giải quyết";
        return updateFeedbackStatus(feedbackID, status, responseComment, responderID);
    }

}