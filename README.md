# Gym Management App

Ứng dụng quản lý phòng gym với giao diện JavaFX, hỗ trợ nhiều vai trò người dùng (Member, Trainer, Manager).

## Chức năng chính

- Đăng ký, đăng nhập, quản lý tài khoản
- Quản lý gói tập, thanh toán, lịch sử tập luyện
- Quản lý khuyến mãi, phản hồi, thông tin cá nhân
- Giao diện riêng cho từng vai trò: Member, Trainer, Manager

## Cấu trúc thư mục

```
├── src/
│   ├── view/
│   │   ├── userView/         # Giao diện & controller cho Member
│   │   ├── trainerView/      # Giao diện & controller cho Trainer
│   │   ├── adminView/        # Giao diện & controller cho Manager
│   │   └── ...
│   ├── controller/           # Controller logic
│   ├── model/                # Model dữ liệu
│   ├── utils/                # Tiện ích, helper
│   └── Main.java             # Điểm khởi động ứng dụng
```

## Hướng dẫn cài đặt & chạy

1. **Yêu cầu:**
   - Java 17 trở lên
   - JavaFX SDK
2. **Chạy ứng dụng:**
   - Mở project trong IDE (Eclipse/IntelliJ/VSCode)
   - Thiết lập VM options để include JavaFX (ví dụ: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`)
   - Chạy file `Main.java`

## Hướng dẫn sử dụng

- **Đăng ký:** Người dùng mới đăng ký, thanh toán gói tập, sau đó đăng nhập.
- **Đăng nhập:**
  - Member: truy cập các chức năng tập luyện, phản hồi, xem thông tin cá nhân.
  - Trainer: quản lý lịch tập, xem phản hồi, quản lý học viên.
  - Manager: quản lý toàn bộ hệ thống, gói tập, khuyến mãi, tài khoản.
- **Chuyển trang:** Menu bar hiển thị các chức năng phù hợp với từng vai trò.

## Một số lưu ý

- File thẻ tín dụng mẫu: `CREDIT_CARD.txt` (dùng để test thanh toán)
- Style giao diện được định nghĩa trong `view/style.css` và `view/dialog.css`
- Để thay đổi giao diện từng vai trò, chỉnh sửa trong các thư mục `userView`, `trainerView`, `adminView`.

## Đóng góp & phát triển

- Fork, tạo branch mới và gửi pull request nếu muốn đóng góp thêm tính năng.
- Mọi ý kiến đóng góp vui lòng gửi về email hoặc issue trên repository.

---

Chúc bạn sử dụng ứng dụng Gym Management App hiệu quả!
