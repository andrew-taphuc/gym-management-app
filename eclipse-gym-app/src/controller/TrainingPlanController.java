package controller;

import model.TrainingPlan;
import model.enums.enum_TrainerSpecialization;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class TrainingPlanController {
    private Connection connection;

    public TrainingPlanController() {
        this.connection = DBConnection.getConnection();
    }

    public List<TrainingPlan> getAllPlans() {
        List<TrainingPlan> plans = new ArrayList<>();
        String query = "SELECT * FROM TrainingPlans ORDER BY PlanID";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TrainingPlan plan = new TrainingPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setType(enum_TrainerSpecialization.fromValue(rs.getString("Type")));
                plan.setSessionAmount(rs.getInt("SessionAmount"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setDescription(rs.getString("Description"));
                plans.add(plan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }

    public TrainingPlan getPlanByID(int planId) {
        String query = "SELECT * FROM TrainingPlans WHERE PlanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, planId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TrainingPlan plan = new TrainingPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setType(enum_TrainerSpecialization.fromValue(rs.getString("Type")));
                plan.setSessionAmount(rs.getInt("SessionAmount"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setDescription(rs.getString("Description"));
                return plan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createTrainingPlan(TrainingPlan plan) {
        String query = "INSERT INTO TrainingPlans (PlanCode, PlanName, Type, SessionAmount, Price, Description) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, plan.getPlanCode());
            stmt.setString(2, plan.getPlanName());
            stmt.setString(3, plan.getType().getValue());
            stmt.setInt(4, plan.getSessionAmount());
            stmt.setDouble(5, plan.getPrice());
            stmt.setString(6, plan.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrainingPlan(TrainingPlan plan) {
        String query = "UPDATE TrainingPlans SET PlanCode = ?, PlanName = ?, Type = ?, SessionAmount = ?, Price = ?, Description = ? WHERE PlanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, plan.getPlanCode());
            stmt.setString(2, plan.getPlanName());
            stmt.setString(3, plan.getType().getValue());
            stmt.setInt(4, plan.getSessionAmount());
            stmt.setDouble(5, plan.getPrice());
            stmt.setString(6, plan.getDescription());
            stmt.setInt(7, plan.getPlanId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrainingPlan(int planId) {
        String query = "DELETE FROM TrainingPlans WHERE PlanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, planId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}