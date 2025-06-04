package view.userView;

import model.Feedback;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    // Thêm feedback mới vào DB
    public boolean insertFeedback(int memberID, String feedbackType, String comment) {
        String sql = "INSERT INTO Feedback (MemberID, FeedbackType, Comment) VALUES (?, ?::feedback_type, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberID);
            pstmt.setString(2, feedbackType);
            pstmt.setString(3, comment);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách feedback của một member
    public List<Feedback> getFeedbacksByMemberID(int memberID) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
            SELECT FeedbackType, Comment, Status, 
                   TO_CHAR(FeedbackDate, 'YYYY-MM-DD') as FeedbackDate
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
                
                feedbacks.add(new Feedback(type, comment, status, date));
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
            SELECT f.FeedbackID, f.MemberID, f.FeedbackType, f.Comment, f.Status,
                   TO_CHAR(f.FeedbackDate, 'YYYY-MM-DD') as FeedbackDate,
                   m.Username
            FROM Feedback f
            JOIN Member m ON f.MemberID = m.MemberID
            ORDER BY f.FeedbackDate DESC
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
                
                // Có thể tạo FeedbackWithUser class hoặc dùng comment để hiển thị user
                feedbacks.add(new Feedback(type, comment + " (" + username + ")", status, date));
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
}