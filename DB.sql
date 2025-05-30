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

-- Bảng thanh toán
CREATE TABLE Payments (
  PaymentID SERIAL PRIMARY KEY,
  Amount DECIMAL(12,2) NOT NULL CHECK (Amount >= 0),
  PaymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PaymentMethod VARCHAR(50) NOT NULL,
  Status payment_status_enum DEFAULT 'Thành công',
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
CREATE TABLE TrainingRegistrations (
  RegistrationID SERIAL PRIMARY KEY,
  MemberID INT NOT NULL REFERENCES Members(MemberID) ON DELETE CASCADE,
  PlanID INT NOT NULL REFERENCES TrainingPlans(PlanID) ON DELETE CASCADE,
  TrainerID INT NOT NULL REFERENCES Trainers(TrainerID) ON DELETE CASCADE,
  StartDate DATE NOT NULL,
  SessionsLeft INT DEFAULT 0 CHECK (SessionsLeft >= 0),
  PaymentID INT REFERENCES Payments(PaymentID)
);

-- BẢNG GIA HẠN GÓI GYM (đã có PaymentID, chỉ cần chắc chắn có FK)
CREATE TABLE MembershipRenewals (
  RenewalID SERIAL PRIMARY KEY,
  MembershipID INT NOT NULL REFERENCES Memberships(MembershipID) ON DELETE CASCADE,
  NewEndDate DATE NOT NULL,
  RenewalDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PaymentID INT REFERENCES Payments(PaymentID),
  Notes VARCHAR(500)
);

-- Module quản lý phòng tập, thiết bị
-- 1. Fitness Center table
CREATE TABLE FitnessCenter (
    CenterID SERIAL PRIMARY KEY,
    CenterName VARCHAR(100) NOT NULL,
    Address VARCHAR(255),
    PhoneNumber VARCHAR(20),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Rooms table
CREATE TABLE Rooms (
    RoomID SERIAL PRIMARY KEY,
    RoomCode VARCHAR(20) NOT NULL UNIQUE,
    RoomName VARCHAR(100) NOT NULL,
    RoomType VARCHAR(50) NOT NULL, -- gym, yoga, etc.
    Description VARCHAR(500),
    Status room_status_enum DEFAULT 'Hoạt động',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CenterID INT REFERENCES FitnessCenter(CenterID) ON DELETE CASCADE
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
    CenterID INT NOT NULL,
    CONSTRAINT fk_center FOREIGN KEY (CenterID) REFERENCES FitnessCenter(CenterID) ON DELETE CASCADE,
    CONSTRAINT uq_equipment_code_per_center UNIQUE (CenterID, EquipmentCode)
);
