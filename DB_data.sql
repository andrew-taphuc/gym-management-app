-- Dữ liệu mẫu cho bảng Users
INSERT INTO Users (Username, Password, Email, PhoneNumber, FullName, DateOfBirth, Gender, Address, CreatedAt, UpdateAt, Status, Role) VALUES
('admin', 'admin123', 'admin@gym.com', '0123456789', 'Nguyễn Văn Admin', '1990-01-01', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Chủ phòng tập'),
('staff1', 'staff123', 'staff1@gym.com', '0123456781', 'Trần Văn Staff', '1992-02-02', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Nhân viên quản lý'),
('trainer1', 'trainer123', 'trainer1@gym.com', '0123456782', 'Lê Văn Trainer', '1993-03-03', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Huấn luyện viên'),
('member1', 'member123', 'member1@gym.com', '0123456783', 'Phạm Văn Member', '1995-05-05', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên');

-- Dữ liệu mẫu cho bảng Staff
INSERT INTO Staff (UserID, StaffCode, Status) VALUES
(2, 'STAFF001', 'Đang làm việc');

-- Dữ liệu mẫu cho bảng Trainers
INSERT INTO Trainers (UserID, TrainerCode, Specialization, Bio, Rating, Status) VALUES
(3, 'TRAINER001', 'Gym', 'Huấn luyện viên chuyên nghiệp với 5 năm kinh nghiệm', 4.5, 'Đang làm việc');

-- Dữ liệu mẫu cho bảng Members
INSERT INTO Members (UserID, MemberCode, JoinDate, Status) VALUES
(4, 'MEMBER001', CURRENT_DATE, 'Hoạt động');

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
INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, Status, StaffID, Notes) VALUES
(500000, CURRENT_TIMESTAMP, 'Tiền mặt', 'Thành công', 1, 'Thanh toán gói tập 1 tháng'),
(2000000, CURRENT_TIMESTAMP, 'Chuyển khoản', 'Thành công', 1, 'Thanh toán gói tập với HLV');

-- Dữ liệu mẫu cho bảng Memberships
INSERT INTO Memberships (MemberID, PlanID, StartDate, EndDate, Status, PaymentID) VALUES
(1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', 'Hoạt động', 1);

-- Dữ liệu mẫu cho bảng TrainingRegistrations
INSERT INTO TrainingRegistrations (MemberID, PlanID, TrainerID, StartDate, SessionsLeft, PaymentID) VALUES
(1, 1, 1, CURRENT_DATE, 10, 2);

-- Dữ liệu mẫu cho bảng MembershipRenewals
INSERT INTO MembershipRenewals (MembershipID, NewEndDate, RenewalDate, PaymentID, StaffID, Notes) VALUES
(1, CURRENT_DATE + INTERVAL '30 days', CURRENT_TIMESTAMP, 1, 1, 'Gia hạn gói tập 1 tháng'); 