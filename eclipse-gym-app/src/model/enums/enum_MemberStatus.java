package model.enums;

public enum enum_MemberStatus {
    HOAT_DONG("Hoạt động"),
    TAM_NGUNG("Tạm ngừng"),
    HET_HAN("Hết hạn");

    private final String value;

    enum_MemberStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}