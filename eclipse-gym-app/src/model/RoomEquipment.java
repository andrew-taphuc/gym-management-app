package model;
import model.enums.enum_EquipmentType;
import java.time.LocalDateTime;

public class RoomEquipment {
    private int roomEquipmentId;
    private int roomId;
    private int equipmentTypeId;
    private String equipmentCode;
    private int quantity;
    private String status;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private int centerId;
    private Room room; // Reference to Room
    private enum_EquipmentType equipmentType; // Reference to EquipmentType
    private FitnessCenter center; // Reference to FitnessCenter

    public RoomEquipment() {
    }

    public RoomEquipment(int roomEquipmentId, int roomId, int equipmentTypeId,
            String equipmentCode, int quantity, String status,
            String description, LocalDateTime createdDate,
            LocalDateTime updatedDate, int centerId) {
        this.roomEquipmentId = roomEquipmentId;
        this.roomId = roomId;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentCode = equipmentCode;
        this.quantity = quantity;
        this.status = status;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.centerId = centerId;
    }

    // Getters and Setters
    public int getRoomEquipmentId() {
        return roomEquipmentId;
    }

    public void setRoomEquipmentId(int roomEquipmentId) {
        this.roomEquipmentId = roomEquipmentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(int equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public enum_EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(enum_EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public FitnessCenter getCenter() {
        return center;
    }

    public void setCenter(FitnessCenter center) {
        this.center = center;
    }
}