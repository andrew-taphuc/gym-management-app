package model.enums;

public enum enum_Role {
    OWNER("Chủ phòng tập"),
    MANAGER("Nhân viên quản lý"),
    TRAINER("Huấn luyện viên"),
    MEMBER("Hội viên");

    private final String value;

    enum_Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_Role fromValue(String value) {
        for (enum_Role role : enum_Role.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }
}