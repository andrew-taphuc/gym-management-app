package controller;

import model.Exercise;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseController {

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        String sql = "SELECT * FROM Exercises ORDER BY exercisename";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Exercise exercise = new Exercise();
                exercise.setExerciseId(rs.getInt("exerciseid"));
                exercise.setExerciseCode(rs.getString("exercisecode"));
                exercise.setExerciseName(rs.getString("exercisename"));
                exercise.setCategory(rs.getString("category"));
                exercise.setDescription(rs.getString("description"));
                exercises.add(exercise);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exercises;
    }

    public Exercise getExerciseById(int exerciseId) {
        String sql = "SELECT * FROM Exercises WHERE exerciseid = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, exerciseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Exercise exercise = new Exercise();
                exercise.setExerciseId(rs.getInt("exerciseid"));
                exercise.setExerciseCode(rs.getString("exercisecode"));
                exercise.setExerciseName(rs.getString("exercisename"));
                exercise.setCategory(rs.getString("category"));
                exercise.setDescription(rs.getString("description"));
                return exercise;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}