package controller;

import utils.DBConnection;
import java.sql.*;

public class TrainerController {
    public int getTrainerIdByUserId(int userId) {
        String sql = "SELECT trainerid FROM Trainers WHERE userid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("trainerid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }
}