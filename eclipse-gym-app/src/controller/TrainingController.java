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

    public List<TrainingSchedule> getSchedulesByTrainerId(int trainerId) {
        List<TrainingSchedule> list = new ArrayList<>();
        String sql = "SELECT ts.*, " +
                "u_member.fullname as member_name, " +
                "r.roomname as room_name " +
                "FROM TrainingSchedule ts " +
                "JOIN Members m ON ts.memberid = m.memberid " +
                "LEFT JOIN Users u_member ON m.userid = u_member.userid " +
                "LEFT JOIN Rooms r ON ts.roomid = r.roomid " +
                "WHERE ts.trainerid = ? " +
                "ORDER BY ts.scheduledate DESC, ts.starttime DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
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

                // Thêm thông tin tên học viên và room
                ts.setMemberName(rs.getString("member_name"));
                ts.setRoomName(rs.getString("room_name"));

                list.add(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm bài tập vào buổi tập
    public boolean addExerciseToSchedule(int scheduleId, int exerciseId, int set, int rep, String comment) {
        String sql = "INSERT INTO TrainingScheduleExercises (ScheduleID, ExerciseID, Set, Rep, Comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, exerciseId);
            ps.setInt(3, set);
            ps.setInt(4, rep);
            ps.setString(5, comment);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin bài tập trong buổi tập
    public boolean updateExerciseInSchedule(int scheduleId, int exerciseId, int set, int rep, String comment) {
        String sql = "UPDATE TrainingScheduleExercises SET Set = ?, Rep = ?, Comment = ? WHERE ScheduleID = ? AND ExerciseID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, set);
            ps.setInt(2, rep);
            ps.setString(3, comment);
            ps.setInt(4, scheduleId);
            ps.setInt(5, exerciseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa bài tập khỏi buổi tập
    public boolean deleteExerciseFromSchedule(int scheduleId, int exerciseId) {
        String sql = "DELETE FROM TrainingScheduleExercises WHERE ScheduleID = ? AND ExerciseID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, exerciseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
