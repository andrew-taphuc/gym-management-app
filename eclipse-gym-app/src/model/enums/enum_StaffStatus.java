package model.enums;

public enum enum_StaffStatus {
    DANG_LAM_VIEC("Đang làm việc"),
    NGHI_PHEP("Nghỉ phép"),
    DA_NGHI_VIEC("Đã nghỉ việc");

    private final String value;

    enum_StaffStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}