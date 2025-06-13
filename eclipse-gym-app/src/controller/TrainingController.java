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
                ts.setId(rs.getInt("scheduleid"));
                ts.setRegistrationId(rs.getInt("registrationid"));
                ts.setMemberId(rs.getInt("memberid"));
                ts.setTrainerId(rs.getInt("trainerid"));
                ts.setMembershipId(rs.getInt("membershipid"));
                ts.setDate(rs.getDate("scheduledate").toLocalDate());
                ts.setTime(rs.getTime("starttime").toLocalTime().toString());
                ts.setRoomId(rs.getInt("roomid"));
                ts.setStatus(rs.getString("status"));
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

    public List<TrainingSchedule> getSchedulesByTrainerId(int trainerId) {
        List<TrainingSchedule> list = new ArrayList<>();
        String sql = "SELECT ts.*, " +
                "u_member.fullname as member_name, " +
                "m.membercode as member_code, " +
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
                ts.setId(rs.getInt("scheduleid"));
                ts.setRegistrationId(rs.getInt("registrationid"));
                ts.setMemberId(rs.getInt("memberid"));
                ts.setTrainerId(rs.getInt("trainerid"));
                ts.setMembershipId(rs.getInt("membershipid"));
                ts.setDate(rs.getDate("scheduledate").toLocalDate());
                ts.setTime(rs.getTime("starttime").toLocalTime().toString());
                ts.setRoomId(rs.getInt("roomid"));
                ts.setStatus(rs.getString("status"));
                ts.setNotes(rs.getString("notes"));
                ts.setCreatedDate(rs.getTimestamp("createddate").toLocalDateTime());

                // Thêm thông tin tên học viên, mã hội viên và room
                ts.setMemberName(rs.getString("member_name"));
                ts.setMemberCode(rs.getString("member_code"));
                ts.setRoomName(rs.getString("room_name"));

                list.add(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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

    // Phân công hội viên cho huấn luyện viên (cập nhật trainerID cho registration
    // chưa có trainer)
    public boolean assignMemberToTrainer(int memberId, int trainerId) {
        String sql = "UPDATE TrainingRegistrations SET trainerid = ? WHERE memberid = ? AND (trainerid IS NULL OR trainerid = 0)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ps.setInt(2, memberId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Phân công hội viên cho huấn luyện viên theo registration ID cụ thể
    public boolean assignMemberToTrainerByRegistrationId(int registrationId, int trainerId) {
        String sql = "UPDATE TrainingRegistrations SET trainerid = ? WHERE registrationid = ? AND (trainerid IS NULL OR trainerid = 0)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            ps.setInt(2, registrationId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách hội viên chưa được phân công huấn luyện viên
    public List<TrainingRegistration> getUnassignedTrainingRegistrations() {
        List<TrainingRegistration> registrations = new ArrayList<>();
        String sql = "SELECT tr.*, " +
                "m.membercode, " +
                "u.fullname as member_name, " +
                "tp.planname " +
                "FROM TrainingRegistrations tr " +
                "JOIN Members m ON tr.memberid = m.memberid " +
                "JOIN Users u ON m.userid = u.userid " +
                "JOIN TrainingPlans tp ON tr.planid = tp.planid " +
                "WHERE tr.trainerid IS NULL OR tr.trainerid = 0 " +
                "ORDER BY tr.startdate DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TrainingRegistration registration = new TrainingRegistration();
                registration.setRegistrationId(rs.getInt("registrationid"));
                registration.setMemberId(rs.getInt("memberid"));
                registration.setPlanId(rs.getInt("planid"));
                registration.setTrainerId(rs.getInt("trainerid"));
                if (rs.getDate("startdate") != null) {
                    registration.setStartDate(rs.getDate("startdate").toLocalDate());
                }
                registration.setSessionsLeft(rs.getInt("sessionsleft"));
                registration.setPaymentId(rs.getInt("paymentid"));

                // Tạo Member object với thông tin cơ bản
                Member member = new Member();
                member.setMemberId(rs.getInt("memberid"));
                member.setMemberCode(rs.getString("membercode"));

                // Tạo User object với tên
                User user = new User();
                user.setFullName(rs.getString("member_name"));
                member.setUser(user);

                // Tạo TrainingPlan object với tên
                TrainingPlan plan = new TrainingPlan();
                plan.setPlanId(rs.getInt("planid"));
                plan.setPlanName(rs.getString("planname"));

                registration.setMember(member);
                registration.setPlan(plan);

                registrations.add(registration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registrations;
    }

    // Lấy danh sách hội viên chưa được phân công huấn luyện viên theo
    // specialization
    public List<TrainingRegistration> getUnassignedTrainingRegistrationsBySpecialization(
            model.enums.enum_TrainerSpecialization specialization) {
        List<TrainingRegistration> registrations = new ArrayList<>();
        String sql = "SELECT tr.*, " +
                "m.membercode, " +
                "u.fullname as member_name, " +
                "tp.planname, tp.type " +
                "FROM TrainingRegistrations tr " +
                "JOIN Members m ON tr.memberid = m.memberid " +
                "JOIN Users u ON m.userid = u.userid " +
                "JOIN TrainingPlans tp ON tr.planid = tp.planid " +
                "WHERE (tr.trainerid IS NULL OR tr.trainerid = 0) AND tp.type = ?::trainer_specialization_enum " +
                "ORDER BY tr.startdate DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, specialization.getValue());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TrainingRegistration registration = new TrainingRegistration();
                registration.setRegistrationId(rs.getInt("registrationid"));
                registration.setMemberId(rs.getInt("memberid"));
                registration.setPlanId(rs.getInt("planid"));
                registration.setTrainerId(rs.getInt("trainerid"));
                if (rs.getDate("startdate") != null) {
                    registration.setStartDate(rs.getDate("startdate").toLocalDate());
                }
                registration.setSessionsLeft(rs.getInt("sessionsleft"));
                registration.setPaymentId(rs.getInt("paymentid"));

                // Tạo Member object với thông tin cơ bản
                Member member = new Member();
                member.setMemberId(rs.getInt("memberid"));
                member.setMemberCode(rs.getString("membercode"));

                // Tạo User object với tên
                User user = new User();
                user.setFullName(rs.getString("member_name"));
                member.setUser(user);

                // Tạo TrainingPlan object với tên và type
                TrainingPlan plan = new TrainingPlan();
                plan.setPlanId(rs.getInt("planid"));
                plan.setPlanName(rs.getString("planname"));
                plan.setType(model.enums.enum_TrainerSpecialization.fromValue(rs.getString("type")));

                registration.setMember(member);
                registration.setPlan(plan);

                registrations.add(registration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registrations;
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

    // Thêm method mới để lấy TrainingRegistrations của học viên
    public List<TrainingRegistration> getTrainingRegistrationsByMemberId(int memberId) {
        List<TrainingRegistration> registrations = new ArrayList<>();
        String sql = "SELECT tr.*, " +
                "tp.planname, tp.type " +
                "FROM TrainingRegistrations tr " +
                "JOIN TrainingPlans tp ON tr.planid = tp.planid " +
                "WHERE tr.memberid = ? " +
                "ORDER BY tr.startdate DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TrainingRegistration registration = new TrainingRegistration();
                registration.setRegistrationId(rs.getInt("registrationid"));
                registration.setMemberId(rs.getInt("memberid"));
                registration.setPlanId(rs.getInt("planid"));
                registration.setTrainerId(rs.getInt("trainerid"));
                if (rs.getDate("startdate") != null) {
                    registration.setStartDate(rs.getDate("startdate").toLocalDate());
                }
                registration.setSessionsLeft(rs.getInt("sessionsleft"));
                registration.setPaymentId(rs.getInt("paymentid"));

                // Tạo TrainingPlan object với tên và type
                TrainingPlan plan = new TrainingPlan();
                plan.setPlanId(rs.getInt("planid"));
                plan.setPlanName(rs.getString("planname"));
                plan.setType(model.enums.enum_TrainerSpecialization.fromValue(rs.getString("type")));

                registration.setPlan(plan);
                registrations.add(registration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registrations;
    }

    // Thêm method mới để đếm số buổi đã lên lịch cho một registration
    public int getScheduledSessionsCount(int registrationId) {
        String sql = "SELECT COUNT(*) FROM TrainingSchedule WHERE registrationid = ? AND status = 'Đã lên lịch'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Thêm method mới để hủy lịch tập
    public boolean cancelTrainingSchedule(int scheduleId) throws SQLException {
        String sql = "UPDATE TrainingSchedule SET status = ?::training_status_enum WHERE scheduleid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enum_TrainingStatus.CANCELLED.getValue());
            ps.setInt(2, scheduleId);
            return ps.executeUpdate() > 0;
        }
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

    // Thêm method để lấy trạng thái của buổi tập
    public String getTrainingScheduleStatus(int scheduleId) {
        String status = "";
        String sql = "SELECT status FROM TrainingSchedule WHERE scheduleid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    // Thêm method để cập nhật đánh giá cho buổi tập
    public boolean updateTrainingScheduleRating(int scheduleId, int rating) {
        String sql = "UPDATE TrainingSchedule SET rating = ? WHERE scheduleid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setInt(2, scheduleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thêm method để lấy đánh giá hiện tại của buổi tập
    public int getTrainingScheduleRating(int scheduleId) {
        int rating = 0;
        String sql = "SELECT rating FROM TrainingSchedule WHERE scheduleid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rating = rs.getInt("rating");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rating;
    }
}
