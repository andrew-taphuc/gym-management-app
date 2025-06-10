package controller;

import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<model.TrainingSchedule> getScheduledPTByMemberId(int memberId) {
        List<model.TrainingSchedule> list = new ArrayList<>();
        String sql = "SELECT * FROM TrainingSchedule WHERE MemberID = ? AND Status = 'Đã lên lịch' ORDER BY ScheduleDate, StartTime";
        try (Connection conn = utils.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.TrainingSchedule schedule = new model.TrainingSchedule();
                    schedule.setId(rs.getInt("ScheduleID"));
                    schedule.setMemberId(rs.getInt("MemberID"));
                    schedule.setTrainerId(rs.getInt("TrainerID"));
                    schedule.setMembershipId(rs.getInt("MembershipID"));
                    schedule.setDate(rs.getDate("ScheduleDate").toLocalDate());
                    schedule.setTime(rs.getString("StartTime"));
                    schedule.setDuration(rs.getInt("Duration"));
                    schedule.setRoomId(rs.getInt("RoomID"));
                    schedule.setStatus(rs.getString("Status"));
                    schedule.setNotes(rs.getString("Notes"));
                    schedule.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    list.add(schedule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}