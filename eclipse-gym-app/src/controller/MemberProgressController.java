package controller;

import model.MemberProgress;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MemberProgressController {

    // Lấy toàn bộ lịch sử số đo cơ thể của một hội viên theo MemberID, sắp xếp mới
    // nhất trước
    public List<MemberProgress> getProgressHistoryByMemberId(int memberId) {
        List<MemberProgress> progressList = new ArrayList<>();
        String sql = """
                    SELECT ProgressID, MemberID, MeasurementDate, Weight, Height, BMI, BodyFatPercentage,
                           Chest, Waist, Hip, Biceps, Thigh, TrainerID, Notes
                    FROM MemberProgress
                    WHERE MemberID = ?
                    ORDER BY MeasurementDate DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MemberProgress progress = new MemberProgress(
                        rs.getInt("ProgressID"),
                        rs.getInt("MemberID"),
                        rs.getDate("MeasurementDate").toLocalDate(),
                        rs.getBigDecimal("Weight") != null ? rs.getBigDecimal("Weight").doubleValue() : null,
                        rs.getBigDecimal("Height") != null ? rs.getBigDecimal("Height").doubleValue() : null,
                        rs.getBigDecimal("BMI") != null ? rs.getBigDecimal("BMI").doubleValue() : null,
                        rs.getBigDecimal("BodyFatPercentage") != null
                                ? rs.getBigDecimal("BodyFatPercentage").doubleValue()
                                : null,
                        rs.getBigDecimal("Chest") != null ? rs.getBigDecimal("Chest").doubleValue() : null,
                        rs.getBigDecimal("Waist") != null ? rs.getBigDecimal("Waist").doubleValue() : null,
                        rs.getBigDecimal("Hip") != null ? rs.getBigDecimal("Hip").doubleValue() : null,
                        rs.getBigDecimal("Biceps") != null ? rs.getBigDecimal("Biceps").doubleValue() : null,
                        rs.getBigDecimal("Thigh") != null ? rs.getBigDecimal("Thigh").doubleValue() : null,
                        rs.getObject("TrainerID") != null ? rs.getInt("TrainerID") : null,
                        rs.getString("Notes"));
                progressList.add(progress);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy lịch sử số đo cơ thể: " + e.getMessage());
            e.printStackTrace();
        }
        return progressList;
    }


    // Inner classes để lưu trữ dữ liệu
    public static class TrainingSchedule {
        private final int scheduleId;
        private final int registrationId;
        private final int memberId;
        private final int trainerId;
        private final int membershipId;
        private final LocalDate scheduleDate;
        private final LocalTime startTime;
        private final int duration;
        private final int roomId;
        private final String status;
        private final String notes;
        private final String roomName;
        private final String trainerName;
        private final String exercises;

        public TrainingSchedule(int scheduleId, int registrationId, int memberId, int trainerId,
                int membershipId, LocalDate scheduleDate, LocalTime startTime,
                int duration, int roomId, String status, String notes,
                String roomName, String trainerName, String exercises) {
            this.scheduleId = scheduleId;
            this.registrationId = registrationId;
            this.memberId = memberId;
            this.trainerId = trainerId;
            this.membershipId = membershipId;
            this.scheduleDate = scheduleDate;
            this.startTime = startTime;
            this.duration = duration;
            this.roomId = roomId;
            this.status = status;
            this.notes = notes;
            this.roomName = roomName;
            this.trainerName = trainerName;
            this.exercises = exercises;
        }

        // Getters
        public int getScheduleId() {
            return scheduleId;
        }

        public int getRegistrationId() {
            return registrationId;
        }

        public int getMemberId() {
            return memberId;
        }

        public int getTrainerId() {
            return trainerId;
        }

        public int getMembershipId() {
            return membershipId;
        }

        public LocalDate getScheduleDate() {
            return scheduleDate;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public int getDuration() {
            return duration;
        }

        public int getRoomId() {
            return roomId;
        }

        public String getStatus() {
            return status;
        }

        public String getNotes() {
            return notes;
        }

        public String getRoomName() {
            return roomName;
        }

        public String getTrainerName() {
            return trainerName;
        }

        public String getExercises() {
            return exercises;
        }
    }
}