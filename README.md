# Gym Management App

Ứng dụng quản lý phòng gym toàn diện được phát triển bằng JavaFX, hỗ trợ đa vai trò người dùng (Member, Trainer, Manager) với các chức năng chuyên biệt cho từng đối tượng.

## 🌟 Tính năng nổi bật

### 👤 Đối với Hội viên (Member)

- Đăng ký tài khoản và thanh toán gói tập online
- Quản lý thông tin cá nhân và lịch sử tập luyện
- Đặt lịch tập với huấn luyện viên (PT)
- Xem lịch sử điểm danh và theo dõi tiến độ
- Đăng ký tham gia các khuyến mãi
- Gửi phản hồi và đánh giá chất lượng dịch vụ

### 🏋️‍♂️ Đối với Huấn luyện viên (Trainer)

- Quản lý danh sách hội viên được phân công
- Tạo và quản lý lịch tập cho từng hội viên
- Theo dõi tiến độ tập luyện của hội viên
- Nhận phản hồi và đánh giá từ hội viên
- Quản lý thông tin cá nhân và lịch làm việc

### 👨‍💼 Đối với Quản lý (Manager)

- Quản lý toàn bộ hệ thống và người dùng
- Thêm/sửa/xóa gói tập và khuyến mãi
- Quản lý thanh toán và hóa đơn
- Phân công huấn luyện viên cho hội viên
- Xem báo cáo thống kê và doanh thu
- Quản lý phòng tập và thiết bị

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ:** Java 17
- **Framework UI:** JavaFX
- **Cơ sở dữ liệu:** PostgreSQL
- **IDE:** Eclipse/IntelliJ IDEA/VSCode
- **Build Tool:** Maven
- **Version Control:** Git

## 📁 Cấu trúc dự án

```
gym-management-app/
├── eclipse-gym-app/              # Thư mục chính của dự án
│   ├── src/
│   │   ├── view/                # Giao diện người dùng
│   │   │   ├── userView/       # Giao diện cho Member
│   │   │   ├── trainerView/    # Giao diện cho Trainer
│   │   │   ├── adminView/      # Giao diện cho Manager
│   │   │   ├── style.css       # CSS chung
│   │   │   └── dialog.css      # CSS cho dialog
│   │   ├── controller/         # Xử lý logic nghiệp vụ
│   │   ├── model/             # Định nghĩa cấu trúc dữ liệu
│   │   ├── utils/             # Tiện ích và helper
│   │   └── Main.java          # Điểm khởi động ứng dụng
│   └── pom.xml                # Cấu hình Maven
├── DB.sql                     # Script tạo cơ sở dữ liệu
├── DB_data.sql               # Dữ liệu mẫu
├── DB_function.sql           # Các hàm database
└── CREDIT_CARD.txt          # Thông tin thẻ tín dụng mẫu
```

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

- Java JDK 17 trở lên
- JavaFX SDK 17 trở lên
- PostgreSQL 12 trở lên
- IDE (Eclipse/IntelliJ IDEA/VSCode)

### Các bước cài đặt

1. **Clone repository:**

   ```bash
   git clone https://github.com/andrew-taphuc/gym-management-app.git
   cd gym-management-app
   ```

2. **Cài đặt cơ sở dữ liệu:**

   - Tạo database mới trong PostgreSQL
   - Chạy lần lượt các file:
     ```bash
     psql -U your_username -d your_database -f DB.sql
     psql -U your_username -d your_database -f DB_function.sql
     psql -U your_username -d your_database -f DB_data.sql
     ```

3. **Cấu hình IDE:**

   - Mở project trong IDE
   - Cấu hình JavaFX SDK trong VM options:
     ```
     --module-path /path/to/javafx-sdk/lib
     --add-modules javafx.controls,javafx.fxml
     ```
   - Cập nhật thông tin kết nối database trong `utils/DBConnection.java`

4. **Chạy ứng dụng:**
   - Chạy file `Main.java`
   - Đăng nhập với tài khoản mẫu:
     - Member: taphuc1/1234
     - Trainer: pt1/1234
     - Manager: mana1/1234
     - Owner: admin/1234

## 📝 Hướng dẫn sử dụng

### Đăng ký tài khoản mới

1. Chọn "Đăng ký" từ màn hình đăng nhập
2. Điền đầy đủ thông tin cá nhân
3. Chọn gói tập và thanh toán
4. Kích hoạt tài khoản qua email

### Quản lý gói tập

- **Member:** Xem thông tin gói tập, gia hạn, nâng cấp
- **Manager:** Thêm/sửa/xóa gói tập, quản lý giá

