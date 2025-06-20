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

    public static enum_TrainerSpecialization fromValue(String value) {
        for (enum_TrainerSpecialization specialization : enum_TrainerSpecialization.values()) {
            if (specialization.value.equals(value)) {
                return specialization;
            }
        }
        throw new IllegalArgumentException("Unknown trainer specialization value: " + value);
    }
}