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
        String sql = "SELECT * FROM Rooms WHERE Status = 'Hoạt động' ORDER BY RoomName";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("RoomID"));
                room.setRoomCode(rs.getString("RoomCode"));
                room.setRoomName(rs.getString("RoomName"));
                room.setRoomType(rs.getString("RoomType"));
                room.setDescription(rs.getString("Description"));
                room.setStatus(rs.getString("Status"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
}