package model;

public class EquipmentType {
    private int equipmentTypeId;
    private String equipmentName;
    private String description;
    private String status;

    public EquipmentType() {
    }

    public EquipmentType(int equipmentTypeId, String equipmentName, String description) {
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.description = description;
    }

    // Getters and Setters
    public int getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(int equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
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
}