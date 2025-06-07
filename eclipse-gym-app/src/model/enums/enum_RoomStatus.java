package model.enums;

public enum enum_RoomStatus {
    ACTIVE("Hoạt động"),
    MAINTENANCE("Bảo trì"),
    SUSPENDED("Tạm ngừng");

    private final String value;

    enum_RoomStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_RoomStatus fromValue(String value) {
        for (enum_RoomStatus status : enum_RoomStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown room status value: " + value);
    }
}