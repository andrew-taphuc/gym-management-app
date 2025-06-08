\c postgres;
DROP DATABASE IF EXISTS gym_management;
CREATE DATABASE gym_management;
\c gym_management;

-- Giới tính
CREATE TYPE gender_enum AS ENUM ('Nam', 'Nữ', 'Khác');

-- Trạng thái tài khoản
CREATE TYPE user_status_enum AS ENUM ('Hoạt động', 'Khóa', 'Tạm ngừng');

-- Vai trò người dùng
CREATE TYPE role_enum AS ENUM ('Chủ phòng tập', 'Nhân viên quản lý', 'Huấn luyện viên', 'Hội viên');

-- Trạng thái nhân viên
CREATE TYPE staff_status_enum AS ENUM ('Đang làm việc', 'Nghỉ phép', 'Đã nghỉ việc');

-- Trạng thái huấn luyện viên
CREATE TYPE trainer_status_enum AS ENUM ('Đang làm việc', 'Bận', 'Nghỉ phép', 'Đã nghỉ việc');

-- Trạng thái hội viên
CREATE TYPE member_status_enum AS ENUM ('Hoạt động', 'Tạm ngừng', 'Hết hạn');

-- Specialization
CREATE TYPE trainer_specialization_enum AS ENUM ('Gym', 'Yoga', 'Kickfit');

-- ENUM trạng thái hội viên
CREATE TYPE membership_status_enum AS ENUM ('Hoạt động', 'Hết hạn', 'Chưa kích hoạt');

-- ENUM trạng thái thanh toán
CREATE TYPE payment_status_enum AS ENUM ('Thành công', 'Thất bại');

-- ENUM trạng thái phòng tập
CREATE TYPE room_status_enum AS ENUM ('Hoạt động', 'Bảo trì', 'Tạm ngừng');

-- ENUM trạng thái thiết bị
CREATE TYPE equipment_status_enum AS ENUM ('Hoạt động', 'Bảo trì');

CREATE TYPE training_status_enum AS ENUM ('Đã lên lịch', 'Hoàn thành', 'Hủy');

CREATE TYPE discount_type AS ENUM ('Phần trăm', 'Tiền mặt');

CREATE TYPE promotion_status AS ENUM ('Còn hạn', 'Hết hạn', 'Chưa khả dụng');


-- Bảng người dùng chung
CREATE TABLE Users (
  UserID SERIAL PRIMARY KEY,
  Username VARCHAR(50) NOT NULL UNIQUE,
  Password VARCHAR(255) NOT NULL,
  Email VARCHAR(100) UNIQUE,
  PhoneNumber VARCHAR(15),
  FullName VARCHAR(100) NOT NULL,
  DateOfBirth DATE,
  Gender gender_enum,
  Address VARCHAR(255),
  CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UpdateAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  Status user_status_enum DEFAULT 'Hoạt động',
  Role role_enum NOT NULL
);

-- Bảng nhân viên (không bao gồm huấn luyện viên nữa)
CREATE TABLE Staff (
  StaffID SERIAL PRIMARY KEY,
  UserID INT UNIQUE NOT NULL REFERENCES Users(UserID) ON DELETE CASCADE,
  StaffCode VARCHAR(20) NOT NULL UNIQUE,
  Status staff_status_enum DEFAULT 'Đang làm việc'
);

-- Bảng huấn luyện viên - KẾ THỪA TRỰC TIẾP TỪ USERS
CREATE TABLE Trainers (
  TrainerID SERIAL PRIMARY KEY,
  UserID INT UNIQUE NOT NULL REFERENCES Users(UserID) ON DELETE CASCADE,
  TrainerCode VARCHAR(20) NOT NULL UNIQUE,
  Specialization trainer_specialization_enum,
  Bio VARCHAR(1000),
  Rating DECIMAL(3,2),
  Status trainer_status_enum DEFAULT 'Đang làm việc'
);

-- Bảng hội viên
CREATE TABLE Members (
  MemberID SERIAL PRIMARY KEY,
  UserID INT UNIQUE NOT NULL REFERENCES Users(UserID) ON DELETE CASCADE,
  MemberCode VARCHAR(20) NOT NULL UNIQUE,
  JoinDate DATE,
  Status member_status_enum DEFAULT 'Hoạt động'
);

