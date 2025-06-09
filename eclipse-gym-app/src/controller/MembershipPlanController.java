package controller;

import model.MembershipPlan;
import model.enums.enum_PlanStatus;
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
        String query = "SELECT * FROM MembershipPlans WHERE Status = 'Hoạt động' ORDER BY PlanCode";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setDuration(rs.getInt("Duration"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setStatus(enum_PlanStatus.fromValue(rs.getString("Status")));
                plan.setDescription(rs.getString("Description"));
                if (rs.getTimestamp("CreatedDate") != null) {
                    plan.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                }
                if (rs.getTimestamp("UpdatedDate") != null) {
                    plan.setUpdatedDate(rs.getTimestamp("UpdatedDate").toLocalDateTime());
                }
                plan.setUpdateFrom(rs.getInt("UpdateFrom"));
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
                plan.setStatus(enum_PlanStatus.fromValue(rs.getString("Status")));
                plan.setDescription(rs.getString("Description"));
                if (rs.getTimestamp("CreatedDate") != null) {
                    plan.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                }
                if (rs.getTimestamp("UpdatedDate") != null) {
                    plan.setUpdatedDate(rs.getTimestamp("UpdatedDate").toLocalDateTime());
                }
                plan.setUpdateFrom(rs.getInt("UpdateFrom"));
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
        try {
            connection.setAutoCommit(false);

            // Bước 1: Cập nhật trạng thái của gói cũ thành "Đã cập nhật"
            String updateQuery = "UPDATE MembershipPlans SET Status = 'Đã cập nhật', UpdatedDate = CURRENT_TIMESTAMP WHERE PlanID = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, plan.getPlanId());
                updateStmt.executeUpdate();
            }

            // Bước 2: Thêm bản ghi mới với thông tin đã cập nhật
            String insertQuery = "INSERT INTO MembershipPlans (PlanCode, PlanName, Duration, Price, Description, Status, UpdateFrom) VALUES (?, ?, ?, ?, ?, 'Hoạt động', ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, plan.getPlanCode());
                insertStmt.setString(2, plan.getPlanName());
                insertStmt.setInt(3, plan.getDuration());
                insertStmt.setDouble(4, plan.getPrice());
                insertStmt.setString(5, plan.getDescription());
                // Nếu UpdateFrom của gói hiện tại là null, sử dụng PlanID của chính nó
                // Nếu không, sử dụng giá trị UpdateFrom hiện có để duy trì chuỗi lịch sử
                int updateFromValue = (plan.getUpdateFrom() == 0) ? plan.getPlanId() : plan.getUpdateFrom();
                insertStmt.setInt(6, updateFromValue); // ID gốc của chuỗi cập nhật

                int rowsAffected = insertStmt.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lấy danh sách các bản cập nhật trước đây của một gói membership
     * 
     * @param planId ID của gói membership hiện tại
     * @return Danh sách các gói membership đã được cập nhật trước đây (bản cũ nhất
     *         ở dưới cùng)
     */
    public List<MembershipPlan> getPlanUpdateHistory(int planId) {
        List<MembershipPlan> historyList = new ArrayList<>();

        // Trước tiên, tìm ID gốc của chuỗi lịch sử
        int originalPlanId = planId; // Mặc định planId hiện tại là gốc

        String checkCurrentPlanQuery = "SELECT * FROM MembershipPlans WHERE PlanID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkCurrentPlanQuery)) {
            checkStmt.setInt(1, planId);
            ResultSet currentRs = checkStmt.executeQuery();

            if (currentRs.next()) {
                int updateFrom = currentRs.getInt("UpdateFrom");

                // Nếu plan hiện tại có UpdateFrom, đó là ID gốc
                if (updateFrom != 0) {
                    originalPlanId = updateFrom;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG: Plan hiện tại ID = " + planId + ", ID gốc = " + originalPlanId);

        // Lấy plan gốc
        String originalPlanQuery = "SELECT * FROM MembershipPlans WHERE PlanID = ?";
        try (PreparedStatement originalStmt = connection.prepareStatement(originalPlanQuery)) {
            originalStmt.setInt(1, originalPlanId);
            ResultSet originalRs = originalStmt.executeQuery();

            if (originalRs.next()) {
                MembershipPlan originalPlan = new MembershipPlan();
                originalPlan.setPlanId(originalRs.getInt("PlanID"));
                originalPlan.setPlanCode(originalRs.getString("PlanCode"));
                originalPlan.setPlanName(originalRs.getString("PlanName"));
                originalPlan.setDuration(originalRs.getInt("Duration"));
                originalPlan.setPrice(originalRs.getDouble("Price"));
                originalPlan.setStatus(enum_PlanStatus.fromValue(originalRs.getString("Status")));
                originalPlan.setDescription(originalRs.getString("Description"));

                // Chuyển đổi Timestamp sang LocalDateTime
                if (originalRs.getTimestamp("CreatedDate") != null) {
                    originalPlan.setCreatedDate(originalRs.getTimestamp("CreatedDate").toLocalDateTime());
                }
                if (originalRs.getTimestamp("UpdatedDate") != null) {
                    originalPlan.setUpdatedDate(originalRs.getTimestamp("UpdatedDate").toLocalDateTime());
                }

                originalPlan.setUpdateFrom(originalRs.getInt("UpdateFrom"));
                historyList.add(originalPlan);
                System.out.println("DEBUG: Đã thêm plan gốc ID = " + originalPlan.getPlanId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Lấy tất cả các bản cập nhật từ plan gốc (UpdateFrom = originalPlanId)
        String query = "SELECT * FROM MembershipPlans WHERE UpdateFrom = ? ORDER BY CreatedDate ASC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, originalPlanId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MembershipPlan plan = new MembershipPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setDuration(rs.getInt("Duration"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setStatus(enum_PlanStatus.fromValue(rs.getString("Status")));
                plan.setDescription(rs.getString("Description"));

                // Chuyển đổi Timestamp sang LocalDateTime
                if (rs.getTimestamp("CreatedDate") != null) {
                    plan.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                }
                if (rs.getTimestamp("UpdatedDate") != null) {
                    plan.setUpdatedDate(rs.getTimestamp("UpdatedDate").toLocalDateTime());
                }

                plan.setUpdateFrom(rs.getInt("UpdateFrom"));
                historyList.add(plan);
                System.out.println("DEBUG: Đã thêm plan cập nhật ID = " + plan.getPlanId() + ", Status = "
                        + plan.getStatus().getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG: Tổng số plan trong lịch sử = " + historyList.size());
        return historyList;
    }

    public boolean deletePlan(int planId) {
        String query = "UPDATE MembershipPlans SET Status = 'Đã xóa', UpdatedDate = CURRENT_TIMESTAMP WHERE PlanID = ?";
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