# Gym Management System

Hệ thống quản lý phòng tập gym được phát triển bằng JavaFX và PostgreSQL.

## Tính năng

- Đăng nhập/Đăng xuất
- Quản lý thông tin người dùng
- Xem thông tin cá nhân
- Phân quyền người dùng (Admin, Staff, Trainer, Member)

## Yêu cầu hệ thống

- Java JDK 17 trở lên
- PostgreSQL 12 trở lên
- Maven

## Cài đặt

1. Clone repository:

```bash
git clone [repository-url]
```

2. Cấu hình database:

- Tạo database PostgreSQL
- Import file `db.sql` vào database

3. Cấu hình kết nối database:

- Mở file `src/utils/DatabaseConnection.java`
- Cập nhật thông tin kết nối:
  - URL
  - Username
  - Password

4. Build và chạy:

```bash
mvn clean install
mvn javafx:run
```

## Tài khoản mẫu

- Admin: admin/admin123
- Staff: staff/staff123
- Trainer: trainer/trainer123
- Member: member/member123

## Cấu trúc dự án

```
src/
├── controller/    # Xử lý logic nghiệp vụ
├── model/        # Định nghĩa các entity
├── utils/        # Tiện ích và helper
└── view/         # Giao diện người dùng
```

## Đóng góp

Mọi đóng góp đều được hoan nghênh. Vui lòng tạo issue hoặc pull request để đóng góp.
