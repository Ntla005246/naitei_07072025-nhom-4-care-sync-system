-- Seed Specialties
INSERT INTO specialties (name, description, created_at, updated_at) VALUES
('Cardiology', 'Chuyên khoa Tim mạch', NOW(), NOW()),
('Pediatrics', 'Chuyên khoa Nhi', NOW(), NOW()),
('Dermatology', 'Chuyên khoa Da liễu', NOW(), NOW()),
('Orthopedics', 'Chấn thương chỉnh hình', NOW(), NOW()),
('Neurology', 'Thần kinh', NOW(), NOW());

-- Seed Users for Doctors (địa điểm đặt ở address)
INSERT INTO users (full_name, email, phone_number, password, gender, date_of_birth, address, is_active, created_at, updated_at) VALUES
('Dr. An',   'an.cardiology@example.com',   '0901000001', 'password-hash', NULL, NULL, 'Hanoi - Ba Dinh',     TRUE, NOW(), NOW()),
('Dr. Binh', 'binh.pediatrics@example.com', '0901000002', 'password-hash', NULL, NULL, 'Hanoi - Hoan Kiem',   TRUE, NOW(), NOW()),
('Dr. Chi',  'chi.derma@example.com',       '0901000003', 'password-hash', NULL, NULL, 'Ho Chi Minh - District 1', TRUE, NOW(), NOW()),
('Dr. Dung', 'dung.ortho@example.com',      '0901000004', 'password-hash', NULL, NULL, 'Da Nang - Hai Chau',  TRUE, NOW(), NOW()),
('Dr. Em',   'em.neuro@example.com',        '0901000005', 'password-hash', NULL, NULL, 'Hanoi - Cau Giay',    TRUE, NOW(), NOW());

-- Map Users -> DOCTOR role (không bắt buộc cho endpoint public nhưng hữu ích)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_name = 'DOCTOR'
WHERE u.email IN (
  'an.cardiology@example.com',
  'binh.pediatrics@example.com',
  'chi.derma@example.com',
  'dung.ortho@example.com',
  'em.neuro@example.com'
);

-- Seed Doctors (map user_id và specialty_id qua subquery theo email/name)
INSERT INTO doctors (user_id, specialty_id, title, experience_years, bio, consultation_fee, created_at, updated_at) VALUES
((SELECT id FROM users WHERE email='an.cardiology@example.com'),
 (SELECT id FROM specialties WHERE name='Cardiology'),
 'BSCKII', 12, 'Kinh nghiệm tim mạch tổng quát', 300000, NOW(), NOW()),

((SELECT id FROM users WHERE email='binh.pediatrics@example.com'),
 (SELECT id FROM specialties WHERE name='Pediatrics'),
 'ThS.BS', 8, 'Khám và tư vấn nhi khoa', 250000, NOW(), NOW()),

((SELECT id FROM users WHERE email='chi.derma@example.com'),
 (SELECT id FROM specialties WHERE name='Dermatology'),
 'BSCKI', 9, 'Da liễu thẩm mỹ và điều trị', 280000, NOW(), NOW()),

((SELECT id FROM users WHERE email='dung.ortho@example.com'),
 (SELECT id FROM specialties WHERE name='Orthopedics'),
 'BS', 7, 'Chấn thương chỉnh hình', 270000, NOW(), NOW()),

((SELECT id FROM users WHERE email='em.neuro@example.com'),
 (SELECT id FROM specialties WHERE name='Neurology'),
 'BSCKI', 10, 'Thần kinh người lớn', 320000, NOW(), NOW());