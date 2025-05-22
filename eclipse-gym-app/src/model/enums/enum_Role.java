package model.enums;

public enum enum_Role {
    CHU_PHONG_TAP("Chủ phòng tập"),
    NHAN_VIEN_QUAN_LY("Nhân viên quản lý"),
    HUAN_LUYEN_VIEN("Huấn luyện viên"),
    HOI_VIEN("Hội viên");

    private final String value;

    enum_Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}