package controller;

import model.EquipmentType;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentTypeController {

    /**
     * Lấy tất cả loại thiết bị
     */
    public List<EquipmentType> getAllEquipmentTypes() {
        List<EquipmentType> equipmentTypes = new ArrayList<>();
        String sql = "SELECT * FROM EquipmentTypes ORDER BY EquipmentName";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                equipmentTypes.add(mapResultSetToEquipmentType(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipmentTypes;
    }

    /**
     * Lấy loại thiết bị theo ID
     */
    public EquipmentType getEquipmentTypeById(int equipmentTypeId) {
        String sql = "SELECT * FROM EquipmentTypes WHERE EquipmentTypeID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToEquipmentType(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy loại thiết bị theo tên
     */
    public EquipmentType getEquipmentTypeByName(String equipmentName) {
        String sql = "SELECT * FROM EquipmentTypes WHERE EquipmentName = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToEquipmentType(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tìm kiếm loại thiết bị theo tên (tìm kiếm mờ)
     */
    public List<EquipmentType> searchEquipmentTypesByName(String searchTerm) {
        List<EquipmentType> equipmentTypes = new ArrayList<>();
        String sql = "SELECT * FROM EquipmentTypes WHERE LOWER(EquipmentName) LIKE LOWER(?) ORDER BY EquipmentName";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchTerm + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                equipmentTypes.add(mapResultSetToEquipmentType(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipmentTypes;
    }

    /**
     * Thêm loại thiết bị mới
     */
    public boolean addEquipmentType(EquipmentType equipmentType) {
        String sql = "INSERT INTO EquipmentTypes (EquipmentName, Description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentType.getEquipmentName());
            ps.setString(2, equipmentType.getDescription());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật loại thiết bị
     */
    public boolean updateEquipmentType(EquipmentType equipmentType) {
        String sql = "UPDATE EquipmentTypes SET EquipmentName = ?, Description = ? WHERE EquipmentTypeID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentType.getEquipmentName());
            ps.setString(2, equipmentType.getDescription());
            ps.setInt(3, equipmentType.getEquipmentTypeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa loại thiết bị
     */
    public boolean deleteEquipmentType(int equipmentTypeId) {
        // Kiểm tra xem loại thiết bị có đang được sử dụng không
        if (isEquipmentTypeInUse(equipmentTypeId)) {
            System.out.println("Không thể xóa loại thiết bị vì đang được sử dụng trong RoomEquipments");
            return false;
        }

        String sql = "DELETE FROM EquipmentTypes WHERE EquipmentTypeID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentTypeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra tên loại thiết bị đã tồn tại chưa
     */
    public boolean isEquipmentNameExists(String equipmentName) {
        String sql = "SELECT COUNT(*) FROM EquipmentTypes WHERE LOWER(EquipmentName) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra tên loại thiết bị đã tồn tại chưa (trừ ID hiện tại - dùng cho
     * update)
     */
    public boolean isEquipmentNameExists(String equipmentName, int excludeId) {
        String sql = "SELECT COUNT(*) FROM EquipmentTypes WHERE LOWER(EquipmentName) = LOWER(?) AND EquipmentTypeID != ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentName);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra loại thiết bị có đang được sử dụng trong RoomEquipments không
     */
    public boolean isEquipmentTypeInUse(int equipmentTypeId) {
        String sql = "SELECT COUNT(*) FROM RoomEquipments WHERE EquipmentTypeID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Đếm số lượng thiết bị của một loại thiết bị
     */
    public int countEquipmentsByType(int equipmentTypeId) {
        String sql = "SELECT COUNT(*) FROM RoomEquipments WHERE EquipmentTypeID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy danh sách loại thiết bị có sắp xếp tùy chỉnh
     */
    public List<EquipmentType> getAllEquipmentTypesOrderBy(String orderColumn, String orderDirection) {
        List<EquipmentType> equipmentTypes = new ArrayList<>();

        // Validate order column and direction for security
        if (!isValidOrderColumn(orderColumn)) {
            orderColumn = "EquipmentName";
        }
        if (!orderDirection.equalsIgnoreCase("ASC") && !orderDirection.equalsIgnoreCase("DESC")) {
            orderDirection = "ASC";
        }

        String sql = "SELECT * FROM EquipmentTypes ORDER BY " + orderColumn + " " + orderDirection;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                equipmentTypes.add(mapResultSetToEquipmentType(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipmentTypes;
    }

    /**
     * Validate order column để tránh SQL injection
     */
    private boolean isValidOrderColumn(String column) {
        return column != null && (column.equals("EquipmentTypeID") ||
                column.equals("EquipmentName") ||
                column.equals("Description"));
    }

    /**
     * Map ResultSet to EquipmentType object
     */
    private EquipmentType mapResultSetToEquipmentType(ResultSet rs) throws SQLException {
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setEquipmentTypeId(rs.getInt("EquipmentTypeID"));
        equipmentType.setEquipmentName(rs.getString("EquipmentName"));
        equipmentType.setDescription(rs.getString("Description"));
        return equipmentType;
    }
}