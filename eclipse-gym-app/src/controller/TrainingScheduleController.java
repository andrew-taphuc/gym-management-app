package controller;

import utils.DBConnection;
import java.sql.*;

public class TrainingScheduleController {
    // Lấy ScheduleID của buổi PT hôm nay cho hội viên và registrationId
    public static int getTodayPTScheduleId(int memberId, int registrationId) {
        int scheduleId = -1;
        String sql = "SELECT ts.ScheduleID FROM TrainingSchedule ts " +
                     "JOIN TrainingRegistrations tr ON ts.MemberID = tr.MemberID " +
                     "WHERE ts.MemberID = ? " +
                     "AND tr.RegistrationID = ? " +
                     "AND ts.ScheduleDate = CURRENT_DATE " +
                     "AND ts.Status = 'Đã lên lịch' " +
                     "LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, registrationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    scheduleId = rs.getInt("ScheduleID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scheduleId;
    }
}