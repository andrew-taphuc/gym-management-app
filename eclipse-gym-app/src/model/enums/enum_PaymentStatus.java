package model.enums;

public enum enum_PaymentStatus {
    PENDING("Chờ xử lý"),
    COMPLETED("Hoàn thành"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền");

    private final String value;

    enum_PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static enum_PaymentStatus fromValue(String value) {
        for (enum_PaymentStatus status : enum_PaymentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status value: " + value);
    }
}