package model.enums;

public enum enum_MembershipStatus {
    HOAT_DONG("Hoạt động"),
    HET_HAN("Hết hạn");

    private final String value;

    enum_MembershipStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}