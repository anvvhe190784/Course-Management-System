-- =========================================================
-- DATABASE SCHEMA FOR COURSE MANAGEMENT SYSTEM (POSTGRESQL)
-- =========================================================

-- 1. Bảng Khóa học (course)
CREATE TABLE IF NOT EXISTS course (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    instructor VARCHAR(100) NOT NULL,
    create_at DATE DEFAULT CURRENT_DATE,
    CONSTRAINT ck_course_duration CHECK (duration > 0)
);

-- 2. Bảng Học viên & Quản trị viên (student)
CREATE TABLE IF NOT EXISTS student (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    sex BOOLEAN NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'STUDENT',
    password VARCHAR(255) NOT NULL,
    create_at DATE DEFAULT CURRENT_DATE,
    CONSTRAINT ck_student_role CHECK (role IN ('ADMIN', 'STUDENT'))
);

-- 3. Bảng Đăng ký khóa học (enrollment)
CREATE TABLE IF NOT EXISTS enrollment (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    course_id INT NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'WAITING',
    CONSTRAINT uk_student_course UNIQUE (student_id, course_id),
    CONSTRAINT ck_enrollment_status CHECK (status IN ('WAITING', 'DENIED', 'CANCEL', 'CONFIRM'))
);

-- Chỉ mục tối ưu truy vấn
CREATE INDEX IF NOT EXISTS idx_enrollment_course_status ON enrollment(course_id, status);
CREATE INDEX IF NOT EXISTS idx_enrollment_status ON enrollment(status);

