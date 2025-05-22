package model.enums;

public enum enum_Gender {
    NAM("Nam"),
    NU("Nữ"),
    KHAC("Khác");

    private final String value;

    enum_Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}