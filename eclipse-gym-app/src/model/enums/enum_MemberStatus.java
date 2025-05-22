package model.enums;

public enum enum_MemberStatus {
    ACTIVE("Hoạt động"),
    EXPIRED("Hết hạn"),
    SUSPENDED("Tạm ngừng");

    private final String value;

    enum_MemberStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_MemberStatus fromValue(String value) {
        for (enum_MemberStatus status : enum_MemberStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown member status value: " + value);
    }
}