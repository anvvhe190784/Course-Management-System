-- =========================================================
-- INITIAL DATA FOR COURSE MANAGEMENT SYSTEM
-- =========================================================

-- 1. Tài khoản mặc định:
-- Administrator (Mật khẩu: admin123 -> SHA-256: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9)
INSERT INTO student (name, dob, email, sex, phone, role, password, create_at)
VALUES ('System Administrator', '1990-01-01', 'admin@cms.local', TRUE, '0000000000', 'ADMIN', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', CURRENT_DATE)
ON CONFLICT (email) DO NOTHING;

-- Học viên mẫu (Mật khẩu: 123456 -> SHA-256: 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92)
INSERT INTO student (name, dob, email, sex, phone, role, password, create_at)
VALUES 
('Nguyen Van An', '2002-05-15', 'an.nguyen@gmail.com', TRUE, '0912345678', 'STUDENT', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', CURRENT_DATE),
('Tran Thi Binh', '2003-08-20', 'binh.tran@gmail.com', FALSE, '0987654321', 'STUDENT', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', CURRENT_DATE),
('Le Hoang Nam', '2001-11-10', 'nam.le@gmail.com', TRUE, '0905123456', 'STUDENT', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', CURRENT_DATE)
ON CONFLICT (email) DO NOTHING;

-- 2. Khóa học mẫu
INSERT INTO course (name, duration, instructor, create_at)
VALUES 
('Database Fundamentals', 45, 'Tran Thi B', CURRENT_DATE),
('Web Development Basics', 50, 'Le Van C', CURRENT_DATE),
('Object Oriented Programming', 55, 'Nguyen Van A', CURRENT_DATE),
('PostgreSQL Practical', 40, 'Tran Thi B', CURRENT_DATE),
('Advanced Java & Stream API', 60, 'Nguyen Van A', CURRENT_DATE)
ON CONFLICT DO NOTHING;

