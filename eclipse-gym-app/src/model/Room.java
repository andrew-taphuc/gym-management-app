package model;

import java.time.LocalDateTime;

public class Room {
    private int roomId;
    private String roomCode;
    private String roomName;
    private String roomType;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int centerId;
    private FitnessCenter center; // Reference to FitnessCenter

    public Room() {
    }

    public Room(int roomId, String roomCode, String roomName, String roomType,
            String description, String status, LocalDateTime createdAt,
            LocalDateTime updatedAt, int centerId) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.roomType = roomType;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.centerId = centerId;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public FitnessCenter getCenter() {
        return center;
    }

    public void setCenter(FitnessCenter center) {
        this.center = center;
    }
}