package controller;

import model.TrainingRegistration;
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
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM TrainingRegistrations WHERE MemberID = ? AND SessionsLeft > 0";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, memberId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        TrainingRegistration reg = new TrainingRegistration();
                        reg.setRegistrationId(rs.getInt("RegistrationID"));
                        reg.setMemberId(rs.getInt("MemberID"));
                        reg.setPlanId(rs.getInt("PlanID"));
                        reg.setTrainerId(rs.getInt("TrainerID"));
                        reg.setStartDate(rs.getDate("StartDate").toLocalDate());
                        reg.setSessionsLeft(rs.getInt("SessionsLeft"));
                        reg.setPaymentId(rs.getInt("PaymentID"));
                        list.add(reg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean checkinPT(int memberId, int membershipId, int registrationId, int trainingScheduleId) {
        String query = "INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, TrainingScheduleID) VALUES (?, ?, NOW(), ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, membershipId);
            stmt.setInt(3, trainingScheduleId);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                // Trừ số buổi còn lại của gói PT
                String updateSql = "UPDATE TrainingRegistrations SET SessionsLeft = SessionsLeft - 1 WHERE RegistrationID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, registrationId);
                    updateStmt.executeUpdate();
                }
                
                // Cập nhật trạng thái lịch tập thành 'Hoàn thành'
                String updateStatusSql = "UPDATE TrainingSchedule SET Status = 'Hoàn thành' WHERE ScheduleID = ?";
                try (PreparedStatement statusStmt = conn.prepareStatement(updateStatusSql)) {
                    statusStmt.setInt(1, trainingScheduleId);
                    statusStmt.executeUpdate();
                }
                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
