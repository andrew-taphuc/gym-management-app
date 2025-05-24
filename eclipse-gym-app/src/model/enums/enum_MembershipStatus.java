package model.enums;

public enum enum_MembershipStatus {
    ACTIVE("Hoạt động"),
    EXPIRED("Hết hạn"),
    INACTIVE("Chưa kích hoạt");

    private final String value;

    enum_MembershipStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_MembershipStatus fromValue(String value) {
        for (enum_MembershipStatus status : enum_MembershipStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown membership status value: " + value);
    }
}