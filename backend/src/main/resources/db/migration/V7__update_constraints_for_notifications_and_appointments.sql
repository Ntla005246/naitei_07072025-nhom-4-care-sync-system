-- Update CHECK constraints for MySQL 8+

-- Drop existing appointment status check and add REJECTED
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_appointment_status;
ALTER TABLE appointments
  ADD CONSTRAINT chk_appointment_status
  CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','COMPLETED','REJECTED'));

-- Drop existing notification type check and extend values
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS chk_notification_type;
ALTER TABLE notifications
  ADD CONSTRAINT chk_notification_type
  CHECK (type IN (
    'APPOINTMENT_REMINDER',
    'APPOINTMENT_CONFIRMATION',
    'APPOINTMENT_CREATED',
    'APPOINTMENT_CONFIRMED',
    'APPOINTMENT_REJECTED',
    'APPOINTMENT_COMPLETED',
    'DOCTOR_NEW_APPOINTMENT_REQUEST',
    'DOCTOR_APPOINTMENT_CANCELLED',
    'TIME_OFF_APPROVED',
    'TIME_OFF_REJECTED',
    'PAYMENT_SUCCESS',
    'GENERAL_ANNOUNCEMENT'
  )); 