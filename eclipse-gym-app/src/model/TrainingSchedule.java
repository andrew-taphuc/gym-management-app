package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import model.enums.enum_TrainingStatus;

public class TrainingSchedule {
    private int scheduleId;
    private int memberId;
    private int trainerId;
    private int membershipId;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private int duration;
    private int roomId;
    private enum_TrainingStatus status;
    private String notes;
    private LocalDateTime createdDate;

    public TrainingSchedule() {
    }

    public TrainingSchedule(int scheduleId, int memberId, int trainerId, int membershipId, LocalDate scheduleDate,
            LocalTime startTime, int duration, int roomId, enum_TrainingStatus status, String notes,
            LocalDateTime createdDate) {
        this.scheduleId = scheduleId;
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.membershipId = membershipId;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.duration = duration;
        this.roomId = roomId;
        this.status = status;
        this.notes = notes;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public enum_TrainingStatus getStatus() {
        return status;
    }

    public void setStatus(enum_TrainingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}