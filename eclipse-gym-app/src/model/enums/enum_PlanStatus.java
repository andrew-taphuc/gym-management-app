package model.enums;

public enum enum_PlanStatus {
    ACTIVE("Hoạt động"),
    UPDATED("Đã cập nhật"),
    DELETED("Đã xóa");

    private final String value;

    enum_PlanStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_PlanStatus fromValue(String value) {
        for (enum_PlanStatus status : enum_PlanStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown plan status value: " + value);
    }
}