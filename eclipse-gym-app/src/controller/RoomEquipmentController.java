package controller;

import model.RoomEquipment;
import utils.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomEquipmentController {

    /**
     * Lấy tất cả thiết bị phòng
     */
    public List<RoomEquipment> getAllRoomEquipments() {
        List<RoomEquipment> roomEquipments = new ArrayList<>();
        String sql = "SELECT * FROM roomequipment ORDER BY EquipmentCode";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomEquipments.add(mapResultSetToRoomEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomEquipments;
    }

    /**
     * Lấy thiết bị phòng theo ID
     */
    public RoomEquipment getRoomEquipmentById(int roomEquipmentId) {
        String sql = "SELECT * FROM roomequipment WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomEquipmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToRoomEquipment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy thiết bị theo Room ID
     */
    public List<RoomEquipment> getRoomEquipmentsByRoomId(int roomId) {
        List<RoomEquipment> roomEquipments = new ArrayList<>();
        String sql = "SELECT * FROM roomequipment WHERE RoomID = ? ORDER BY EquipmentCode";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomEquipments.add(mapResultSetToRoomEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomEquipments;
    }

    /**
     * Lấy thiết bị theo Equipment Type ID
     */
    public List<RoomEquipment> getRoomEquipmentsByEquipmentTypeId(int equipmentTypeId) {
        List<RoomEquipment> roomEquipments = new ArrayList<>();
        String sql = "SELECT * FROM roomequipment WHERE EquipmentTypeID = ? ORDER BY EquipmentCode";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentTypeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomEquipments.add(mapResultSetToRoomEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomEquipments;
    }

    /**
     * Lấy thiết bị theo Equipment Code
     */
    public RoomEquipment getRoomEquipmentByCode(String equipmentCode) {
        String sql = "SELECT * FROM roomequipment WHERE EquipmentCode = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToRoomEquipment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm thiết bị phòng mới
     */
    public boolean addRoomEquipment(RoomEquipment roomEquipment) {
        String sql = "INSERT INTO roomequipment (RoomID, EquipmentTypeID, EquipmentCode, Quantity, Status, Description, CreatedDate, UpdatedDate) VALUES (?, ?, ?, ?, ?::equipment_status_enum, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomEquipment.getRoomId());
            ps.setInt(2, roomEquipment.getEquipmentTypeId());
            ps.setString(3, roomEquipment.getEquipmentCode());
            ps.setInt(4, roomEquipment.getQuantity());
            ps.setString(5, roomEquipment.getStatus());
            ps.setString(6, roomEquipment.getDescription());
            ps.setTimestamp(7, Timestamp.valueOf(roomEquipment.getCreatedDate()));
            ps.setTimestamp(8, Timestamp.valueOf(roomEquipment.getUpdatedDate()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật thiết bị phòng
     */
    public boolean updateRoomEquipment(RoomEquipment roomEquipment) {
        String sql = "UPDATE roomequipment SET RoomID = ?, EquipmentTypeID = ?, EquipmentCode = ?, Quantity = ?, Status = ?::equipment_status_enum, Description = ?, UpdatedDate = ? WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomEquipment.getRoomId());
            ps.setInt(2, roomEquipment.getEquipmentTypeId());
            ps.setString(3, roomEquipment.getEquipmentCode());
            ps.setInt(4, roomEquipment.getQuantity());
            ps.setString(5, roomEquipment.getStatus());
            ps.setString(6, roomEquipment.getDescription());
            ps.setTimestamp(7, Timestamp.valueOf(roomEquipment.getUpdatedDate()));
            ps.setInt(8, roomEquipment.getRoomEquipmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa thiết bị phòng
     */
    public boolean deleteRoomEquipment(int roomEquipmentId) {
        String sql = "DELETE FROM roomequipment WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomEquipmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật trạng thái thiết bị
     */
    public boolean updateRoomEquipmentStatus(int roomEquipmentId, String status) {
        String sql = "UPDATE roomequipment SET Status = ?::equipment_status_enum, UpdatedDate = ? WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, roomEquipmentId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật số lượng thiết bị
     */
    public boolean updateRoomEquipmentQuantity(int roomEquipmentId, int quantity) {
        String sql = "UPDATE roomequipment SET Quantity = ?, UpdatedDate = ? WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, roomEquipmentId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra Equipment Code đã tồn tại chưa
     */
    public boolean isEquipmentCodeExists(String equipmentCode) {
        String sql = "SELECT COUNT(*) FROM roomequipment WHERE EquipmentCode = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipmentCode);
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
     * Lấy thiết bị theo trạng thái
     */
    public List<RoomEquipment> getRoomEquipmentsByStatus(String status) {
        List<RoomEquipment> roomEquipments = new ArrayList<>();
        String sql = "SELECT * FROM roomequipment WHERE Status = ?::equipment_status_enum ORDER BY EquipmentCode";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomEquipments.add(mapResultSetToRoomEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomEquipments;
    }

    /**
     * Map ResultSet to RoomEquipment object
     */
    private RoomEquipment mapResultSetToRoomEquipment(ResultSet rs) throws SQLException {
        RoomEquipment roomEquipment = new RoomEquipment();
        roomEquipment.setRoomEquipmentId(rs.getInt("RoomEquipmentID"));
        roomEquipment.setRoomId(rs.getInt("RoomID"));
        roomEquipment.setEquipmentTypeId(rs.getInt("EquipmentTypeID"));
        roomEquipment.setEquipmentCode(rs.getString("EquipmentCode"));
        roomEquipment.setQuantity(rs.getInt("Quantity"));
        roomEquipment.setStatus(rs.getString("Status"));
        roomEquipment.setDescription(rs.getString("Description"));

        Timestamp createdTimestamp = rs.getTimestamp("CreatedDate");
        if (createdTimestamp != null) {
            roomEquipment.setCreatedDate(createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp = rs.getTimestamp("UpdatedDate");
        if (updatedTimestamp != null) {
            roomEquipment.setUpdatedDate(updatedTimestamp.toLocalDateTime());
        }

        return roomEquipment;
    }
}