### Đặt lịch tập PT

1. Member chọn "Đặt lịch PT"
2. Chọn huấn luyện viên và thời gian
3. Xác nhận đặt lịch
4. Nhận thông báo xác nhận

### Quản lý điểm danh

- **Member:** Check-in qua QR code hoặc thẻ
- **Trainer:** Xác nhận buổi tập và ghi nhận tiến độ
- **Manager:** Theo dõi thống kê điểm danh

## 🔧 Bảo trì và phát triển

### Cập nhật database

- Backup dữ liệu trước khi cập nhật
- Chạy script cập nhật theo thứ tự
- Kiểm tra tính toàn vẹn dữ liệu

### Thêm tính năng mới

1. Tạo branch mới từ `develop`
2. Phát triển và test kỹ lưỡng
3. Tạo pull request để review
4. Merge vào `develop` sau khi được duyệt

### Báo lỗi

- Tạo issue với mô tả chi tiết
- Đính kèm log lỗi nếu có
- Cung cấp các bước để tái hiện lỗi

## 📚 Tài liệu tham khảo

### Tài liệu kỹ thuật

1. **Java & JavaFX**

   - [JavaFX Documentation](https://docs.oracle.com/javase/8/javafx/api/toc.htm)
   - [JavaFX CSS Reference Guide](https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html)
   - [JavaFX Scene Builder](https://gluonhq.com/products/scene-builder/)

2. **Database & PostgreSQL**

   - [PostgreSQL Documentation](https://www.postgresql.org/docs/)
   - [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
   - [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/)

3. **Design Patterns & Architecture**
   - [MVC Pattern in JavaFX](https://edencoding.com/mvc-pattern-javafx/)
   - [JavaFX Best Practices](https://docs.oracle.com/javafx/2/best_practices/jfxpub-best_practices.htm)
   - [Clean Architecture in Java](https://www.baeldung.com/java-clean-architecture)

### Tài liệu nghiệp vụ

1. **Quản lý phòng tập**

   - [Fitness Business Management](https://www.acefitness.org/fitness-certifications/group-fitness-certification/)
   - [Gym Management Best Practices](https://www.ihrsa.org/improve-your-club/business-operations/)
   - [Personal Training Management](https://www.acefitness.org/fitness-certifications/personal-trainer-certification/)

2. **Tiêu chuẩn & Quy định**

   - [ISO 9001:2015 - Quality Management Systems](https://www.iso.org/iso-9001-quality-management.html)
   - [GDPR Compliance for Fitness Apps](https://gdpr.eu/fitness-apps/)
   - [Payment Card Industry Data Security Standard (PCI DSS)](https://www.pcisecuritystandards.org/)

3. **Tài liệu SRS**
   - [IEEE 830-1998 - Recommended Practice for Software Requirements Specifications](https://standards.ieee.org/standard/830-1998.html)
   - [ISO/IEC/IEEE 29148:2018 - Systems and software engineering](https://www.iso.org/standard/72089.html)
   - [Software Requirements Specification Template](https://www.altexsoft.com/blog/business/software-requirements-specification-document-with-template/)

### Công cụ & Framework

1. **Development Tools**

   - [Eclipse IDE](https://www.eclipse.org/documentation/)
   - [IntelliJ IDEA](https://www.jetbrains.com/idea/documentation/)
   - [Visual Studio Code](https://code.visualstudio.com/docs)

2. **Version Control & Collaboration**

   - [Git Documentation](https://git-scm.com/doc)
   - [GitHub Guides](https://guides.github.com/)
   - [Git Flow Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

3. **Testing & Quality Assurance**
   - [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
   - [TestFX Documentation](https://testfx.github.io/TestFX/)
   - [SonarQube Documentation](https://docs.sonarqube.org/)

## 📄 Giấy phép

Dự án được phát triển dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.

## 👥 Đóng góp

Mọi đóng góp đều được hoan nghênh! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 👨‍💻 Cộng tác viên

Dự án được phát triển bởi:

- **Tạ Hồng Phúc** ([@andrew-taphuc](https://github.com/andrew-taphuc)) - Chủ dự án
- **Nguyễn Quang Hưng** ([@Gnuhq26](https://github.com/Gnuhq26)) - Collaborator
- **Nguyễn Mạnh Tùng** ([@nmtun](https://github.com/nmtun)) - Collaborator

## 📧 Liên hệ

- Email: taphuc1@gmail.com
- Issue: https://github.com/andrew-taphuc/gym-management-app/issues

---

Chúc bạn có trải nghiệm tốt với Gym Management App! 🎉
