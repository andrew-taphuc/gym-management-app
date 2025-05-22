package model.enums;

public enum enum_UserStatus {
    ACTIVE("Hoạt động"),
    LOCKED("Khóa"),
    SUSPENDED("Tạm ngừng");

    private final String value;

    enum_UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_UserStatus fromValue(String value) {
        for (enum_UserStatus status : enum_UserStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown user status value: " + value);
    }
}