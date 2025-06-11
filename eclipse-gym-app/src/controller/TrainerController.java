package controller;

import utils.DBConnection;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import model.Trainer;
import model.User;

public class TrainerController {
    public int getTrainerIdByUserId(int userId) {
        String sql = "SELECT trainerid FROM Trainers WHERE userid = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("trainerid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }

    public List<Trainer> getAllTrainersWithRating() {
        List<Trainer> trainers = new ArrayList<>();
        String sql = "SELECT t.trainerid, t.userid, t.trainercode, t.specialization, t.bio, t.status as trainer_status, "
                +
                "u.username, u.email, u.phonenumber, u.fullname, u.dateofbirth, u.gender, u.address, " +
                "u.createdat, u.updateat, u.status as user_status, u.role, " +
                "COALESCE(AVG(ts.rating), 0) as avg_rating " +
                "FROM Trainers t " +
                "JOIN Users u ON t.userid = u.userid " +
                "LEFT JOIN TrainingSchedule ts ON t.trainerid = ts.trainerid AND ts.rating IS NOT NULL " +
                "WHERE t.status = 'Đang làm việc' " +
                "GROUP BY t.trainerid, t.userid, t.trainercode, t.specialization, t.bio, t.status, " +
                "u.username, u.email, u.phonenumber, u.fullname, u.dateofbirth, u.gender, u.address, " +
                "u.createdat, u.updateat, u.status, u.role " +
                "ORDER BY u.fullname";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Tạo User object
                User user = new User();
                user.setUserId(rs.getInt("userid"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phonenumber"));
                user.setFullName(rs.getString("fullname"));
                if (rs.getDate("dateofbirth") != null) {
                    user.setDateOfBirth(rs.getDate("dateofbirth").toLocalDate());
                }
                user.setGender(model.enums.enum_Gender.fromValue(rs.getString("gender")));
                user.setAddress(rs.getString("address"));
                if (rs.getTimestamp("createdat") != null) {
                    user.setCreatedAt(rs.getTimestamp("createdat").toLocalDateTime());
                }
                if (rs.getTimestamp("updateat") != null) {
                    user.setUpdatedAt(rs.getTimestamp("updateat").toLocalDateTime());
                }
                user.setStatus(model.enums.enum_UserStatus.fromValue(rs.getString("user_status")));
                user.setRole(model.enums.enum_Role.fromValue(rs.getString("role")));

                // Tạo Trainer object
                Trainer trainer = new Trainer();
                trainer.setTrainerId(rs.getInt("trainerid"));
                trainer.setUserId(rs.getInt("userid"));
                trainer.setTrainerCode(rs.getString("trainercode"));
                trainer.setSpecialization(
                        model.enums.enum_TrainerSpecialization.fromValue(rs.getString("specialization")));
                trainer.setBio(rs.getString("bio"));
                trainer.setRating(rs.getDouble("avg_rating"));
                trainer.setStatus(model.enums.enum_TrainerStatus.fromValue(rs.getString("trainer_status")));
                trainer.setUser(user);

                trainers.add(trainer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trainers;
    }
}