package controller;

import model.TrainingSchedule;
import model.Exercise;
import model.TrainingScheduleExercise;
import model.ExerciseWithDetails;
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
        String sql = "SELECT ts.*, " +
                "u_trainer.fullname as trainer_name, " +
                "r.roomname as room_name " +
                "FROM TrainingSchedule ts " +
                "JOIN Members m ON ts.memberid = m.memberid " +
                "LEFT JOIN Trainers t ON ts.trainerid = t.trainerid " +
                "LEFT JOIN Users u_trainer ON t.userid = u_trainer.userid " +
                "LEFT JOIN Rooms r ON ts.roomid = r.roomid " +
                "WHERE m.userid = ? " +
                "ORDER BY ts.scheduledate DESC, ts.starttime DESC";
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

                // Thêm thông tin tên trainer và room
                ts.setTrainerName(rs.getString("trainer_name"));
                ts.setRoomName(rs.getString("room_name"));

                list.add(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ExerciseWithDetails> getExercisesByScheduleId(int scheduleId) {
        List<ExerciseWithDetails> exerciseDetails = new ArrayList<>();
        String sql = "SELECT e.exerciseid, e.exercisecode, e.exercisename, e.category, e.description, " +
                "tse.scheduleid, tse.set, tse.rep, tse.comment " +
                "FROM TrainingScheduleExercises tse " +
                "JOIN Exercises e ON tse.exerciseid = e.exerciseid " +
                "WHERE tse.scheduleid = ? " +
                "ORDER BY e.exerciseid";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Tạo Exercise object
                Exercise exercise = new Exercise();
                exercise.setExerciseId(rs.getInt("exerciseid"));
                exercise.setExerciseCode(rs.getString("exercisecode"));
                exercise.setExerciseName(rs.getString("exercisename"));
                exercise.setCategory(rs.getString("category"));
                exercise.setDescription(rs.getString("description"));

                // Tạo TrainingScheduleExercise object
                TrainingScheduleExercise scheduleExercise = new TrainingScheduleExercise();
                scheduleExercise.setScheduleId(rs.getInt("scheduleid"));
                scheduleExercise.setExerciseId(rs.getInt("exerciseid"));
                scheduleExercise.setSet(rs.getInt("set"));
                scheduleExercise.setRep(rs.getInt("rep"));
                scheduleExercise.setComment(rs.getString("comment"));

                // Tạo ExerciseWithDetails object
                ExerciseWithDetails detail = new ExerciseWithDetails(exercise, scheduleExercise);
                exerciseDetails.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exerciseDetails;
    }
}
