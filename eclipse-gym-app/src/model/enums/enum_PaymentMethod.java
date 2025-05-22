package model.enums;

public enum enum_PaymentMethod {
    TIEN_MAT("Tiền mặt"),
    THE_NGAN_HANG("Thẻ ngân hàng"),
    VI_DIEN_TU("Ví điện tử"),
    CHUYEN_KHOAN("Chuyển khoản");

    private final String value;

    enum_PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}