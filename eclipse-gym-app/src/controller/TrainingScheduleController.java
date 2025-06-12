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

    public static List<model.TrainingSchedule> getUpcomingSchedules(int memberId) {
        List<model.TrainingSchedule> schedules = new ArrayList<>();
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
                model.TrainingSchedule schedule = new model.TrainingSchedule();
                schedule.setId(rs.getInt("ScheduleID"));
                schedule.setRegistrationId(rs.getInt("RegistrationID"));
                schedule.setMemberId(rs.getInt("MemberID"));
                schedule.setTrainerId(rs.getInt("TrainerID"));
                schedule.setMembershipId(rs.getInt("MembershipID"));
                schedule.setDate(rs.getDate("ScheduleDate").toLocalDate());
                schedule.setTime(rs.getString("StartTime"));
                schedule.setDuration(rs.getInt("Duration"));
                schedule.setRoomId(rs.getInt("RoomID"));
                schedule.setStatus(rs.getString("Status"));
                schedule.setNotes(rs.getString("Notes"));
                schedule.setRoomName(rs.getString("RoomName"));
                schedule.setTrainerName(rs.getString("TrainerName"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch tập sắp tới: " + e.getMessage());
            e.printStackTrace();
        }
        return schedules;
    }
}