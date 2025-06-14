package controller;

import model.TrainingRegistration;
import model.Member;
import model.User;
import model.TrainingPlan;
import model.enums.enum_TrainerSpecialization;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrainingRegistrationController {
    private Connection connection;

    public TrainingRegistrationController() {
        this.connection = DBConnection.getConnection();
    }

    public List<TrainingRegistration> getTrainingRegistrationsByMemberId(int memberId) {
        List<TrainingRegistration> registrations = new ArrayList<>();
        String query = "SELECT tr.*, tp.planname, tp.type, u.fullname as trainername " +
                "FROM TrainingRegistrations tr " +
                "JOIN TrainingPlans tp ON tr.planid = tp.planid " +
                "LEFT JOIN Trainers t ON tr.trainerid = t.trainerid " +
                "LEFT JOIN Users u ON t.userid = u.userid " +
                "WHERE tr.memberid = ? " +
                "ORDER BY tr.startdate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

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
                registration.setTrainerName(rs.getString("trainername"));

                // Tạo TrainingPlan object
                model.TrainingPlan plan = new model.TrainingPlan();
                plan.setPlanId(rs.getInt("planid"));
                plan.setPlanName(rs.getString("planname"));
                plan.setType(model.enums.enum_TrainerSpecialization.fromValue(rs.getString("type")));
                registration.setPlan(plan);

                registrations.add(registration);
            }
            return registrations;
        } catch (SQLException e) {
            e.printStackTrace();
            return registrations;
        }
    }

    public boolean createTrainingRegistration(TrainingRegistration registration) {
        String query = "INSERT INTO TrainingRegistrations (memberid, planid, startdate, sessionsleft, paymentid) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, registration.getMemberId());
            stmt.setInt(2, registration.getPlanId());
            stmt.setDate(3, java.sql.Date.valueOf(registration.getStartDate()));
            stmt.setInt(4, registration.getSessionsLeft());
            stmt.setInt(5, registration.getPaymentId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<TrainingRegistration> getActivePTRegistrationsByMemberId(int memberId) {
        List<TrainingRegistration> list = new ArrayList<>();
        String query = "SELECT * FROM TrainingRegistrations WHERE MemberID = ? AND SessionsLeft > 0";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TrainingRegistration tr = new TrainingRegistration();
                tr.setRegistrationId(rs.getInt("RegistrationID"));
                tr.setMemberId(rs.getInt("MemberID"));
                tr.setTrainerId(rs.getInt("TrainerID"));
                tr.setStartDate(rs.getDate("StartDate").toLocalDate());
                tr.setSessionsLeft(rs.getInt("SessionsLeft"));
                list.add(tr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getTotalRemainingSessions(int memberId) {
        String sql = """
                    SELECT COALESCE(SUM(SessionsLeft), 0) as total_sessions_left
                    FROM TrainingRegistrations
                    WHERE MemberID = ? AND SessionsLeft > 0
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_sessions_left");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng số buổi tập còn lại: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
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

    // Lấy danh sách hội viên chưa được phân công huấn luyện viên theo specialization
    public List<TrainingRegistration> getUnassignedTrainingRegistrationsBySpecialization(
            enum_TrainerSpecialization specialization) {
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
                plan.setType(enum_TrainerSpecialization.fromValue(rs.getString("type")));

                registration.setMember(member);
                registration.setPlan(plan);

                registrations.add(registration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registrations;
    }

    // Phân công hội viên cho huấn luyện viên (cập nhật trainerID cho registration chưa có trainer)
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
}
