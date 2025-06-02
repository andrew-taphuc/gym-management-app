-- Dữ liệu mẫu cho bảng Users
INSERT INTO Users (Username, Password, Email, PhoneNumber, FullName, DateOfBirth, Gender, Address, CreatedAt, UpdateAt, Status, Role) VALUES

('admin', '1234', 'admin@gym.com', '0123456789', 'Tô Át Min', '1990-01-01', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Chủ phòng tập'),

('mana1', '1234', 'mana1@gym.com', '0123456781', 'Xuân Bắc', '1992-02-02', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Nhân viên quản lý'),

('pt1', '1234', 'pt1@gym.com', '0123456782', 'Tự Long', '1993-03-03', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Huấn luyện viên'),

('taphuc1', '1234', 'taphuc1@gym.com', '0123456783', 'Tạ Hồng Phúc', '2004-09-24', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),

('hungngoc', '1234', 'taphuc1@gym.com', '0123456783', 'Bùi Quang Hưng', '2004-08-26', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),

('tungdanchoi', '1234', 'taphuc1@gym.com', '0123456783', 'Nguyễn Tùng Béo', '2004-10-28', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),

('1', '1', 'member111@gym.com', '0123456783', 'Phạm Văn Member', '1995-05-05', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên');

-- Dữ liệu mẫu cho bảng Staff
INSERT INTO Staff (UserID, StaffCode, Status) VALUES
(2, 'STAFF001', 'Đang làm việc');

-- Dữ liệu mẫu cho bảng Trainers
INSERT INTO Trainers (UserID, TrainerCode, Specialization, Bio, Rating, Status) VALUES
(3, 'TRAINER001', 'Gym', 'Huấn luyện viên chuyên nghiệp với 5 năm kinh nghiệm', 4.5, 'Đang làm việc');

-- Dữ liệu mẫu cho bảng Members
INSERT INTO Members (UserID, MemberCode, JoinDate, Status) VALUES
(5, 'HV0001', CURRENT_DATE, 'Hoạt động');

-- Dữ liệu mẫu cho bảng FitnessCenter
INSERT INTO FitnessCenter (CenterName, Address, PhoneNumber) VALUES
('Gym Center Hà Nội', '123 Đường ABC, Quận XYZ, Hà Nội', '02412345678');

-- Dữ liệu mẫu cho bảng Rooms
INSERT INTO Rooms (RoomCode, RoomName, RoomType, Description, Status, CenterID) VALUES
('ROOM001', 'Phòng Gym Chính', 'gym', 'Phòng tập chính với đầy đủ thiết bị', 'Hoạt động', 1),
('ROOM002', 'Phòng Yoga', 'yoga', 'Phòng tập yoga rộng rãi', 'Hoạt động', 1);

-- Dữ liệu mẫu cho bảng EquipmentTypes
INSERT INTO EquipmentTypes (EquipmentName, Description) VALUES
('Máy chạy bộ', 'Máy chạy bộ điện tử'),
('Xe đạp tập', 'Xe đạp tập thể dục'),
('Máy tập tạ', 'Máy tập tạ đa năng');

-- Dữ liệu mẫu cho bảng RoomEquipment
INSERT INTO RoomEquipment (RoomID, EquipmentTypeID, EquipmentCode, Quantity, Status, Description, CenterID) VALUES
(1, 1, 'EQ001', 5, 'Hoạt động', 'Máy chạy bộ mới', 1),
(1, 2, 'EQ002', 3, 'Hoạt động', 'Xe đạp tập mới', 1),
(1, 3, 'EQ003', 2, 'Hoạt động', 'Máy tập tạ mới', 1);

-- Dữ liệu mẫu cho bảng MembershipPlans
INSERT INTO MembershipPlans (PlanCode, PlanName, Duration, Price, Description) VALUES
('PLAN001', 'Gói 1 tháng', 30, 500000, 'Gói tập 1 tháng'),
('PLAN002', 'Gói 3 tháng', 90, 1400000, 'Gói tập 3 tháng'),
('PLAN003', 'Gói 6 tháng', 180, 2600000, 'Gói tập 6 tháng');

-- Dữ liệu mẫu cho bảng TrainingPlans
INSERT INTO TrainingPlans (PlanCode, PlanName, Type, SessionAmount, Price, Description) VALUES
('TPLAN001', 'Gói 10 buổi', 'Gym', 10, 2000000, 'Gói tập với HLV 10 buổi'),
('TPLAN002', 'Gói 20 buổi', 'Gym', 20, 3800000, 'Gói tập với HLV 20 buổi');

-- Dữ liệu mẫu cho bảng Payments
INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, Status, Notes) VALUES
(500000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 1 tháng'),
(2000000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập với HLV');

-- Dữ liệu mẫu cho bảng Memberships
INSERT INTO Memberships (MemberID, UserID, PlanID, StartDate, EndDate, Status, PaymentID) VALUES
(1, 1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', 'Chưa kích hoạt', 1);

-- Dữ liệu mẫu cho bảng TrainingRegistrations
INSERT INTO TrainingRegistrations (MemberID, PlanID, TrainerID, StartDate, SessionsLeft, PaymentID) VALUES
(1, 1, 1, CURRENT_DATE, 10, 2);

-- Dữ liệu mẫu cho bảng MembershipRenewals
INSERT INTO MembershipRenewals (MembershipID, NewEndDate, RenewalDate, PaymentID, Notes) VALUES
(1, CURRENT_DATE + INTERVAL '30 days', CURRENT_TIMESTAMP, 1, 'Gia hạn gói tập 1 tháng');

-- Dữ liệu mẫu cho bảng Attendance
INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, Type) VALUES
(1, 1, CURRENT_TIMESTAMP - INTERVAL '2 days', 'Gym'),
(1, 1, CURRENT_TIMESTAMP - INTERVAL '1 days', 'Gym'),
(1, 1, CURRENT_TIMESTAMP, 'Gym');

-- Dữ liệu mẫu cho bảng TrainingSchedule
INSERT INTO TrainingSchedule (MemberID, TrainerID, MembershipID, ScheduleDate, StartTime, Duration, RoomID, Status, Notes, CreatedDate) VALUES
(1, 1, 1, CURRENT_DATE, '08:00', 60, 1, 'Đã lên lịch', 'Buổi tập sáng', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(1, 1, 1, CURRENT_DATE + INTERVAL '1 days', '09:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP);

-- Dữ liệu mẫu cho bảng Exercises
INSERT INTO Exercises (ExerciseCode, ExerciseName, Category, Description) VALUES
('EX001', 'Đẩy ngực', 'Gym', 'Bài tập ngực cơ bản'),
('EX002', 'Squat', 'Gym', 'Bài tập chân cơ bản'),
('EX003', 'Plank', 'Yoga', 'Bài tập core');

-- Dữ liệu mẫu cho bảng TrainingScheduleExercises
INSERT INTO TrainingScheduleExercises (ScheduleID, ExerciseID, Comment) VALUES
(1, 1, 'Tập tốt'),
(1, 2, 'Cần cải thiện kỹ thuật'),
(2, 3, 'Giữ tư thế đúng');

-- Dữ liệu mẫu cho bảng MemberProgress
INSERT INTO MemberProgress (MemberID, MeasurementDate, Weight, Height, BMI, BodyFatPercentage, Chest, Waist, Hip, Biceps, Thigh, TrainerID, Notes) VALUES
(1, CURRENT_DATE - INTERVAL '7 days', 70.5, 175.0, 23.0, 15.0, 95.0, 80.0, 95.0, 35.0, 55.0, 1, 'Khởi đầu'),
(1, CURRENT_DATE, 69.8, 175.0, 22.8, 14.5, 96.0, 79.0, 96.0, 36.0, 56.0, 1, 'Tiến bộ tốt'); 




INSERT INTO TrainingSchedule (MemberID, TrainerID, MembershipID, ScheduleDate, StartTime, Duration, RoomID, Status, Notes, CreatedDate) VALUES
(1, 1, 1, CURRENT_DATE + INTERVAL '2 days', '08:00', 60, 1, 'Đã lên lịch', 'Buổi tập sáng', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(1, 1, 1, CURRENT_DATE + INTERVAL '3 days', '09:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '4 days', '19:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '5 days', '07:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '6 days', '14:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '7 days', '17:50', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '8 days', '10:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '9 days', '16:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '10 days', '18:40', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '11 days', '09:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP),
(1, 1, 1, CURRENT_DATE + INTERVAL '12 days', '09:00', 60, 1, 'Đã lên lịch', 'Buổi tập tiếp theo', CURRENT_TIMESTAMP);


INSERT INTO Promotions (
  PromotionCode, PromotionName, Description, DiscountType, DiscountValue,
  StartDate, EndDate, Status
) VALUES
-- 2 mã hết hạn
('KM2501', 'Giảm giá Tết', 'Khuyến mại dịp Tết Nguyên Đán', 'Phần trăm', 10.00,
 '2025-01-01 00:00:00', '2025-01-10 23:59:59', 'Hết hạn'),

('KM2502', 'Mở cửa tháng 3', 'Ưu đãi dành cho thành viên mới', 'Tiền mặt', 50000.00,
 '2025-03-01 00:00:00', '2025-03-31 23:59:59', 'Hết hạn'),

-- 10 mã còn hạn
('KM2503', 'Hè sôi động', 'Giảm giá cho tất cả gói tập trong hè', 'Phần trăm', 15.00,
 '2025-05-15 00:00:00', '2025-08-31 23:59:59', 'Còn hạn'),

('KM2504', 'Khuyến mãi ngày lễ', 'Ưu đãi đặc biệt cho ngày Quốc khánh', 'Tiền mặt', 100000.00,
 '2025-05-01 00:00:00', '2025-09-02 23:59:59', 'Còn hạn'),

('KM2505', 'Mừng sinh nhật trung tâm', 'Giảm giá toàn bộ dịch vụ', 'Phần trăm', 20.00,
 '2025-06-01 00:00:00', '2025-12-07 23:59:59', 'Còn hạn'),

('KM2506', 'Tặng bạn bè', 'Giảm giá khi giới thiệu bạn bè đăng ký', 'Phần trăm', 5.00,
 '2025-06-01 00:00:00', '2025-06-30 23:59:59', 'Còn hạn'),

('KM2507', 'Ưu đãi mùa xuân', 'Khuyến mãi đầu năm cho hội viên cũ', 'Tiền mặt', 30000.00,
 '2025-06-01 00:00:00', '2025-06-15 23:59:59', 'Còn hạn'),

('KM2508', 'Giảm giá combo', 'Giảm giá khi mua nhiều gói tập cùng lúc', 'Phần trăm', 12.00,
 '2025-05-20 00:00:00', '2025-06-30 23:59:59', 'Còn hạn'),

('KM2509', 'Ưu đãi VIP', 'Khuyến mãi dành riêng cho hội viên VIP', 'Tiền mặt', 150000.00,
 '2025-06-01 00:00:00', '2025-07-15 23:59:59', 'Còn hạn'),

('KM2510', 'Flash Sale', 'Chương trình giảm giá bất ngờ 1 ngày', 'Phần trăm', 25.00,
 '2025-06-03 00:00:00', '2025-06-03 23:59:59', 'Còn hạn'),

('KM2511', 'Giảm giá buổi tối', 'Giảm giá cho các buổi tập sau 19h', 'Phần trăm', 10.00,
 '2025-05-01 00:00:00', '2025-06-30 23:59:59', 'Còn hạn'),

-- 3 mã chưa khả dụng
('KM2512', 'Back to School', 'Khuyến mãi cho sinh viên nhập học mới', 'Tiền mặt', 40000.00,
 '2025-09-10 00:00:00', '2025-09-30 23:59:59', 'Chưa khả dụng'),

('KM2513', 'Ngày hội sức khỏe', 'Ưu đãi dịp tổ chức hội thao', 'Phần trăm', 18.00,
 '2025-10-05 00:00:00', '2025-10-10 23:59:59', 'Chưa khả dụng'),

('KM2514', 'Black Friday', 'Khuyến mãi khủng nhất năm', 'Tiền mặt', 200000.00,
 '2025-11-29 00:00:00', '2025-11-29 23:59:59', 'Chưa khả dụng');

INSERT INTO Promotions (
  PromotionCode, PromotionName, Description, DiscountType, DiscountValue,
  StartDate, EndDate, Status
) VALUES
-- 5 mã hết hạn
('KM2515', 'Ưu đãi đầu năm 2024', 'Khuyến mãi mở đầu năm 2024', 'Phần trăm', 10.00,
 '2024-01-01 00:00:00', '2024-01-31 23:59:59', 'Hết hạn'),

('KM2516', 'Tháng thanh toán', 'Ưu đãi khi thanh toán theo năm', 'Tiền mặt', 100000.00,
 '2024-02-01 00:00:00', '2024-02-28 23:59:59', 'Hết hạn'),

('KM2517', 'Mừng Quốc tế phụ nữ', 'Khuyến mãi cho hội viên nữ', 'Phần trăm', 20.00,
 '2024-03-08 00:00:00', '2024-03-08 23:59:59', 'Hết hạn'),

('KM2518', 'Ưu đãi cuối xuân', 'Khuyến mãi gói tập cuối mùa xuân', 'Tiền mặt', 40000.00,
 '2024-04-15 00:00:00', '2024-04-30 23:59:59', 'Hết hạn'),

('KM2519', 'Khuyến mãi 30/4', 'Giảm giá nhân dịp lễ 30/4', 'Phần trăm', 15.00,
 '2024-04-30 00:00:00', '2024-04-30 23:59:59', 'Hết hạn'),

-- 10 mã còn hạn
('KM2520', 'Ưu đãi tháng 6', 'Khuyến mãi đặc biệt tháng 6', 'Tiền mặt', 30000.00,
 '2025-06-01 00:00:00', '2025-06-30 23:59:59', 'Còn hạn'),

('KM2521', 'Summer Sale', 'Ưu đãi hè rực rỡ', 'Phần trăm', 18.00,
 '2025-05-20 00:00:00', '2025-08-31 23:59:59', 'Còn hạn'),

('KM2522', 'Đồng hành cùng bạn', 'Ưu đãi khi đăng ký 2 người', 'Tiền mặt', 50000.00,
 '2025-06-01 00:00:00', '2025-07-31 23:59:59', 'Còn hạn'),

('KM2523', 'Giảm giá cuối tuần', 'Khuyến mãi áp dụng cuối tuần', 'Phần trăm', 12.00,
 '2025-05-01 00:00:00', '2025-09-30 23:59:59', 'Còn hạn'),

('KM2524', 'Giảm giá cho sinh viên', 'Ưu đãi cho sinh viên có thẻ SV', 'Tiền mặt', 45000.00,
 '2025-06-01 00:00:00', '2025-12-31 23:59:59', 'Còn hạn'),

('KM2525', 'Khuyến mãi mùa thi', 'Giảm giá cho học sinh/sinh viên mùa thi', 'Phần trăm', 10.00,
 '2025-05-20 00:00:00', '2025-06-20 23:59:59', 'Còn hạn'),

('KM2526', 'Ngày hội thể thao', 'Ưu đãi nhân ngày hội thể thao', 'Phần trăm', 15.00,
 '2025-06-02 00:00:00', '2025-06-15 23:59:59', 'Còn hạn'),

('KM2527', 'Ưu đãi ngày thứ 2', 'Giảm giá vào thứ 2 hàng tuần', 'Tiền mặt', 20000.00,
 '2025-06-01 00:00:00', '2025-07-01 23:59:59', 'Còn hạn'),

('KM2528', 'Tập thử miễn phí', 'Tặng gói tập thử 1 ngày', 'Tiền mặt', 0.00,
 '2025-05-01 00:00:00', '2025-12-31 23:59:59', 'Còn hạn'),

('KM2529', 'Ưu đãi tháng 7', 'Khuyến mãi mùa hè tháng 7', 'Phần trăm', 10.00,
 '2025-07-01 00:00:00', '2025-07-31 23:59:59', 'Còn hạn'),

-- 5 mã chưa khả dụng
('KM2530', 'Giảm giá cuối năm', 'Ưu đãi hấp dẫn tháng 12', 'Tiền mặt', 100000.00,
 '2025-12-01 00:00:00', '2025-12-31 23:59:59', 'Chưa khả dụng'),

('KM2531', 'Tết Dương Lịch', 'Ưu đãi đón năm mới 2026', 'Phần trăm', 20.00,
 '2025-12-30 00:00:00', '2026-01-02 23:59:59', 'Chưa khả dụng'),

('KM2532', 'Back to Gym', 'Ưu đãi đầu năm sau Tết', 'Phần trăm', 15.00,
 '2026-01-10 00:00:00', '2026-01-31 23:59:59', 'Chưa khả dụng'),

('KM2533', 'Tháng sức khỏe', 'Khuyến mãi khuyến khích luyện tập', 'Tiền mặt', 35000.00,
 '2025-10-01 00:00:00', '2025-10-31 23:59:59', 'Chưa khả dụng'),

('KM2534', 'Ngày của mẹ', 'Ưu đãi cho hội viên nữ dịp 20/10', 'Phần trăm', 22.00,
 '2025-10-20 00:00:00', '2025-10-20 23:59:59', 'Chưa khả dụng');
