package controller;

import model.User;
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    private Connection connection;

    public UserController() {
        this.connection = DBConnection.getConnection();
    }

    public User getUserByID(int userID) {
        String query = "SELECT * FROM Users WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setEmail(rs.getString("Email"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setFullName(rs.getString("FullName"));
                user.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
                user.setGender(enum_Gender.fromValue(rs.getString("Gender")));
                user.setAddress(rs.getString("Address"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("UpdateAt").toLocalDateTime());
                user.setStatus(enum_UserStatus.fromValue(rs.getString("Status")));
                user.setRole(enum_Role.fromValue(rs.getString("Role")));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User login(String username, String password) {
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setEmail(rs.getString("Email"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setFullName(rs.getString("FullName"));
                user.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());
                user.setGender(enum_Gender.fromValue(rs.getString("Gender")));
                user.setAddress(rs.getString("Address"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                user.setUpdatedAt(rs.getTimestamp("UpdateAt").toLocalDateTime());
                user.setStatus(enum_UserStatus.fromValue(rs.getString("Status")));
                user.setRole(enum_Role.fromValue(rs.getString("Role")));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
