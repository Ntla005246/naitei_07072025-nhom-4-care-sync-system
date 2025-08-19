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
    public static final String EMAIL_ALREADY_EXISTS = "Lỗi: Email đã được sử dụng!";
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String APPOINTMENT_NOT_AVAILABLE = "Selected time slot is not available.";
    public static final String APPOINTMENT_CANCEL_FAILED = "Appointment cannot be cancelled.";
    public static final String APPOINTMENT_RESCHEDULE_FAILED = "Appointment cannot be rescheduled.";
    public static final String ROLE_NOT_FOUND = "Lỗi: Không tìm thấy vai trò 'PATIENT'.";

    // Specialty Messages
    public static final String SPECIALTY_NOT_FOUND = "Specialty not found.";
    public static final String SPECIALTY_SEARCH_SUCCESS = "Specialty search completed successfully.";

    // Time Off Messages
    public static final String DOCTOR_TIME_OFF_CREATED_SUCCESS = "Thêm lịch nghỉ thành công.";
    public static final String DOCTOR_TIME_OFF_NOT_FOUND = "Lịch nghỉ không tồn tại.";
    public static final String TIME_OFF_CONFLICT = "Time off conflicts with existing schedule.";

    // Validation Messages
    public static final String VALIDATION_PAGE_NUMBER_MIN = "Page number must be >= 0.";
    public static final String VALIDATION_PAGE_SIZE_MIN = "Page size must be >= 1.";
    public static final String VALIDATION_SORT_FIELD_INVALID = "Invalid sort field. Allowed values: name, description, createdAt.";
    public static final String VALIDATION_SORT_DIRECTION_INVALID = "Sort direction must be 'asc' or 'desc'.";
    public static final String VALIDATION_TIME_OFF_START_AFTER_END = "Start time must be before end time.";
    public static final String VALIDATION_TIME_OFF_START_IN_PAST = "Start time cannot be in the past.";
    public static final String VALIDATION_TIME_OFF_DOCTOR_REQUIRED = "Doctor is required.";
    public static final String VALIDATION_TIME_OFF_REASON_REQUIRED = "Reason is required.";
    public static final String VALIDATION_TIME_OFF_START_DATETIME_REQUIRED = "Start datetime is required.";
    public static final String VALIDATION_TIME_OFF_END_DATETIME_REQUIRED = "End datetime is required.";
    public static final String VALIDATION_TIME_OFF_DATETIME_FORMAT = "Invalid datetime format. Use 'YYYY-MM-DD' or 'YYYY-MM-DD HH:mm'.";

    // Specialty DTO Validation Messages
    public static final String VALIDATION_SPECIALTY_ID_POSITIVE = "Specialty ID must be positive.";
    public static final String VALIDATION_SPECIALTY_NAME_NOT_EMPTY = "Specialty name cannot be empty.";

    // Email Subjects
    public static final String EMAIL_SUBJECT_APPOINTMENT_CONFIRMATION = "Xác nhận lịch hẹn khám bệnh";
    public static final String EMAIL_SUBJECT_APPOINTMENT_REMINDER = "Nhắc nhở lịch hẹn khám bệnh";
    public static final String EMAIL_SUBJECT_PASSWORD_RESET = "Yêu cầu đặt lại mật khẩu";

    // Generic Error Messages
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";

    // Validation Messages
    public static final String USERNAME_REQUIRED = "Tên đăng nhập là bắt buộc";
    public static final String EMAIL_REQUIRED = "Email là bắt buộc";
    public static final String PASSWORD_REQUIRED = "Mật khẩu là bắt buộc";
    public static final String PASSWORD_MIN_LENGTH = "Mật khẩu phải có ít nhất 8 ký tự";
    public static final String FULLNAME_REQUIRED = "Họ và tên là bắt buộc";

    // Auth / Validation
    public static final String EMAIL_NOT_BLANK = "Email không được để trống.";
    public static final String EMAIL_INVALID = "Email không đúng định dạng.";
    public static final String PASSWORD_NOT_BLANK = "Mật khẩu không được để trống.";

    // INVALID_CREDENTIALS :
    public static final String INVALID_EMAIL_OR_PASSWORD = "Email hoặc mật khẩu không đúng.";

    //
    public static final String EMAIL_SUBJECT_EMAIL_VERIFICATION = "Vui lòng xác thực tài khoản của bạn.";
    public static final String EMAIL_SENDING_FAILED = "Gửi email thất bại.";

    // HTML/PlainText responses
    public static final String VERIFY_EMAIL_SUCCESS_HTML = "<h1>Xác thực tài khoản thành công!</h1><p>Bạn có thể đóng cửa sổ này và đăng nhập vào ứng dụng.</p>";

    // Token verify
    public static final String TOKEN_INVALID = "Token không hợp lệ.";
    public static final String TOKEN_EXPIRED_CODE = "TOKEN_EXPIRED";
    public static final String TOKEN_EXPIRED_MESSAGE = "Token xác thực hết hạn.";

}
