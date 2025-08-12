package com.example.backend.constant;

public final class MessageConstants {
    private MessageConstants() {
    }

    // API Response Messages
    public static final String SUCCESS_MESSAGE = "Operation successful.";
    public static final String ERROR_MESSAGE = "Operation failed.";
    public static final String RESOURCE_NOT_FOUND = "Resource not found.";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access.";
    public static final String VALIDATION_ERROR = "Validation failed.";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists.";
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String APPOINTMENT_NOT_AVAILABLE = "Selected time slot is not available.";
    public static final String APPOINTMENT_CANCEL_FAILED = "Appointment cannot be cancelled.";
    public static final String APPOINTMENT_RESCHEDULE_FAILED = "Appointment cannot be rescheduled.";

    // Email Subjects
    public static final String EMAIL_SUBJECT_APPOINTMENT_CONFIRMATION = "Xác nhận lịch hẹn khám bệnh";
    public static final String EMAIL_SUBJECT_APPOINTMENT_REMINDER = "Nhắc nhở lịch hẹn khám bệnh";
    public static final String EMAIL_SUBJECT_PASSWORD_RESET = "Yêu cầu đặt lại mật khẩu";

    // Generic Error Messages
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
}
