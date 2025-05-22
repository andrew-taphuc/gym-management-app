package model.enums;

public enum enum_TrainerSpecialization {
    GYM("Gym"),
    YOGA("Yoga"),
    KICKFIT("Kickfit");

    private final String value;

    enum_TrainerSpecialization(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}