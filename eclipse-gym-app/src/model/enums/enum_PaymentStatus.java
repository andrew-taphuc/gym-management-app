package model.enums;

public enum enum_PaymentStatus {
    THANH_CONG("Thành công"),
    THAT_BAI("Thất bại"),
    DANG_XU_LY("Đang xử lý"),
    HOAN_TIEN("Hoàn tiền");

    private final String value;

    enum_PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}