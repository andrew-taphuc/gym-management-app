-- Dữ liệu mẫu cho bảng Users
INSERT INTO Users (Username, Password, Email, PhoneNumber, FullName, DateOfBirth, Gender, Address, CreatedAt, UpdateAt, Status, Role) VALUES

('admin', '1234', 'admin@gym.com', '0123456789', 'Tô Át Min', '1990-01-01', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Chủ phòng tập'),

('mana1', '1234', 'mana1@gym.com', '0123456781', 'Xuân Bắc', '1992-02-02', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Nhân viên quản lý'),

('mana2', '1234', 'mana2@gym.com', '0123456781', 'Tây Bắc', '1992-02-02', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Nhân viên quản lý'),

('pt1', '1234', 'pt1@gym.com', '0123456782', 'Tự Long', '1993-03-03', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Huấn luyện viên'),

('pt2', '1234', 'pt2@gym.com', '0123456782', 'Bằng Kiều', '1993-03-03', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Huấn luyện viên'),

('pt3', '1234', 'pt3@gym.com', '0123456782', 'Chí Trung', '1993-03-03', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Huấn luyện viên'),

('taphuc1', '1234', 'taphuc1@gym.com', '0123456783', 'Tạ Hồng Phúc', '2004-09-24', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),

('hungngoc', '1234', 'hungngoc@gym.com', '0123456783', 'Bùi Quang Hưng', '2004-08-26', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),

('tungdanchoi', '1234', 'tungbeo@gym.com', '0123456783', 'Nguyễn Tùng Béo', '2004-10-28', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên'),

('1', '1', 'member111@gym.com', '0123456783', 'Phạm Văn Member', '1995-05-05', 
'Nam', 'Hà Nội', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hoạt động', 'Hội viên');

-- Dữ liệu mẫu cho bảng Staff
INSERT INTO Staff (UserID, StaffCode, Status) VALUES
(2, 'STAFF001', 'Đang làm việc');

-- Dữ liệu mẫu cho bảng Trainers
INSERT INTO Trainers (UserID, TrainerCode, Specialization, Bio, Rating, Status) VALUES
(4, 'TRAINER001', 'Gym', 'Huấn luyện viên chuyên nghiệp với 3 năm kinh nghiệm', 4.5, 'Đang làm việc'),
(5, 'TRAINER002', 'Kickfit', 'Huấn luyện viên chuyên nghiệp với 4 năm kinh nghiệm', 4.5, 'Đang làm việc'),
(6, 'TRAINER003', 'Yoga', 'Huấn luyện viên chuyên nghiệp với 5 năm kinh nghiệm', 4.5, 'Đang làm việc');
-- Dữ liệu mẫu cho bảng Members
INSERT INTO Members (UserID, MemberCode, JoinDate, Status) VALUES
(7, 'HV0001', CURRENT_DATE, 'Hoạt động'),
(8, 'HV0002', CURRENT_DATE, 'Hoạt động'),
(9, 'HV0003', CURRENT_DATE, 'Hoạt động'),
(10, 'HV0004', CURRENT_DATE, 'Hoạt động');

-- Dữ liệu mẫu cho bảng Rooms
INSERT INTO Rooms (RoomCode, RoomName, RoomType, Description, Status) VALUES
('ROOM001', 'Phòng Gym Chính', 'gym', 'Phòng tập chính với đầy đủ thiết bị', 'Hoạt động'),
('ROOM002', 'Phòng Kickfit', 'kickfit', 'Phòng tập Kickfit với các thiết bị chuyên dụng', 'Hoạt động'),
('ROOM003', 'Phòng Chạy bộ', 'cardio', 'Phòng tập chạy bộ với nhiều máy chạy bộ hiện đại', 'Hoạt động'),
('ROOM004', 'Phòng Kickfit 2', 'kickfit', 'Phòng tập Kickfit phụ với đầy đủ thiết bị', 'Hoạt động'),
('ROOM005', 'Phòng Đạp xe', 'cardio', 'Phòng tập xe đạp với nhiều xe đạp tập chất lượng cao', 'Hoạt động'),
('ROOM006', 'Phòng Yoga', 'yoga', 'Phòng tập yoga yên tĩnh với không gian thoáng đãng', 'Hoạt động'),
('ROOM007', 'Phòng Giãn cơ', 'stretch', 'Phòng giãn cơ với các thiết bị hỗ trợ', 'Hoạt động');

-- Dữ liệu mẫu cho bảng EquipmentTypes
INSERT INTO EquipmentTypes (EquipmentName, Description) VALUES
('Máy chạy bộ', 'Life Fitness Platinum Club Series - Máy chạy bộ cao cấp với màn hình HD 22 inch, công nghệ giảm chấn FlexDeck'),
('Xe đạp tập', 'Technogym ARTIS BIKE - Xe đạp tập thông minh với màn hình Unity 19 inch, tích hợp AI'),
('Máy tập bụng', 'Hammer Strength Ab Series - Máy tập cơ bụng chuyên nghiệp với thiết kế công thái học'),
('Máy kéo xô', 'Cybex PRESTIGE VRS - Máy kéo xô cao cấp với hệ thống ròng rọc chính xác'),
('Máy ép ngực', 'Precor Discovery Series - Máy ép ngực với công nghệ điều chỉnh tự động'),
('Máy tập đùi', 'Matrix Ultra Series - Máy tập đùi với hệ thống tạ stack chuyên nghiệp'),
('Tạ tay', 'Eleiko IWF Competition - Bộ tạ tay Olympic tiêu chuẩn thi đấu quốc tế'),
('Giàn tạ đa năng', 'Star Trac Inspiration - Giàn tạ đa năng với nhiều vị trí tập'),
('Máy rowing', 'Concept2 Model E - Máy chèo thuyền cao cấp với màn hình PM5'),
('Máy tập vai', 'Nautilus Inspiration - Máy tập vai với công nghệ SmartLock'),
('Máy tập tay', 'TechnoGym Selection Pro - Máy tập cánh tay với hệ thống điều chỉnh thông minh'),
('Máy xoay eo', 'Life Fitness Signature Series - Máy xoay eo với đệm cao su đặc biệt'),
('Máy tập lưng', 'Hammer Strength MTS - Máy tập lưng với công nghệ iso-lateral'),
('Tạ đĩa', 'Rogue Calibrated Steel - Tạ đĩa chuẩn IPF với độ chính xác cao'),
('Máy ép ngực', 'Cybex Eagle NX - Máy ép ngực với công nghệ độc quyền'),
('Máy tập chân', 'Matrix Versa Series - Máy tập chân đa năng với nhiều góc tập'),
('Máy tập tay', 'Precor Vitality Series - Máy tập tay với thiết kế tiện dụng'),
('Máy tập mông', 'Star Trac Max Rack - Máy tập mông với công nghệ Max Rack'),
('Máy tập bụng', 'TechnoGym Pure Strength - Máy tập bụng với thiết kế biomechanical'),
('Máy chạy cong', 'AssaultRunner Elite - Máy chạy bộ cong không dùng điện cao cấp'),
('Thảm yoga', 'Manduka PRO - Thảm yoga cao cấp chống trượt, độ dày 6mm, thân thiện với môi trường'),
('Bao đấm', 'Fairtex HB6 - Bao đấm chuyên nghiệp với da microfiber cao cấp, chiều dài 6ft'),
('Găng tay đấm bốc', 'Twins Special - Găng tay đấm bốc chuyên nghiệp với da bò thật'),
('Dây kháng lực', 'TheraBand - Bộ dây kháng lực cao su tự nhiên với nhiều mức độ kháng lực'),
('Bóng yoga', 'STOTT PILATES - Bóng tập yoga/pilates chống nổ với đường kính 65cm');

-- Dữ liệu mẫu cho bảng RoomEquipment
INSERT INTO RoomEquipment (RoomID, EquipmentTypeID, EquipmentCode, Quantity, Status, Description) VALUES
(1, 1, 'EQ001', 5, 'Hoạt động', 'Máy chạy bộ mới'),
(1, 2, 'EQ002', 5, 'Hoạt động', 'Xe đạp tập mới'),
(1, 3, 'EQ003', 1, 'Hoạt động', 'Máy tập bụng'),
(1, 4, 'EQ004', 1, 'Hoạt động', 'Máy kéo xô'),
(1, 5, 'EQ005', 1, 'Hoạt động', 'Máy ép ngực'),
(1, 6, 'EQ006', 1, 'Hoạt động', 'Máy tập đùi'),
(1, 7, 'EQ007', 10, 'Hoạt động', 'Bộ tạ tay'),
(1, 8, 'EQ008', 1, 'Hoạt động', 'Giàn tạ đa năng'),
(1, 14, 'EQ009', 20, 'Hoạt động', 'Tạ đĩa'),

(2, 21, 'EQ010', 20, 'Hoạt động', 'Thảm tập kickfit'),
(2, 22, 'EQ011', 10, 'Hoạt động', 'Bao đấm'),
(2, 23, 'EQ012', 20, 'Hoạt động', 'Găng tay đấm bốc'),

(3, 1, 'EQ013', 10, 'Hoạt động', 'Máy chạy bộ phòng cardio'),
(3, 20, 'EQ014', 5, 'Hoạt động', 'Máy chạy cong'),

(4, 21, 'EQ015', 20, 'Hoạt động', 'Thảm tập kickfit'),
(4, 22, 'EQ016', 8, 'Hoạt động', 'Bao đấm'),
(4, 23, 'EQ017', 20, 'Hoạt động', 'Găng tay đấm bốc'),

(5, 2, 'EQ018', 15, 'Hoạt động', 'Xe đạp tập phòng cardio'),

(6, 21, 'EQ019', 30, 'Hoạt động', 'Thảm yoga'),
(6, 24, 'EQ020', 20, 'Hoạt động', 'Dây kháng lực'),
(6, 25, 'EQ021', 20, 'Hoạt động', 'Bóng yoga'),

(7, 21, 'EQ022', 20, 'Hoạt động', 'Thảm tập stretch'),
(7, 24, 'EQ023', 20, 'Hoạt động', 'Dây kháng lực'),
(7, 25, 'EQ024', 15, 'Hoạt động', 'Bóng tập');


-- Dữ liệu mẫu cho bảng MembershipPlans
INSERT INTO MembershipPlans (PlanCode, PlanName, Duration, Price, Description) VALUES
('PLAN001', 'Gói 1 tháng', 30, 500000, 'Gói tập 1 tháng'),
('PLAN002', 'Gói 3 tháng', 90, 1400000, 'Gói tập 3 tháng với ưu đãi giảm giá'),
('PLAN003', 'Gói 6 tháng', 180, 2500000, 'Gói tập 6 tháng với quyền sử dụng phòng xông hơi'),
('PLAN004', 'Gói 12 tháng', 365, 4500000, 'Gói tập 1 năm với đầy đủ quyền lợi VIP'),
('PLAN005', 'Gói Gia đình', 90, 1200000, 'Gói tập dành cho 2 người trong gia đình trong 3 tháng');


-- Dữ liệu mẫu cho bảng TrainingPlans
INSERT INTO TrainingPlans (PlanCode, PlanName, Type, SessionAmount, Price, Description) VALUES
('TPLAN001', 'Gói 10 buổi', 'Gym', 10, 2000000, 'Gói tập với HLV 10 buổi'),
('TPLAN002', 'Gói 20 buổi', 'Gym', 20, 3500000, 'Gói tập với HLV 20 buổi'),
('TPLAN003', 'Gói 30 buổi', 'Gym', 30, 5000000, 'Gói tập với HLV 30 buổi'),
('TPLAN004', 'Gói 10 buổi', 'Kickfit', 10, 2200000, 'Gói tập Kickfit với HLV 10 buổi'),
('TPLAN005', 'Gói 20 buổi', 'Kickfit', 20, 4000000, 'Gói tập Kickfit với HLV 20 buổi'),
('TPLAN006', 'Gói 30 buổi', 'Kickfit', 30, 5500000, 'Gói tập Kickfit với HLV 30 buổi'),
('TPLAN007', 'Gói 10 buổi', 'Yoga', 10, 1800000, 'Gói tập Yoga với HLV 10 buổi'),
('TPLAN008', 'Gói 20 buổi', 'Yoga', 20, 3200000, 'Gói tập Yoga với HLV 20 buổi'),
('TPLAN009', 'Gói 30 buổi', 'Yoga', 30, 4500000, 'Gói tập Yoga với HLV 30 buổi');


-- Dữ liệu mẫu cho bảng Payments
INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, Status, Notes) VALUES
(1400000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 3 tháng'),
(2500000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 6 tháng'), 
(4500000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 12 tháng'),
(1200000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập gia đình'),
(2000000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 10 buổi Gym'),
(3500000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 20 buổi Gym'),
(2200000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 10 buổi Kickfit'),
(1800000, CURRENT_TIMESTAMP, 'Credit Card', 'Thành công', 'Thanh toán gói tập 10 buổi Yoga');

-- Dữ liệu mẫu cho bảng Memberships
INSERT INTO Memberships (MemberID, UserID, PlanID, StartDate, EndDate, Status, PaymentID) VALUES
(1, 5, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '90 days', 'Chưa kích hoạt', 1),
(2, 6, 2, CURRENT_DATE, CURRENT_DATE + INTERVAL '180 days', 'Chưa kích hoạt', 2),
(3, 7, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '365 days', 'Chưa kích hoạt', 3), 
(4, 8, 5, CURRENT_DATE, CURRENT_DATE + INTERVAL '90 days', 'Chưa kích hoạt', 4);

-- Dữ liệu mẫu cho bảng TrainingRegistrations
INSERT INTO TrainingRegistrations (MemberID, PlanID, TrainerID, StartDate, SessionsLeft, PaymentID) VALUES
(1, 1, 1, CURRENT_DATE, 10, 5), -- Member 1 đăng ký gói Gym 10 buổi với trainer 1
(2, 2, 2, CURRENT_DATE, 20, 6), -- Member 2 đăng ký gói Gym 20 buổi với trainer 2
(3, 7, 3, CURRENT_DATE, 10, 7), -- Member 3 đăng ký gói Yoga 10 buổi với trainer 3
(4, 4, 1, CURRENT_DATE, 10, 8);  -- Member 4 đăng ký gói Kickfit 10 buổi với trainer 1

INSERT INTO TrainingRegistrations (MemberID, PlanID, StartDate, SessionsLeft, PaymentID) VALUES
(1, 1, CURRENT_DATE, 10, 5); -- Member 1 đăng ký gói Gym 10 buổi với trainer 1


-- Dữ liệu mẫu cho bảng MembershipRenewals
-- INSERT INTO MembershipRenewals (MembershipID, NewEndDate, RenewalDate, PaymentID, Notes) VALUES
-- (1, CURRENT_DATE + INTERVAL '30 days', CURRENT_TIMESTAMP, 1, 'Gia hạn gói tập 1 tháng');

-- Dữ liệu mẫu cho bảng Attendance
-- INSERT INTO Attendance (MemberID, MembershipID, CheckInTime, Type) VALUES
-- (1, 1, CURRENT_TIMESTAMP - INTERVAL '2 days', 'Gym'),
-- (1, 1, CURRENT_TIMESTAMP - INTERVAL '1 days', 'Gym'),
-- (1, 1, CURRENT_TIMESTAMP, 'Gym');

-- Dữ liệu mẫu cho bảng TrainingSchedule
INSERT INTO TrainingSchedule (MemberID, RegistrationID, TrainerID, MembershipID, ScheduleDate, StartTime, Duration, RoomID, Status, Notes, CreatedDate) VALUES
-- Member 1 - Gym với trainer 1
(1, 1, 1, 1, CURRENT_DATE + INTERVAL '1 day', '09:00', 60, 1, 'Đã lên lịch', 'Buổi tập Gym 1', CURRENT_TIMESTAMP),
(1, 1, 1, 1, CURRENT_DATE + INTERVAL '3 day', '09:00', 60, 4, 'Đã lên lịch', 'Buổi tập Stretch', CURRENT_TIMESTAMP), -- Phòng stretch
(1, 1, 1, 1, CURRENT_DATE + INTERVAL '5 day', '09:00', 60, 1, 'Đã lên lịch', 'Buổi tập Gym 3', CURRENT_TIMESTAMP),
(1, 1, 1, 1, CURRENT_DATE + INTERVAL '7 day', '09:00', 60, 5, 'Đã lên lịch', 'Buổi tập Cardio', CURRENT_TIMESTAMP), -- Phòng cardio

-- Member 2 - Gym với trainer 2 
(2, 2, 2, 2, CURRENT_DATE + INTERVAL '1 day', '10:00', 60, 1, 'Đã lên lịch', 'Buổi tập Gym 1', CURRENT_TIMESTAMP),
(2, 2, 2, 2, CURRENT_DATE + INTERVAL '3 day', '10:00', 60, 1, 'Đã lên lịch', 'Buổi tập Gym 2', CURRENT_TIMESTAMP),
(2, 2, 2, 2, CURRENT_DATE + INTERVAL '5 day', '10:00', 60, 5, 'Đã lên lịch', 'Buổi tập Cardio', CURRENT_TIMESTAMP), -- Phòng cardio
(2, 2, 2, 2, CURRENT_DATE + INTERVAL '7 day', '10:00', 60, 4, 'Đã lên lịch', 'Buổi tập Stretch', CURRENT_TIMESTAMP), -- Phòng stretch

-- Member 3 - Yoga với trainer 3
(3, 3, 3, 3, CURRENT_DATE + INTERVAL '1 day', '14:00', 60, 2, 'Đã lên lịch', 'Buổi tập Yoga 1', CURRENT_TIMESTAMP),
(3, 3, 3, 3, CURRENT_DATE + INTERVAL '3 day', '14:00', 60, 4, 'Đã lên lịch', 'Buổi tập Stretch', CURRENT_TIMESTAMP), -- Phòng stretch
(3, 3, 3, 3, CURRENT_DATE + INTERVAL '5 day', '14:00', 60, 2, 'Đã lên lịch', 'Buổi tập Yoga 3', CURRENT_TIMESTAMP),
(3, 3, 3, 3, CURRENT_DATE + INTERVAL '7 day', '14:00', 60, 5, 'Đã lên lịch', 'Buổi tập Cardio', CURRENT_TIMESTAMP), -- Phòng cardio

-- Member 4 - Kickfit với trainer 1
(4, 4, 1, 4, CURRENT_DATE + INTERVAL '2 day', '09:00', 60, 3, 'Đã lên lịch', 'Buổi tập Kickfit 1', CURRENT_TIMESTAMP),
(4, 4, 1, 4, CURRENT_DATE + INTERVAL '4 day', '09:00', 60, 5, 'Đã lên lịch', 'Buổi tập Cardio', CURRENT_TIMESTAMP), -- Phòng cardio
(4, 4, 1, 4, CURRENT_DATE + INTERVAL '6 day', '09:00', 60, 3, 'Đã lên lịch', 'Buổi tập Kickfit 3', CURRENT_TIMESTAMP),
(4, 4, 1, 4, CURRENT_DATE + INTERVAL '8 day', '09:00', 60, 4, 'Đã lên lịch', 'Buổi tập Stretch', CURRENT_TIMESTAMP); -- Phòng stretch



-- Dữ liệu mẫu cho bảng Exercises
INSERT INTO Exercises (ExerciseCode, ExerciseName, Category, Description) VALUES
-- Bài tập GYM
('GYM001', 'Bench Press', 'Gym', 'Bài tập ngực với tạ đẩy'),
('GYM002', 'Deadlift', 'Gym', 'Bài tập lưng và chân tổng hợp'),
('GYM003', 'Squat', 'Gym', 'Bài tập chân với tạ đòn'),
('GYM004', 'Shoulder Press', 'Gym', 'Bài tập vai với tạ đẩy'),
('GYM005', 'Lat Pulldown', 'Gym', 'Bài tập lưng với máy kéo xô'),
('GYM006', 'Bicep Curl', 'Gym', 'Bài tập tay trước với tạ tay'),
('GYM007', 'Tricep Extension', 'Gym', 'Bài tập tay sau với dây cáp'),
('GYM008', 'Leg Press', 'Gym', 'Bài tập chân với máy đẩy'),
('GYM009', 'Cable Fly', 'Gym', 'Bài tập ngực với dây cáp'),
('GYM010', 'Romanian Deadlift', 'Gym', 'Bài tập mông và đùi sau'),

-- Bài tập Kickfit
('KICK001', 'Jab', 'Kickfit', 'Đòn đấm thẳng tay trước'),
('KICK002', 'Cross', 'Kickfit', 'Đòn đấm chéo tay sau'),
('KICK003', 'Hook', 'Kickfit', 'Đòn đấm móc'),
('KICK004', 'Uppercut', 'Kickfit', 'Đòn đấm móc dưới'),
('KICK005', 'Front Kick', 'Kickfit', 'Đá thẳng'),
('KICK006', 'Roundhouse Kick', 'Kickfit', 'Đá vòng cầu'),
('KICK007', 'Side Kick', 'Kickfit', 'Đá ngang'),
('KICK008', 'Back Kick', 'Kickfit', 'Đá sau'),
('KICK009', 'Knee Strike', 'Kickfit', 'Đòn gối'),
('KICK010', 'Elbow Strike', 'Kickfit', 'Đòn khuỷu'),

-- Bài tập Yoga
('YOGA001', 'Sun Salutation', 'Yoga', 'Chuỗi động tác chào mặt trời'),
('YOGA002', 'Downward Dog', 'Yoga', 'Tư thế chó cúi đầu'),
('YOGA003', 'Warrior I', 'Yoga', 'Tư thế chiến binh 1'),
('YOGA004', 'Tree Pose', 'Yoga', 'Tư thế cây'),
('YOGA005', 'Child Pose', 'Yoga', 'Tư thế em bé'),
('YOGA006', 'Cobra Pose', 'Yoga', 'Tư thế rắn hổ mang'),
('YOGA007', 'Bridge Pose', 'Yoga', 'Tư thế cây cầu'),
('YOGA008', 'Triangle Pose', 'Yoga', 'Tư thế tam giác'),
('YOGA009', 'Cat-Cow Stretch', 'Yoga', 'Tư thế mèo-bò'),
('YOGA010', 'Corpse Pose', 'Yoga', 'Tư thế xác chết'),

-- Bài tập Stretch
('STR001', 'Hamstring Stretch', 'Stretch', 'Giãn cơ đùi sau'),
('STR002', 'Quad Stretch', 'Stretch', 'Giãn cơ đùi trước'),
('STR003', 'Hip Flexor Stretch', 'Stretch', 'Giãn cơ hông'),
('STR004', 'Calf Stretch', 'Stretch', 'Giãn cơ bắp chân'),
('STR005', 'Shoulder Stretch', 'Stretch', 'Giãn cơ vai'),
('STR006', 'Chest Stretch', 'Stretch', 'Giãn cơ ngực'),
('STR007', 'Back Stretch', 'Stretch', 'Giãn cơ lưng'),
('STR008', 'Neck Stretch', 'Stretch', 'Giãn cơ cổ'),
('STR009', 'Butterfly Stretch', 'Stretch', 'Tư thế bươm bướm'),
('STR010', 'Spinal Twist', 'Stretch', 'Xoay cột sống'),

-- Bài tập Cardio
('CAR001', 'Running', 'Cardio', 'Chạy bộ'),
('CAR002', 'Jumping Jacks', 'Cardio', 'Nhảy jack'),
('CAR003', 'Mountain Climbers', 'Cardio', 'Leo núi tại chỗ'),
('CAR004', 'Burpees', 'Cardio', 'Bài tập burpee'),
('CAR005', 'Jump Rope', 'Cardio', 'Nhảy dây'),
('CAR006', 'High Knees', 'Cardio', 'Nâng gối cao'),
('CAR007', 'Cycling', 'Cardio', 'Đạp xe'),
('CAR008', 'Box Jumps', 'Cardio', 'Nhảy hộp'),
('CAR009', 'Stair Climbing', 'Cardio', 'Leo cầu thang'),
('CAR010', 'Rowing', 'Cardio', 'Chèo thuyền');

-- Dữ liệu mẫu cho bảng TrainingScheduleExercises
INSERT INTO TrainingScheduleExercises (ScheduleID, ExerciseID, Set, Rep, Comment) VALUES
-- Schedule 1 (Gym): 3 bài tập gym
(1, 1, 3, 12, 'Tập trung vào form chuẩn, tăng dần trọng lượng'),
(1, 2, 3, 10, 'Giữ lưng thẳng, hạ xuống chậm rãi'),
(1, 3, 3, 8, 'Nắm chặt thanh, kéo lên đến cằm'),

-- Schedule 2 (Stretch): 3 bài tập stretch
(2, 31, 3, 30, 'Giữ tư thế 30 giây mỗi lần, thở đều'),
(2, 32, 3, 30, 'Đẩy nhẹ nhàng, không gây đau'),
(2, 33, 3, 30, 'Mở rộng hông, giữ thăng bằng'),

-- Schedule 3 (Gym): 3 bài tập gym (có thể trùng với schedule 1)
(3, 1, 4, 10, 'Tăng số set, giảm rep để tăng sức mạnh'),
(3, 4, 3, 12, 'Đẩy mạnh lên trên, hạ xuống kiểm soát'),
(3, 5, 3, 15, 'Squat sâu, đùi song song với sàn'),

-- Schedule 4 (Cardio): 3 bài tập cardio
(4, 41, 3, 60, 'Chạy tốc độ vừa phải, duy trì nhịp thở'),
(4, 42, 3, 20, 'Nhảy nhanh, giữ nhịp đều'),
(4, 43, 3, 15, 'Leo nhanh, giữ thân người thẳng'),

-- Schedule 5 (Gym): 3 bài tập gym (có thể trùng với các schedule khác)
(5, 2, 4, 8, 'Tăng cường độ, giảm rep để build sức mạnh'),
(5, 6, 3, 12, 'Nâng tạ đều hai tay, không xoay người'),
(5, 7, 3, 10, 'Giữ thăng bằng, nâng tạ đều hai bên'),

-- Schedule 6 (Gym): 3 bài tập gym (có thể trùng bài tập)
(6, 3, 3, 12, 'Kéo lên cao hơn so với lần trước'),
(6, 8, 3, 12, 'Cúi người về phía trước, giữ lưng thẳng'),
(6, 9, 4, 8, 'Nâng tạ lên cao, kiểm soát chuyển động'),

-- Schedule 7 (Cardio): 3 bài tập cardio (có thể trùng với schedule 4)
(7, 41, 4, 45, 'Tăng thời gian chạy, giữ nhịp tim ổn định'),
(7, 44, 3, 30, 'Burpees cường độ cao, nghỉ ngắn giữa các set'),
(7, 45, 3, 100, 'Nhảy dây liên tục, giữ nhịp độ'),

-- Schedule 8 (Stretch): 3 bài tập stretch (có thể trùng với schedule 2)
(8, 31, 3, 45, 'Tăng thời gian giãn, thở sâu hơn'),
(8, 34, 3, 30, 'Giãn cơ bắp chân nhẹ nhàng, không gây đau'),
(8, 35, 3, 30, 'Xoay vai từ từ, thả lỏng cơ bắp'),

-- Schedule 9 (Yoga): 3 bài tập yoga
(9, 21, 3, 30, 'Giữ tư thế ổn định, thở sâu và đều'),
(9, 22, 3, 30, 'Duỗi thẳng cột sống, mở rộng ngực'),
(9, 23, 3, 30, 'Cân bằng trên một chân, tập trung tinh thần'),

-- Schedule 10 (Stretch): 3 bài tập stretch (có thể trùng bài)
(10, 32, 3, 35, 'Tăng cường độ giãn so với lần trước'),
(10, 36, 3, 30, 'Mở rộng ngực, thở sâu khi giãn'),
(10, 37, 3, 30, 'Giãn cơ đùi nhẹ nhàng, không gây đau'),

-- Schedule 11 (Yoga): 3 bài tập yoga (có thể trùng với schedule 9)
(11, 21, 2, 45, 'Giảm set nhưng tăng thời gian giữ tư thế'),
(11, 24, 3, 30, 'Tập trung vào hơi thở, giữ thăng bằng'),
(11, 25, 3, 30, 'Mở rộng hông, duỗi thẳng cột sống'),

-- Schedule 12 (Cardio): 3 bài tập cardio (có thể trùng bài)
(12, 42, 4, 25, 'Tăng số set jumping jacks'),
(12, 46, 3, 30, 'Nâng gối cao, tăng tốc độ dần'),
(12, 47, 3, 30, 'Đạp xe với tốc độ vừa phải, giữ nhịp thở'),

-- Schedule 13 (Kickfit): 3 bài tập kickfit
(13, 11, 3, 30, 'Đấm thẳng mạnh mẽ, giữ thăng bằng cơ thể'),
(13, 12, 3, 20, 'Đấm chéo với lực mạnh, xoay hông'),
(13, 15, 3, 25, 'Đá thẳng chính xác, duỗi thẳng chân'),

-- Schedule 14 (Cardio): 3 bài tập cardio (có thể trùng bài)
(14, 43, 4, 20, 'Tăng cường độ mountain climbers'),
(14, 44, 3, 25, 'Burpees nhanh hơn, giữ form chuẩn'),
(14, 48, 3, 15, 'Nhảy hộp an toàn, hạ xuống nhẹ nhàng'),

-- Schedule 15 (Kickfit): 3 bài tập kickfit (có thể trùng bài)
(15, 11, 4, 25, 'Tăng số set cho jab, tập trung tốc độ'),
(15, 13, 3, 20, 'Đấm móc liên tục, xoay hông theo chuyển động'),
(15, 16, 3, 15, 'Đá vòng cầu với lực mạnh, giữ thăng bằng'),

-- Schedule 16 (Stretch): 3 bài tập stretch (có thể trùng bài)
(16, 33, 3, 40, 'Tăng thời gian giãn hông'),
(16, 38, 3, 30, 'Xoay cổ từ từ, thả lỏng cơ cổ'),
(16, 40, 3, 30, 'Xoay cột sống nhẹ nhàng, thở sâu');

-- Dữ liệu mẫu cho bảng MemberProgress
INSERT INTO MemberProgress (MemberID, MeasurementDate, Weight, Height, BMI, BodyFatPercentage, Chest, Waist, Hip, Biceps, Thigh, TrainerID, Notes) VALUES
-- Member 1 - Đo lường ban đầu
(1, CURRENT_DATE - INTERVAL '30 days', 75.5, 175.0, 24.65, 18.5, 95.0, 80.0, 98.0, 32.0, 55.0, 1, 'Đo lường ban đầu khi bắt đầu tập'),
-- Member 1 - Đo lường sau 2 tuần
(1, CURRENT_DATE - INTERVAL '14 days', 74.8, 175.0, 24.41, 17.8, 96.0, 79.0, 97.5, 32.5, 55.5, 1, 'Tiến bộ tốt, giảm mỡ tăng cơ'),
-- Member 1 - Đo lường hiện tại
(1, CURRENT_DATE, 74.2, 175.0, 24.23, 17.2, 97.0, 78.0, 97.0, 33.0, 56.0, 1, 'Đạt mục tiêu giảm cân, tăng cơ bắp'),

-- Member 2 - Đo lường ban đầu
(2, CURRENT_DATE - INTERVAL '25 days', 68.0, 165.0, 24.98, 22.0, 88.0, 75.0, 95.0, 28.0, 52.0, 2, 'Mục tiêu tăng cân và xây dựng cơ bắp'),
-- Member 2 - Đo lường sau 2 tuần
(2, CURRENT_DATE - INTERVAL '12 days', 69.2, 165.0, 25.42, 21.5, 89.5, 75.5, 95.5, 29.0, 53.0, 2, 'Tăng cân tích cực, cơ bắp phát triển'),
-- Member 2 - Đo lường hiện tại
(2, CURRENT_DATE, 70.1, 165.0, 25.75, 21.0, 91.0, 76.0, 96.0, 30.0, 54.0, 2, 'Đạt mục tiêu tăng cân, cơ bắp rõ rệt hơn'),

-- Member 3 - Đo lường ban đầu
(3, CURRENT_DATE - INTERVAL '20 days', 58.5, 160.0, 22.85, 20.5, 82.0, 68.0, 88.0, 25.0, 48.0, 3, 'Tập trung vào độ dẻo dai và cân bằng'),
-- Member 3 - Đo lường sau 10 ngày
(3, CURRENT_DATE - INTERVAL '10 days', 58.8, 160.0, 22.97, 20.0, 82.5, 67.5, 88.5, 25.5, 48.5, 3, 'Cải thiện độ dẻo dai, tăng nhẹ cơ bắp'),
-- Member 3 - Đo lường hiện tại
(3, CURRENT_DATE, 59.0, 160.0, 23.05, 19.5, 83.0, 67.0, 89.0, 26.0, 49.0, 3, 'Đạt được sự cân bằng cơ thể tốt'),

-- Member 4 - Đo lường ban đầu
(4, CURRENT_DATE - INTERVAL '18 days', 82.0, 180.0, 25.31, 25.0, 105.0, 95.0, 105.0, 35.0, 62.0, 1, 'Mục tiêu giảm cân và tăng sức bền'),
-- Member 4 - Đo lường sau 1 tuần
(4, CURRENT_DATE - INTERVAL '7 days', 80.5, 180.0, 24.85, 23.5, 104.0, 92.0, 103.0, 35.5, 61.0, 1, 'Giảm cân tốt, tăng sức bền cardio'),
-- Member 4 - Đo lường hiện tại
(4, CURRENT_DATE, 79.2, 180.0, 24.44, 22.0, 103.0, 89.0, 101.0, 36.0, 60.0, 1, 'Đạt mục tiêu giảm cân, cải thiện sức khỏe tim mạch');

INSERT INTO Promotions (
  PromotionCode, PromotionName, Description, DiscountType, DiscountValue,
  StartDate, EndDate, Status
) VALUES
-- 2 mã hết hạn
('KM2501', 'Giảm giá Tết', 'Khuyến mại dịp Tết Nguyên Đán', 'Phần trăm', 10.00,
 '2025-01-01 00:00:00', '2025-01-10 23:59:59', 'Hết hạn'),

-- 10 mã còn hạn
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

('KM2514', 'Giảm giá dịp cuối tuần', 'Giảm giá cho các buổi tập cuối tuần', 'Phần trăm', 15.00,
 '2025-11-15 00:00:00', '2025-11-20 23:59:59', 'Chưa khả dụng');

INSERT INTO Feedback (MemberID, FeedbackType,Comment, FeedbackDate, Status, ResponseComment, ResponseDate, ResponderID) VALUES
(1, 'Cơ sở vật chất', 'Máy chạy bộ có vấn đề khi sử dụng', CURRENT_TIMESTAMP, 'Đã giải quyết', 'Máy đã được sửa chữa', CURRENT_TIMESTAMP, 2),
(2, 'Nhân viên', 'Nhân viên tập luyện rất tận tâm', CURRENT_TIMESTAMP, 'Đã giải quyết', 'Cảm ơn bạn đã phản hồi', CURRENT_TIMESTAMP, 1),
(3, 'Khác', 'Không gian tập luyện cần được cải thiện', CURRENT_TIMESTAMP, 'Đã giải quyết', 'Vui lòng cung cấp thêm thông tin chi tiết', CURRENT_TIMESTAMP, 1),
(4, 'Cơ sở vật chất', 'Máy chạy bộ có vấn đề khi sử dụng', CURRENT_TIMESTAMP, 'Đã giải quyết', 'Máy đã được sửa chữa', CURRENT_TIMESTAMP, 1);

INSERT INTO Feedback (MemberID, FeedbackType,Comment, FeedbackDate, Status) VALUES
(1, 'Cơ sở vật chất', 'Máy chạy bộ có vấn đề khi sử dụng', CURRENT_TIMESTAMP, 'Đang giải quyết'),
(2, 'Nhân viên', 'Nhân viên tập luyện rất tận tâm', CURRENT_TIMESTAMP, 'Đang giải quyết'),
(3, 'Khác', 'Không gian tập luyện cần được cải thiện', CURRENT_TIMESTAMP, 'Đang giải quyết'),
(4, 'Cơ sở vật chất', 'Máy chạy bộ có vấn đề khi sử dụng', CURRENT_TIMESTAMP, 'Đang giải quyết');

