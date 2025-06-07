package controller;

import model.Promotion;
import utils.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PromosController {
    /**
     * Cập nhật trạng thái tất cả promotion dựa trên thời gian hiện tại
     */
    public void updatePromotionStatus() {
        String sql = "SELECT update_promotion_status()";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Promotion> getAllPromotions() {
        // Cập nhật trạng thái trước khi lấy danh sách
        updatePromotionStatus();

        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotions";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPromotion(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Promotion getPromotionById(int id) {
        String sql = "SELECT * FROM Promotions WHERE PromotionID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPromotion(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addPromotion(Promotion promo) {
        String sql = "INSERT INTO Promotions (PromotionCode, PromotionName, Description, DiscountType, DiscountValue, StartDate, EndDate, CreatedDate, UpdatedDate) VALUES (?, ?, ?, ?::discount_type, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promo.getPromotionCode());
            ps.setString(2, promo.getPromotionName());
            ps.setString(3, promo.getDescription());
            ps.setString(4, promo.getDiscountType());
            ps.setDouble(5, promo.getDiscountValue());
            ps.setTimestamp(6, Timestamp.valueOf(promo.getStartDate()));
            ps.setTimestamp(7, Timestamp.valueOf(promo.getEndDate()));
            ps.setTimestamp(8, Timestamp.valueOf(promo.getCreatedDate()));
            ps.setTimestamp(9, Timestamp.valueOf(promo.getUpdatedDate()));
            boolean result = ps.executeUpdate() > 0;

            // Cập nhật trạng thái sau khi thêm
            if (result) {
                updatePromotionStatus();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePromotion(Promotion promo) {
        String sql = "UPDATE Promotions SET PromotionCode=?, PromotionName=?, Description=?, DiscountType=?::discount_type, DiscountValue=?, StartDate=?, EndDate=?, CreatedDate=?, UpdatedDate=? WHERE PromotionID=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, promo.getPromotionCode());
            ps.setString(2, promo.getPromotionName());
            ps.setString(3, promo.getDescription());
            ps.setString(4, promo.getDiscountType());
            ps.setDouble(5, promo.getDiscountValue());
            ps.setTimestamp(6, Timestamp.valueOf(promo.getStartDate()));
            ps.setTimestamp(7, Timestamp.valueOf(promo.getEndDate()));
            ps.setTimestamp(8, Timestamp.valueOf(promo.getCreatedDate()));
            ps.setTimestamp(9, Timestamp.valueOf(promo.getUpdatedDate()));
            ps.setInt(10, promo.getPromotionId());
            boolean result = ps.executeUpdate() > 0;

            // Cập nhật trạng thái sau khi sửa
            if (result) {
                updatePromotionStatus();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePromotion(int id) {
        String sql = "DELETE FROM Promotions WHERE PromotionID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Promotion getPromotionByCode(String code) {
        String sql = "SELECT * FROM Promotions WHERE PromotionCode = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPromotion(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion promo = new Promotion();
        promo.setPromotionId(rs.getInt("PromotionID"));
        promo.setPromotionCode(rs.getString("PromotionCode"));
        promo.setPromotionName(rs.getString("PromotionName"));
        promo.setDescription(rs.getString("Description"));
        promo.setDiscountType(rs.getString("DiscountType"));
        promo.setDiscountValue(rs.getDouble("DiscountValue"));
        promo.setStartDate(rs.getTimestamp("StartDate").toLocalDateTime());
        promo.setEndDate(rs.getTimestamp("EndDate").toLocalDateTime());
        promo.setStatus(rs.getString("Status"));
        promo.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
        promo.setUpdatedDate(rs.getTimestamp("UpdatedDate").toLocalDateTime());
        return promo;
    }
}