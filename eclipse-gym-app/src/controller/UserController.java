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
import java.time.LocalDateTime;

public class UserController {
    private Connection connection;

    public UserController() {
        this.connection = DBConnection.getConnection();
    }

    public Integer getMemberIDByUserID(int userID) {
        String query = "SELECT MemberID FROM Members WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MemberID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    private static User currentUser; // Thêm thuộc tính static

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
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
                setCurrentUser(user); // Gán user đăng nhập thành công
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createUser(User user) {
        String query = "INSERT INTO Users (Username, Password, Email, PhoneNumber, FullName, " +
                "DateOfBirth, Gender, Address, CreatedAt, UpdateAt, Status, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?::gender_enum, ?, ?, ?, ?::user_status_enum, ?::role_enum) " +
                "RETURNING UserID";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getFullName());
            stmt.setDate(6, java.sql.Date.valueOf(user.getDateOfBirth()));
            stmt.setString(7, user.getGender().getValue());
            stmt.setString(8, user.getAddress());

            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(9, java.sql.Timestamp.valueOf(now));
            stmt.setTimestamp(10, java.sql.Timestamp.valueOf(now));

            stmt.setString(11, user.getStatus().getValue());
            stmt.setString(12, user.getRole().getValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT * FROM Users WHERE Username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có bản ghi nào trả về thì đã tồn tại
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        String query = "SELECT * FROM Users WHERE Email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Nếu có bản ghi nào trả về thì đã tồn tại
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE Users SET Email = ?, PhoneNumber = ?, FullName = ?, " +
                      "DateOfBirth = ?, Gender = ?::gender_enum, Address = ?, " +
                      "UpdateAt = CURRENT_TIMESTAMP WHERE UserID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getFullName());
            stmt.setDate(4, java.sql.Date.valueOf(user.getDateOfBirth()));
            stmt.setString(5, user.getGender().getValue());
            stmt.setString(6, user.getAddress());
            stmt.setInt(7, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserStatus(User user) {
        String query = "UPDATE Users SET Status = ?::user_status_enum, UpdateAt = CURRENT_TIMESTAMP WHERE UserID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getStatus().getValue());
            stmt.setInt(2, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
