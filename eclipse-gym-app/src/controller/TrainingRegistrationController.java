package controller;

import model.TrainingRegistration;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
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
}
