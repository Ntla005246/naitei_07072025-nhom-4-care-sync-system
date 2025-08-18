-- Seed services (giá tại services.price)
-- Cardiology services
INSERT INTO services (specialty_id, name, description, duration_minutes, price, created_at, updated_at)
SELECT s.id, 'Khám tim mạch tổng quát', 'Khám & tư vấn', 60, 250000.00, NOW(), NOW()
FROM specialties s WHERE s.name = 'Cardiology';

INSERT INTO services (specialty_id, name, description, duration_minutes, price, created_at, updated_at)
SELECT s.id, 'Điện tâm đồ (ECG)', 'Ghi điện tim', 30, 300000.00, NOW(), NOW()
FROM specialties s WHERE s.name = 'Cardiology';

-- Pediatrics services
INSERT INTO services (specialty_id, name, description, duration_minutes, price, created_at, updated_at)
SELECT s.id, 'Khám nhi tổng quát', 'Khám & tư vấn', 45, 220000.00, NOW(), NOW()
FROM specialties s WHERE s.name = 'Pediatrics';

-- Seed vài appointment slots AVAILABLE trong tương lai
-- Dr. An (Cardiology) - 2 slots ngày mai
INSERT INTO appointment_slots (doctor_id, start_time, end_time, status, created_at, updated_at)
SELECT d.id, DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d 09:00:00'), INTERVAL 1 DAY), DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d 10:00:00'), INTERVAL 1 DAY), 'AVAILABLE', NOW(), NOW()
FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'an.cardiology@example.com';

INSERT INTO appointment_slots (doctor_id, start_time, end_time, status, created_at, updated_at)
SELECT d.id, DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d 10:00:00'), INTERVAL 1 DAY), DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d 11:00:00'), INTERVAL 1 DAY), 'AVAILABLE', NOW(), NOW()
FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'an.cardiology@example.com';

-- Dr. Binh (Pediatrics) - 1 slot ngày kia
INSERT INTO appointment_slots (doctor_id, start_time, end_time, status, created_at, updated_at)
SELECT d.id, DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d 09:00:00'), INTERVAL 2 DAY), DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d 10:00:00'), INTERVAL 2 DAY), 'AVAILABLE', NOW(), NOW()
FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'binh.pediatrics@example.com';