-- =================================================================
-- ROLES & USERS
-- =================================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT chk_role_name CHECK (role_name IN ('PATIENT', 'DOCTOR', 'ADMIN', 'RECEPTIONIST'))
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    password VARCHAR(255) NOT NULL,
    gender VARCHAR(10),
    date_of_birth DATE,
    address TEXT,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =================================================================
-- SPECIALTIES & SERVICES
-- =================================================================
CREATE TABLE IF NOT EXISTS specialties (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    specialty_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE CASCADE
);

-- =================================================================
-- PATIENTS & DOCTORS
-- =================================================================
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    health_insurance_code VARCHAR(100) UNIQUE,
    medical_history TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    specialty_id INT,
    title VARCHAR(255),
    experience_years INT,
    bio TEXT,
    consultation_fee DECIMAL(10, 2),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE SET NULL
);

-- =================================================================
-- DOCTOR SCHEDULING
-- =================================================================
CREATE TABLE IF NOT EXISTS doctor_working_hours (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    day_of_week INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS doctor_time_off (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    reason TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- =================================================================
-- APPOINTMENTS
-- =================================================================
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT chk_appointment_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointment_slots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(50),
    appointment_id BIGINT UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    CONSTRAINT chk_appointment_slot_status CHECK (status IN ('AVAILABLE', 'BOOKED'))
);

CREATE TABLE IF NOT EXISTS appointment_services (
    appointment_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    price_at_booking DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    PRIMARY KEY (appointment_id, service_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- =================================================================
-- BILLING & PAYMENTS
-- =================================================================
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL UNIQUE,
    invoice_code VARCHAR(100) NOT NULL UNIQUE,
    final_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50),
    issued_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT chk_invoice_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(50),
    transaction_code VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CREDIT_CARD', 'CASH', 'BANK_TRANSFER')),
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'SUCCESSFUL', 'FAILED'))
);

-- =================================================================
-- REVIEWS
-- =================================================================
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL UNIQUE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- =================================================================
-- SYSTEM & LOGGING
-- =================================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    content TEXT,
    type VARCHAR(50),
    status VARCHAR(50),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_notification_type CHECK (type IN ('APPOINTMENT_REMINDER', 'APPOINTMENT_CONFIRMATION', 'PAYMENT_SUCCESS', 'GENERAL_ANNOUNCEMENT')),
    CONSTRAINT chk_notification_status CHECK (status IN ('UNREAD', 'READ'))
);

CREATE TABLE IF NOT EXISTS login_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    ip_address VARCHAR(45),
    device_info TEXT,
    login_time DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action_type VARCHAR(100) NOT NULL,
    target_entity VARCHAR(100),
    target_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_audit_action_type CHECK (action_type IN ('CREATE', 'UPDATE', 'DELETE', 'LOGIN_SUCCESS', 'LOGIN_FAILURE'))
);

-- =================================================================
-- INITIAL DATA
-- =================================================================
INSERT INTO roles (role_name, created_at, updated_at) VALUES
('PATIENT', NOW(), NOW()),
('DOCTOR', NOW(), NOW()),
('ADMIN', NOW(), NOW()),
('RECEPTIONIST', NOW(), NOW());

-- =================================================================
-- INDEXES FOR PERFORMANCE
-- =================================================================
-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Role indexes
CREATE INDEX idx_roles_name ON roles(role_name);

-- Patient indexes
CREATE INDEX idx_patients_user_id ON patients(user_id);
CREATE INDEX idx_patients_insurance_code ON patients(health_insurance_code);

-- Doctor indexes
CREATE INDEX idx_doctors_user_id ON doctors(user_id);
CREATE INDEX idx_doctors_specialty ON doctors(specialty_id);

-- Specialty indexes
CREATE INDEX idx_specialties_name ON specialties(name);

-- Service indexes
CREATE INDEX idx_services_specialty ON services(specialty_id);
CREATE INDEX idx_services_name ON services(name);

-- Appointment indexes
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_created_at ON appointments(created_at);

-- Appointment slot indexes
CREATE INDEX idx_appointment_slots_doctor ON appointment_slots(doctor_id);
CREATE INDEX idx_appointment_slots_time ON appointment_slots(start_time, end_time);
CREATE INDEX idx_appointment_slots_status ON appointment_slots(status);

-- Doctor working hours indexes
CREATE INDEX idx_working_hours_doctor ON doctor_working_hours(doctor_id);
CREATE INDEX idx_working_hours_day ON doctor_working_hours(day_of_week);

-- Doctor time off indexes
CREATE INDEX idx_time_off_doctor ON doctor_time_off(doctor_id);
CREATE INDEX idx_time_off_datetime ON doctor_time_off(start_datetime, end_datetime);

-- Invoice indexes
CREATE INDEX idx_invoices_appointment ON invoices(appointment_id);
CREATE INDEX idx_invoices_code ON invoices(invoice_code);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_created_at ON invoices(created_at);

-- Payment indexes
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);

-- Review indexes
CREATE INDEX idx_reviews_appointment ON reviews(appointment_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- Notification indexes
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- Login history indexes
CREATE INDEX idx_login_history_user ON login_history(user_id);
CREATE INDEX idx_login_history_time ON login_history(login_time);

-- Audit logs indexes
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action_type);
CREATE INDEX idx_audit_logs_target ON audit_logs(target_entity, target_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);