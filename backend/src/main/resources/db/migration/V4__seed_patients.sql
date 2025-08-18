-- Users cho bệnh nhân
INSERT INTO users (full_name, email, phone_number, password, gender, date_of_birth, address, is_active, created_at, updated_at)
VALUES
('Nguyen Van A', 'patient1@example.com', '0902000001', 'password-hash', 'MALE', '1995-05-10', 'Hanoi - Dong Da', TRUE, NOW(), NOW()),
('Tran Thi B',  'patient2@example.com', '0902000002', 'password-hash', 'FEMALE', '1992-08-20', 'HCMC - Thu Duc', TRUE, NOW(), NOW());

-- Gán role PATIENT
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_name = 'PATIENT'
WHERE u.email IN ('patient1@example.com', 'patient2@example.com');

-- Tạo bản ghi patients (1-1 với users)
INSERT INTO patients (user_id, health_insurance_code, medical_history, emergency_contact_name, emergency_contact_phone, created_at, updated_at)
SELECT u.id, 'HI-PA-0001', 'No known allergies', 'Le Van C', '0903000001', NOW(), NOW()
FROM users u WHERE u.email = 'patient1@example.com';

INSERT INTO patients (user_id, health_insurance_code, medical_history, emergency_contact_name, emergency_contact_phone, created_at, updated_at)
SELECT u.id, 'HI-PA-0002', 'Asthma', 'Pham Thi D', '0903000002', NOW(), NOW()
FROM users u WHERE u.email = 'patient2@example.com';