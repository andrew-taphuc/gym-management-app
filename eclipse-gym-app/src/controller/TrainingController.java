package controller;

import model.TrainingSchedule;
import model.enums.enum_TrainingStatus;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrainingController {
    public List<TrainingSchedule> getSchedulesByUserId(int userId) {
        List<TrainingSchedule> list = new ArrayList<>();
        String sql = "SELECT ts.* FROM TrainingSchedule ts " +
                "JOIN Members m ON ts.memberid = m.memberid " +
                "WHERE m.userid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TrainingSchedule ts = new TrainingSchedule();
                ts.setScheduleId(rs.getInt("scheduleid"));
                ts.setMemberId(rs.getInt("memberid"));
                ts.setTrainerId(rs.getInt("trainerid"));
                ts.setMembershipId(rs.getInt("membershipid"));
                ts.setScheduleDate(rs.getDate("scheduledate").toLocalDate());
                ts.setStartTime(rs.getTime("starttime").toLocalTime());
                ts.setDuration(rs.getInt("duration"));
                ts.setRoomId(rs.getInt("roomid"));
                ts.setStatus(enum_TrainingStatus.fromValue(rs.getString("status")));
                ts.setNotes(rs.getString("notes"));
                ts.setCreatedDate(rs.getTimestamp("createddate").toLocalDateTime());
                list.add(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<model.Exercise> getExercisesByScheduleId(int scheduleId) {
        List<model.Exercise> exercises = new ArrayList<>();
        String sql = "SELECT e.* FROM TrainingScheduleExercises tse " +
                "JOIN Exercises e ON tse.exerciseid = e.exerciseid " +
                "WHERE tse.scheduleid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.Exercise ex = new model.Exercise();
                ex.setExerciseId(rs.getInt("exerciseid"));
                ex.setExerciseCode(rs.getString("exercisecode"));
                ex.setExerciseName(rs.getString("exercisename"));
                ex.setCategory(rs.getString("category"));
                ex.setDescription(rs.getString("description"));
                exercises.add(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exercises;
    }
}
