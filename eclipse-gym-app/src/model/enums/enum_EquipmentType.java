package model.enums;

public enum enum_EquipmentType {
    MAY_CHAY_BO("Máy chạy bộ"),
    XE_DAP_TAP("Xe đạp tập"),
    MAY_TAP_TA("Máy tập tạ"),
    GHE_TAP("Ghế tập"),
    MAY_KEO_XA("Máy kéo xà"),
    MAY_EP_NGU("Máy ép ngực"),
    MAY_EP_LE("Máy ép lưng"),
    MAY_EP_BA_TU("Máy ép bụng"),
    MAY_KEO_CAP("Máy kéo cáp"),
    MAY_TAP_TAY("Máy tập tay"),
    MAY_TAP_CHAN("Máy tập chân"),
    MAY_TAP_VAI("Máy tập vai"),
    MAY_TAP_LUNG("Máy tập lưng"),
    MAY_TAP_BUNG("Máy tập bụng"),
    MAY_TAP_MONG("Máy tập mông"),
    MAY_TAP_DUI("Máy tập đùi"),
    MAY_TAP_BAP_CHUOI("Máy tập bắp chuối"),
    MAY_TAP_CO("Máy tập cổ"),
    MAY_TAP_LUNG_DUOI("Máy tập lưng dưới"),
    MAY_TAP_LUNG_TREN("Máy tập lưng trên");

    private final String value;

    enum_EquipmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}