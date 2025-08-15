package com.example.backend.dto;

import com.example.backend.constant.MessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request DTO for creating doctor time off")
public record DoctorTimeOffRequestDto(
        @Schema(description = "Doctor ID", example = "1") @NotNull(message = MessageConstants.VALIDATION_TIME_OFF_DOCTOR_REQUIRED) Long doctorId,

        @Schema(description = "Start datetime (YYYY-MM-DD or YYYY-MM-DD HH:mm)", example = "2025-01-15 14:00") @NotBlank(message = MessageConstants.VALIDATION_TIME_OFF_START_DATETIME_REQUIRED) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2})?", message = MessageConstants.VALIDATION_TIME_OFF_DATETIME_FORMAT) String startDatetime,

        @Schema(description = "End datetime (YYYY-MM-DD or YYYY-MM-DD HH:mm)", example = "2025-01-15 16:00") @NotBlank(message = MessageConstants.VALIDATION_TIME_OFF_END_DATETIME_REQUIRED) @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2})?", message = MessageConstants.VALIDATION_TIME_OFF_DATETIME_FORMAT) String endDatetime,

        @Schema(description = "Reason for time off", example = "Họp quan trọng") @NotBlank(message = MessageConstants.VALIDATION_TIME_OFF_REASON_REQUIRED) String reason) {
}
