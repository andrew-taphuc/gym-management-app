package model.enums;

public enum enum_EquipmentType {
    CARDIO("Cardio"),
    STRENGTH("Strength"),
    FLEXIBILITY("Flexibility"),
    RECOVERY("Recovery");

    private final String value;

    enum_EquipmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_EquipmentType fromValue(String value) {
        for (enum_EquipmentType type : enum_EquipmentType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown equipment type value: " + value);
    }
}