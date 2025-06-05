package model.enums;

public enum enum_TrainerStatus {
    WORKING("Đang làm việc"),
    BUSY("Bận"),
    ON_LEAVE("Nghỉ phép"),
    TERMINATED("Đã nghỉ việc");

    private final String value;

    enum_TrainerStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_TrainerStatus fromValue(String value) {
        for (enum_TrainerStatus status : enum_TrainerStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown trainer status value: " + value);
    }
}