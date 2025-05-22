-- Tạo bảng users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'member'
);

-- Thêm chỉ mục để tối ưu tìm kiếm
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- Thêm một số dữ liệu mẫu
INSERT INTO users (username, password, email, role)
VALUES 
    ('admin1', 'admin123', 'admin@gym.com', 'admin'),
    ('member', 'member123', 'member1@example.com', 'member'),
    ('trainer', 'trainer123', 'trainer1@gym.com', 'trainer');
