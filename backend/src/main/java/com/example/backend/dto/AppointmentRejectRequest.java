package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Yêu cầu hủy lịch hẹn (bác sĩ/ADMIN)")
public record AppointmentRejectRequest(
        @Schema(example = "Bác sĩ bận công tác đột xuất") @NotBlank @Size(min = 10, max = 200) String reason) {
}
