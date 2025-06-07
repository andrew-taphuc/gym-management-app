package model.enums;

public enum enum_EquipmentStatus {
    ACTIVE("Hoạt động"),
    MAINTENANCE("Bảo trì");

    private final String value;

    enum_EquipmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_EquipmentStatus fromValue(String value) {
        for (enum_EquipmentStatus status : enum_EquipmentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown equipment status value: " + value);
    }
}