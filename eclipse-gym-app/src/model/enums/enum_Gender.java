package model.enums;

public enum enum_Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác");

    private final String value;

    enum_Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_Gender fromValue(String value) {
        for (enum_Gender gender : enum_Gender.values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender value: " + value);
    }
}