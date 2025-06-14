package controller;

import model.TrainingSchedule;
import model.Exercise;
import model.TrainingScheduleExercise;
import model.ExerciseWithDetails;
import model.Member;
import model.User;
import model.TrainingRegistration;
import model.TrainingPlan;
import model.enums.enum_TrainingStatus;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingController {
    public List<ExerciseWithDetails> getExercisesByScheduleId(int scheduleId) {
        List<ExerciseWithDetails> exerciseDetails = new ArrayList<>();
        String sql = "SELECT e.exerciseid, e.exercisecode, e.exercisename, e.category, e.description, " +
                "tse.scheduleid, tse.rep, tse.set, tse.comment, tse.trainercomment " +
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
                scheduleExercise.setRep(rs.getInt("rep"));
                scheduleExercise.setSet(rs.getInt("set"));
                scheduleExercise.setComment(rs.getString("comment"));
                scheduleExercise.setTrainerComment(rs.getString("trainercomment"));

                // Tạo ExerciseWithDetails object
                ExerciseWithDetails detail = new ExerciseWithDetails(exercise, scheduleExercise);
                exerciseDetails.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exerciseDetails;
    }

    // Thêm bài tập vào buổi tập
    public boolean addExerciseToSchedule(int scheduleId, int exerciseId, int rep, int set, String comment) {
        String sql = "INSERT INTO TrainingScheduleExercises (ScheduleID, ExerciseID, Rep, Set, Comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, exerciseId);
            ps.setInt(3, rep);
            ps.setInt(4, set);
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

    // Lấy thông tin tất cả hội viên được quản lý bởi huấn luyện viên
    public List<Member> getMembersByTrainerId(int trainerId) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT DISTINCT m.memberid, m.userid, m.membercode, m.joindate, m.status, " +
                "u.username, u.email, u.phonenumber, u.fullname, u.dateofbirth, u.gender, u.address, " +
                "u.createdat, u.updateat, u.status as user_status, u.role " +
                "FROM Members m " +
                "JOIN Users u ON m.userid = u.userid " +
                "JOIN TrainingRegistrations tr ON m.memberid = tr.memberid " +
                "WHERE tr.trainerid = ? " +
                "ORDER BY u.fullname";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Tạo User object
                User user = new User();
                user.setUserId(rs.getInt("userid"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phonenumber"));
                user.setFullName(rs.getString("fullname"));
                if (rs.getDate("dateofbirth") != null) {
                    user.setDateOfBirth(rs.getDate("dateofbirth").toLocalDate());
                }
                user.setGender(model.enums.enum_Gender.fromValue(rs.getString("gender")));
                user.setAddress(rs.getString("address"));
                if (rs.getTimestamp("createdat") != null) {
                    user.setCreatedAt(rs.getTimestamp("createdat").toLocalDateTime());
                }
                if (rs.getTimestamp("updateat") != null) {
                    user.setUpdatedAt(rs.getTimestamp("updateat").toLocalDateTime());
                }
                user.setStatus(model.enums.enum_UserStatus.fromValue(rs.getString("user_status")));
                user.setRole(model.enums.enum_Role.fromValue(rs.getString("role")));

                // Tạo Member object
                Member member = new Member();
                member.setMemberId(rs.getInt("memberid"));
                member.setUserId(rs.getInt("userid"));
                member.setMemberCode(rs.getString("membercode"));
                if (rs.getDate("joindate") != null) {
                    member.setJoinDate(rs.getDate("joindate").toLocalDate());
                }
                member.setStatus(model.enums.enum_MemberStatus.fromValue(rs.getString("status")));
                member.setUser(user);

                members.add(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }


    public boolean addExercisesToSchedule(int scheduleId, List<TrainingScheduleExercise> exercises)
            throws SQLException {
        String sql = "INSERT INTO TrainingScheduleExercises (ScheduleID, ExerciseID, Rep, Set, Comment) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (TrainingScheduleExercise exercise : exercises) {
                pstmt.setInt(1, scheduleId);
                pstmt.setInt(2, exercise.getExerciseId());
                pstmt.setInt(3, exercise.getRep());
                pstmt.setInt(4, exercise.getSet());
                pstmt.setString(5, exercise.getComment());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            return Arrays.stream(results).allMatch(r -> r > 0);
        }
    }

    public List<TrainingScheduleExercise> getExercisesForSchedule(int scheduleId) throws SQLException {
        String sql = "SELECT tse.*, e.exercisecode, e.exercisename, e.category, e.description " +
                "FROM TrainingScheduleExercises tse " +
                "JOIN Exercises e ON tse.ExerciseID = e.ExerciseID " +
                "WHERE tse.ScheduleID = ?";

        List<TrainingScheduleExercise> exercises = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, scheduleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TrainingScheduleExercise exercise = new TrainingScheduleExercise();
                    exercise.setScheduleId(rs.getInt("ScheduleID"));
                    exercise.setExerciseId(rs.getInt("ExerciseID"));
                    exercise.setRep(rs.getInt("Rep"));
                    exercise.setSet(rs.getInt("Set"));
                    exercise.setComment(rs.getString("Comment"));
                    exercises.add(exercise);
                }
            }
        }

        return exercises;
    }

    // Cập nhật trạng thái các lịch tập đã hết hạn
    public boolean updateExpiredTrainingSchedules() {
        String sql = "SELECT update_expired_training_schedules()";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeQuery();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
