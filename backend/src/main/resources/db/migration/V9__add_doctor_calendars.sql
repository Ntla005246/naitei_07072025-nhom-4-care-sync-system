-- =================================================================
-- DOCTOR CALENDAR INTEGRATION
-- =================================================================
CREATE TABLE IF NOT EXISTS doctor_calendars (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL UNIQUE,
    calendar_type VARCHAR(20) NOT NULL,
    calendar_id VARCHAR(255),
    access_token TEXT,
    refresh_token TEXT,
    token_expiry DATETIME,
    last_event_id VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    CONSTRAINT chk_calendar_type CHECK (calendar_type IN ('GOOGLE', 'OUTLOOK'))
); 