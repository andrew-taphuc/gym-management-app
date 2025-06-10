package controller;

import model.Feedback;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackController {
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

        return null; // Không tìm thấy hoặc lỗi xảy ra
    }

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

    /**
     * Kiểm tra EquipmentID có tồn tại trong database không
     * 
     * @param equipmentId ID thiết bị cần kiểm tra
     * @return true nếu tồn tại, false nếu không tồn tại
     */
    public boolean checkEquipmentExists(int equipmentId) {
        String sql = "SELECT COUNT(*) FROM roomequipment WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra Equipment ID: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Kiểm tra EquipmentCode có tồn tại trong database không
     * 
     * @param equipmentCode Mã thiết bị cần kiểm tra
     * @return true nếu tồn tại, false nếu không tồn tại
     */
    public boolean checkEquipmentCodeExists(String equipmentCode) {
        String sql = "SELECT COUNT(*) FROM roomequipment WHERE EquipmentCode = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra Equipment Code: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy thông tin thiết bị theo mã thiết bị để hiển thị
     * 
     * @param equipmentCode Mã thiết bị
     * @return Thông tin thiết bị (tên + mã)
     */
    public String getEquipmentInfoByCode(String equipmentCode) {
        String sql = """
                SELECT re.EquipmentCode, et.EquipmentName, re.Status
                FROM roomequipment re
                JOIN EquipmentTypes et ON re.EquipmentTypeID = et.EquipmentTypeID
                WHERE re.EquipmentCode = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String code = rs.getString("EquipmentCode");
                String name = rs.getString("EquipmentName");
                String status = rs.getString("Status");
                return name + " (" + code + ") - " + status;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin thiết bị theo mã: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy EquipmentID từ EquipmentCode
     * 
     * @param equipmentCode Mã thiết bị
     * @return ID thiết bị, hoặc null nếu không tìm thấy
     */
    public Integer getEquipmentIdByCode(String equipmentCode) {
        String sql = "SELECT RoomEquipmentID FROM roomequipment WHERE EquipmentCode = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("RoomEquipmentID");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Equipment ID từ mã: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy EquipmentCode từ EquipmentID
     * 
     * @param equipmentId ID thiết bị
     * @return Mã thiết bị, hoặc null nếu không tìm thấy
     */
    public String getEquipmentCodeById(int equipmentId) {
        String sql = "SELECT EquipmentCode FROM roomequipment WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("EquipmentCode");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Equipment Code từ ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
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

    // public boolean replyFeedback(int feedbackID, String responseComment, int
    // responderID) {
    // String sql = """
    // UPDATE Feedback
    // SET ResponseComment = ?,
    // ResponseDate = CURRENT_TIMESTAMP,
    // ResponderID = ?
    // WHERE FeedbackID = ?
    // """;

    // try (Connection conn = DBConnection.getConnection();
    // PreparedStatement pstmt = conn.prepareStatement(sql)) {

    // pstmt.setString(1, responseComment);
    // pstmt.setInt(2, responderID);
    // pstmt.setInt(3, feedbackID);

    // int rowsAffected = pstmt.executeUpdate();
    // return rowsAffected > 0;

    // } catch (SQLException e) {
    // System.err.println("Lỗi khi cập nhật phản hồi: " + e.getMessage());
    // e.printStackTrace();
    // return false;
    // }
    // }
}