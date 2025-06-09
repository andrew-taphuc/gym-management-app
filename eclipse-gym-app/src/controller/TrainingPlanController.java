package controller;

import model.TrainingPlan;
import model.enums.enum_TrainerSpecialization;
import model.enums.enum_PlanStatus;
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
        String query = "SELECT * FROM TrainingPlans WHERE Status = 'Hoạt động' ORDER BY PlanCode";

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

    public boolean createTrainingPlan(TrainingPlan plan) {
        String query = "INSERT INTO TrainingPlans (PlanCode, PlanName, Type, SessionAmount, Price, Description) VALUES (?, ?, ?::trainer_specialization_enum, ?, ?, ?)";
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
        try {
            connection.setAutoCommit(false);

            // Bước 1: Cập nhật trạng thái của gói cũ thành "Đã cập nhật"
            String updateQuery = "UPDATE TrainingPlans SET Status = 'Đã cập nhật', UpdatedDate = CURRENT_TIMESTAMP WHERE PlanID = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, plan.getPlanId());
                updateStmt.executeUpdate();
            }

            // Bước 2: Thêm bản ghi mới với thông tin đã cập nhật
            String insertQuery = "INSERT INTO TrainingPlans (PlanCode, PlanName, Type, SessionAmount, Price, Description, Status, UpdateFrom) VALUES (?, ?, ?::trainer_specialization_enum, ?, ?, ?, 'Hoạt động', ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, plan.getPlanCode());
                insertStmt.setString(2, plan.getPlanName());
                insertStmt.setString(3, plan.getType().getValue());
                insertStmt.setInt(4, plan.getSessionAmount());
                insertStmt.setDouble(5, plan.getPrice());
                insertStmt.setString(6, plan.getDescription());
                // Nếu UpdateFrom của gói hiện tại là null, sử dụng PlanID của chính nó
                // Nếu không, sử dụng giá trị UpdateFrom hiện có để duy trì chuỗi lịch sử
                int updateFromValue = (plan.getUpdateFrom() == 0) ? plan.getPlanId() : plan.getUpdateFrom();
                insertStmt.setInt(7, updateFromValue); // ID gốc của chuỗi cập nhật

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
     * Lấy danh sách các bản cập nhật trước đây của một gói training
     * 
     * @param planId ID của gói training hiện tại
     * @return Danh sách các gói training đã được cập nhật trước đây (bản cũ nhất
     *         ở dưới cùng)
     */

    public List<TrainingPlan> getPlanUpdateHistory(int planId) {
        List<TrainingPlan> historyList = new ArrayList<>();

        // Trước tiên, tìm ID gốc của chuỗi lịch sử
        int originalPlanId = planId; // Mặc định planId hiện tại là gốc

        String checkCurrentPlanQuery = "SELECT * FROM TrainingPlans WHERE PlanID = ?";
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
        String originalPlanQuery = "SELECT * FROM TrainingPlans WHERE PlanID = ?";
        try (PreparedStatement originalStmt = connection.prepareStatement(originalPlanQuery)) {
            originalStmt.setInt(1, originalPlanId);
            ResultSet originalRs = originalStmt.executeQuery();

            if (originalRs.next()) {
                TrainingPlan originalPlan = new TrainingPlan();
                originalPlan.setPlanId(originalRs.getInt("PlanID"));
                originalPlan.setPlanCode(originalRs.getString("PlanCode"));
                originalPlan.setPlanName(originalRs.getString("PlanName"));
                originalPlan.setType(enum_TrainerSpecialization.fromValue(originalRs.getString("Type")));
                originalPlan.setSessionAmount(originalRs.getInt("SessionAmount"));
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
        String query = "SELECT * FROM TrainingPlans WHERE UpdateFrom = ? ORDER BY CreatedDate ASC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, originalPlanId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TrainingPlan plan = new TrainingPlan();
                plan.setPlanId(rs.getInt("PlanID"));
                plan.setPlanCode(rs.getString("PlanCode"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setType(enum_TrainerSpecialization.fromValue(rs.getString("Type")));
                plan.setSessionAmount(rs.getInt("SessionAmount"));
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

    public boolean deleteTrainingPlan(int planId) {
        // Soft delete - chỉ cập nhật status thành 'Đã xóa'
        String query = "UPDATE TrainingPlans SET Status = 'Đã xóa', UpdatedDate = CURRENT_TIMESTAMP WHERE PlanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, planId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra mã gói đã tồn tại hay chưa
     * 
     * @param planCode Mã gói cần kiểm tra
     * @return true nếu mã gói đã tồn tại, false nếu chưa tồn tại
     */
    public boolean checkPlanCodeExists(String planCode) {
        String query = "SELECT COUNT(*) FROM TrainingPlans WHERE PlanCode = ? AND Status = 'Hoạt động' ";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, planCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}