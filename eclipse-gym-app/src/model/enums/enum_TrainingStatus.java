package model.enums;

public enum enum_TrainingStatus {
    SCHEDULED("Đã lên lịch"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Hủy");

    private final String value;

    enum_TrainingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_TrainingStatus fromValue(String value) {
        for (enum_TrainingStatus status : enum_TrainingStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown training status value: " + value);
    }
}