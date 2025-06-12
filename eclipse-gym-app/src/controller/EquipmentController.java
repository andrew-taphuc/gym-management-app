package controller;

import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentController {

    /**
     * Lấy EquipmentCode từ EquipmentID
     * 
     * @param equipmentId ID thiết bị
     * @return Mã thiết bị, hoặc null nếu không tìm thấy
     */
    public String getEquipmentCodeById(int equipmentId) {
        String sql = "SELECT EquipmentCode FROM roomequipment WHERE RoomEquipmentID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, equipmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("EquipmentCode");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy Equipment Code từ ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy thông tin thiết bị theo mã thiết bị để hiển thị
     * 
     * @param equipmentCode Mã thiết bị
     * @return Thông tin thiết bị (tên + mã)
     */
    public String getEquipmentInfoByCode(String equipmentCode) {
        String sql = """
                SELECT re.EquipmentCode, et.EquipmentName, re.Status
                FROM roomequipment re
                JOIN EquipmentTypes et ON re.EquipmentTypeID = et.EquipmentTypeID
                WHERE re.EquipmentCode = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String code = rs.getString("EquipmentCode");
                String name = rs.getString("EquipmentName");
                String status = rs.getString("Status");
                return name + " (" + code + ") - " + status;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin thiết bị theo mã: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
} 