-- BẢNG GÓI TẬP - DÀNH CHO TRUY CẬP GYM
CREATE TABLE MembershipPlans (
  PlanID SERIAL PRIMARY KEY,
  PlanCode VARCHAR(20) NOT NULL UNIQUE,
  PlanName VARCHAR(100) NOT NULL,
  Duration INT NOT NULL,
  Price DECIMAL(12,2) NOT NULL CHECK (Price >= 0),
  Description VARCHAR(500)
);

-- BẢNG GÓI TẬP - DÀNH CHO HUẤN LUYỆN VIÊN (THEO SỐ BUỔI)
CREATE TABLE TrainingPlans (
  PlanID SERIAL PRIMARY KEY,
  PlanCode VARCHAR(20) NOT NULL UNIQUE,
  PlanName VARCHAR(100) NOT NULL,
  Type trainer_specialization_enum NOT NULL,
  SessionAmount INT NOT NULL CHECK (SessionAmount > 0),
  Price DECIMAL(12,2) NOT NULL CHECK (Price >= 0),
  Description VARCHAR(500)
);

-- Bảng khuyến mãi
CREATE TABLE Promotions (
  PromotionID SERIAL PRIMARY KEY,
  PromotionCode VARCHAR(20) NOT NULL UNIQUE,
  PromotionName VARCHAR(100) NOT NULL,
  Description VARCHAR(500),
  DiscountType discount_type NOT NULL,
  DiscountValue DECIMAL(10,2) NOT NULL,
  StartDate TIMESTAMP NOT NULL,
  EndDate TIMESTAMP NOT NULL,
  Status promotion_status DEFAULT 'Còn hạn',
  CreatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UpdatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng thanh toán
CREATE TABLE Payments (
  PaymentID SERIAL PRIMARY KEY,
  Amount DECIMAL(12,2) NOT NULL CHECK (Amount >= 0),
  PaymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PaymentMethod VARCHAR(50) NOT NULL,
  Status payment_status_enum DEFAULT 'Thành công',
  PromotionID INT REFERENCES Promotions(PromotionID) ON DELETE CASCADE,
  Notes VARCHAR(500)
);

-- BẢNG ĐĂNG KÝ GÓI TRUY CẬP GYM (Thêm PaymentID)
CREATE TABLE Memberships (
  MembershipID SERIAL PRIMARY KEY,
  UserID INT NOT NULL REFERENCES Users(UserID) ON DELETE CASCADE,
  MemberID INT NOT NULL REFERENCES Members(MemberID) ON DELETE CASCADE,
  PlanID INT NOT NULL REFERENCES MembershipPlans(PlanID) ON DELETE CASCADE,
  StartDate DATE NOT NULL,
  EndDate DATE NOT NULL,
  Status membership_status_enum DEFAULT 'Chưa kích hoạt',
  PaymentID INT REFERENCES Payments(PaymentID)
);

-- BẢNG ĐĂNG KÝ GÓI HUẤN LUYỆN (Thêm PaymentID)
CREATE TABLE Memberships (
  MembershipID SERIAL PRIMARY KEY,
  UserID INT NOT NULL REFERENCES Users(UserID) ON DELETE CASCADE,
  MemberID INT NOT NULL REFERENCES Members(MemberID) ON DELETE CASCADE,
  PlanID INT NOT NULL REFERENCES MembershipPlans(PlanID) ON DELETE CASCADE,
  StartDate DATE NOT NULL,
  EndDate DATE NOT NULL,
  Status membership_status_enum DEFAULT 'Chưa kích hoạt',
  PaymentID INT REFERENCES Payments(PaymentID),
  RenewalTo INT REFERENCES Memberships(MembershipID),
  RenewalDate TIMESTAMP;
);


-- Module quản lý phòng tập, thiết bị
-- 2. Rooms table
CREATE TABLE Rooms (
    RoomID SERIAL PRIMARY KEY,
    RoomCode VARCHAR(20) NOT NULL UNIQUE,
    RoomName VARCHAR(100) NOT NULL,
    RoomType VARCHAR(50) NOT NULL, -- gym, yoga, etc.
    Description VARCHAR(500),
    Status room_status_enum DEFAULT 'Hoạt động',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. EquipmentTypes table (global catalog)
CREATE TABLE EquipmentTypes (
    EquipmentTypeID SERIAL PRIMARY KEY,
    EquipmentName VARCHAR(100) NOT NULL,
    Description VARCHAR(500)
);

-- 4. RoomEquipment table (each equipment instance in a room)
CREATE TABLE RoomEquipment (
    RoomEquipmentID SERIAL PRIMARY KEY,
    RoomID INT NOT NULL REFERENCES Rooms(RoomID) ON DELETE CASCADE,
    EquipmentTypeID INT NOT NULL REFERENCES EquipmentTypes(EquipmentTypeID) ON DELETE CASCADE,
    EquipmentCode VARCHAR(20) NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,
    Status equipment_status_enum DEFAULT 'Hoạt động',
    Description VARCHAR(500),
    CreatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Ensure equipment code is unique within a fitness center
    CONSTRAINT uq_equipment_code_per_center UNIQUE (EquipmentCode)
);

-- Bảng lịch tập
CREATE TABLE TrainingSchedule (
  ScheduleID SERIAL PRIMARY KEY,
  RegistrationID INT NOT NULL REFERENCES TrainingRegistrations(RegistrationID) ON DELETE CASCADE,
  MemberID INT NOT NULL,
  TrainerID INT,
  MembershipID INT NOT NULL,
  ScheduleDate DATE NOT NULL,
  StartTime TIME NOT NULL,
  Duration INT,
  RoomID INT,
  Status training_status_enum DEFAULT 'Đã lên lịch',
  Notes VARCHAR(500),
  CreatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Check-in/Check-out
CREATE TABLE Attendance (
  AttendanceID SERIAL PRIMARY KEY,
  MemberID INT NOT NULL,
  MembershipID INT NOT NULL,
  CheckInTime TIMESTAMP NOT NULL,
  TrainingScheduleID INT REFERENCES TrainingSchedule(ScheduleID) ON DELETE CASCADE
);

-- Bảng bài tập
CREATE TABLE Exercises (
  ExerciseID SERIAL PRIMARY KEY,
  ExerciseCode VARCHAR(20) NOT NULL UNIQUE,
  ExerciseName VARCHAR(100) NOT NULL,
  Category VARCHAR(50),
  Description VARCHAR(500)
);

-- Bảng liên kết lịch tập và bài tập
CREATE TABLE TrainingScheduleExercises (
  ScheduleID INT NOT NULL REFERENCES TrainingSchedule(ScheduleID) ON DELETE CASCADE,
  ExerciseID INT NOT NULL REFERENCES Exercises(ExerciseID) ON DELETE CASCADE,
  Set INT NOT NULL,
  Rep INT NOT NULL,
  Comment VARCHAR(500), -- Nhận xét của PT cho bài tập này
  PRIMARY KEY (ScheduleID, ExerciseID)
);

-- Bảng Đánh giá tiến độ hội viên
CREATE TABLE MemberProgress (
  ProgressID SERIAL PRIMARY KEY,
  MemberID INT NOT NULL,
  MeasurementDate DATE NOT NULL,
  Weight DECIMAL(5,2),
  Height DECIMAL(5,2),
  BMI DECIMAL(4,2),
  BodyFatPercentage DECIMAL(4,2),
  Chest DECIMAL(5,2),
  Waist DECIMAL(5,2),
  Hip DECIMAL(5,2),
  Biceps DECIMAL(5,2),
  Thigh DECIMAL(5,2),
  TrainerID INT,
  Notes VARCHAR(500)
);

CREATE TYPE feedback_type AS ENUM ('Cơ sở vật chất', 'Nhân viên', 'Khác');
CREATE TYPE feedback_status AS ENUM ('Đang giải quyết', 'Đã giải quyết');

CREATE TABLE Feedback (
    FeedbackID SERIAL PRIMARY KEY,
    MemberID INT NOT NULL,
    FeedbackType feedback_type NOT NULL,
    Comment VARCHAR(1000),
    FeedbackDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Status feedback_status DEFAULT 'Đang giải quyết',
    ResponseComment VARCHAR(1000),
    ResponseDate TIMESTAMP,
    ResponderID INT -- Người phản hồi
);
