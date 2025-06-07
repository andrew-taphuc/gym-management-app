package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/gym_management";
    private static final String USER = "postgres";
    private static final String PASS = "123456";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
            return null;
        }
    }
}