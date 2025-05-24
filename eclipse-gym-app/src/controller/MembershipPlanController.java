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
        String query = "SELECT * FROM MembershipPlans ORDER BY PlanID";

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
}