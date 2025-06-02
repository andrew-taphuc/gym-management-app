-- Dữ liệu mẫu cho bảng Users
INSERT INTO Users (Username, Password, Email, PhoneNumber, FullName, DateOfBirth, Gender, Address, CreatedAt, UpdateAt, Status, Role) VALUES
('admin', 'admin123', 'admin@gym.com', '0123456789', 'Nguyễn Văn Admin', '1990-01-01', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Chủ phòng tập'),
('staff1', 'staff123', 'staff1@gym.com', '0123456781', 'Trần Văn Staff', '1992-02-02', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Nhân viên quản lý'),
('trainer1', 'trainer123', 'trainer1@gym.com', '0123456782', 'Lê Văn Trainer', '1993-03-03', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Huấn luyện viên'),
('member1', 'member123', 'member1@gym.com', '0123456783', 'Phạm Văn Member', '1995-05-05', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),
('1', '1', 'member111@gym.com', '0123456783', 'Phạm Văn Member', '1995-05-05', 'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên');

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