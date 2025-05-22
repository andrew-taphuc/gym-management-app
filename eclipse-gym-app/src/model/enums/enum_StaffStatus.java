package model.enums;

public enum enum_StaffStatus {
    ACTIVE("Hoạt động"),
    ON_LEAVE("Nghỉ phép"),
    TERMINATED("Đã nghỉ việc");

    private final String value;

    enum_StaffStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_StaffStatus fromValue(String value) {
        for (enum_StaffStatus status : enum_StaffStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown staff status value: " + value);
    }
}