package model.enums;

public enum enum_UserStatus {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa"),
    TAM_NGUNG("Tạm ngừng");

    private final String value;

    enum_UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}