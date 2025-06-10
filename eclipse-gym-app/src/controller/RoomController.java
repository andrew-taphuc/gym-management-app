package controller;

import model.Room;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomController {
    private Connection connection;

    public RoomController() {
        this.connection = DBConnection.getConnection();
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms ORDER BY RoomName";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE RoomID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO Rooms (RoomCode, RoomName, RoomType, Description, Status) VALUES (?, ?, ?, ?, ?::room_status_enum)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomCode());
            ps.setString(2, room.getRoomName());
            ps.setString(3, room.getRoomType());
            ps.setString(4, room.getDescription());
            ps.setString(5, room.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE Rooms SET RoomName = ?, RoomType = ?, Description = ?, Status = ?::room_status_enum WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomName());
            ps.setString(2, room.getRoomType());
            ps.setString(3, room.getDescription());
            ps.setString(4, room.getStatus());
            ps.setInt(5, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("RoomID"));
        room.setRoomCode(rs.getString("RoomCode"));
        room.setRoomName(rs.getString("RoomName"));
        room.setRoomType(rs.getString("RoomType"));
        room.setDescription(rs.getString("Description"));
        room.setStatus(rs.getString("Status"));
        return room;
    }
}