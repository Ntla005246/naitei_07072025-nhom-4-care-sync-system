package com.example.backend.util;

import com.example.backend.constant.MessageConstants;
import com.example.backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@Slf4j
public class TimeOffValidator {

    /**
     * Parse datetime string thành LocalDateTime Hỗ trợ 2 format: - "2025-01-15" ->
     * Nghỉ cả ngày (00:00 - 23:59) - "2025-01-15 14:00" -> Nghỉ theo giờ
     */
    public LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            if (dateTimeStr.length() == 10) {
                // Format: "2025-01-15" -> Nghỉ cả ngày
                LocalDate date = LocalDate.parse(dateTimeStr);
                return date.atStartOfDay();
            } else {
                // Format: "2025-01-15 14:00" -> Nghỉ theo giờ
                return LocalDateTime.parse(dateTimeStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
        } catch (DateTimeParseException e) {
            throw new BusinessException("VALIDATION_ERROR", "Invalid datetime format: "
                    + dateTimeStr + ". Use 'YYYY-MM-DD' or 'YYYY-MM-DD HH:mm'");
        }
    }

    /**
     * Validate business logic cho time off request
     */
    public void validateTimeOffRequest(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Validation: start time phải trước end time
        if (startDateTime.isAfter(endDateTime)) {
            throw new BusinessException("VALIDATION_ERROR",
                    MessageConstants.VALIDATION_TIME_OFF_START_AFTER_END);
        }

        // Validation: start time không được trong quá khứ
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("VALIDATION_ERROR",
                    MessageConstants.VALIDATION_TIME_OFF_START_IN_PAST);
        }

        // Validation: Nếu nghỉ cả ngày, end time sẽ là 23:59:59
        if (startDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                && endDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            // Đây là nghỉ cả ngày, không cần validation thêm
            return;
        }

        // Validation: Khoảng thời gian nghỉ phải hợp lý (ít nhất 30 phút)
        if (startDateTime.plusMinutes(30).isAfter(endDateTime)) {
            throw new BusinessException("VALIDATION_ERROR",
                    "Time off duration must be at least 30 minutes");
        }
    }
}
