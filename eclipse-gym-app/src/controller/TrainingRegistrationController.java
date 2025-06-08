package controller;

import model.TrainingRegistration;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrainingRegistrationController {
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
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
