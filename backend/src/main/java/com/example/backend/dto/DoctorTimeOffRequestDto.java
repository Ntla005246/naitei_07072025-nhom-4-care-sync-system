package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request DTO for creating doctor time off")
public record DoctorTimeOffRequestDto(
        @Schema(description = "Start datetime (YYYY-MM-DD or YYYY-MM-DD HH:mm)", example = "2025-01-15 14:00") @NotBlank(message = "{validation.time.off.start.datetime.required}") @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2})?", message = "{validation.time.off.datetime.format}") String startDatetime,

        @Schema(description = "End datetime (YYYY-MM-DD or YYYY-MM-DD HH:mm)", example = "2025-01-15 16:00") @NotBlank(message = "{validation.time.off.end.datetime.required}") @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2})?", message = "{validation.time.off.datetime.format}") String endDatetime,

        @Schema(description = "Reason for time off", example = "Họp quan trọng") @NotBlank(message = "{validation.time.off.reason.required}") String reason) {
}
