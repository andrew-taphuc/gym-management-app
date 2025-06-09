package controller;

import model.MembershipPlan;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class MembershipPlanController {
    private Connection connection;

    public MembershipPlanController() {
        this.connection = DBConnection.getConnection();
    }

    public List<MembershipPlan> getAllPlans() {
        List<MembershipPlan> plans = new ArrayList<>();
        String query = "SELECT * FROM MembershipPlans WHERE status = 'Hoạt động' ORDER BY PlanID";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setDuration(rs.getInt("Duration"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setDescription(rs.getString("Description"));
                plans.add(plan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }
 
    public MembershipPlan getPlanByID(int planId) {
        String query = "SELECT * FROM MembershipPlans WHERE PlanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, planId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setDuration(rs.getInt("Duration"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setDescription(rs.getString("Description"));
                return plan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createPlan(MembershipPlan plan) {
        String query = "INSERT INTO MembershipPlans (PlanCode, PlanName, Duration, Price, Description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, plan.getPlanCode());
            stmt.setString(2, plan.getPlanName());
            stmt.setInt(3, plan.getDuration());
            stmt.setDouble(4, plan.getPrice());
            stmt.setString(5, plan.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePlan(MembershipPlan plan) {
        String query = "UPDATE MembershipPlans SET PlanCode = ?, PlanName = ?, Duration = ?, Price = ?, Description = ? WHERE PlanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, plan.getPlanCode());
            stmt.setString(2, plan.getPlanName());
            stmt.setInt(3, plan.getDuration());
            stmt.setDouble(4, plan.getPrice());
            stmt.setString(5, plan.getDescription());
            stmt.setInt(6, plan.getPlanId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePlan(int planId) {
        String query = "DELETE FROM MembershipPlans WHERE PlanID = ?";
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