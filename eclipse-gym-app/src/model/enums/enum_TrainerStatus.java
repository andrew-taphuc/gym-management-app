package model.enums;

public enum enum_TrainerStatus {
    DANG_LAM_VIEC("Đang làm việc"),
    BAN("Bận"),
    NGHI_PHEP("Nghỉ phép"),
    DA_NGHI_VIEC("Đã nghỉ việc");

    private final String value;

    enum_TrainerStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}