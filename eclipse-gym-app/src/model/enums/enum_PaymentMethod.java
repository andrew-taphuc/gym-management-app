package model.enums;

public enum enum_PaymentMethod {
    CASH("Tiền mặt"),
    CREDIT_CARD("Thẻ tín dụng"),
    BANK_TRANSFER("Chuyển khoản"),
    E_WALLET("Ví điện tử");

    private final String value;

    enum_PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_PaymentMethod fromValue(String value) {
        for (enum_PaymentMethod method : enum_PaymentMethod.values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method value: " + value);
    }
}