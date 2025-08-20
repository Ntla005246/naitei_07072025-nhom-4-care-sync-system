package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu huỷ lịch hẹn")
public record AppointmentCancelRequest(
        @Schema(description = "ID bệnh nhân đang yêu cầu(nếu người gọi là bệnh nhân, dùng để xác thực chủ sỡ hữu", example = "2001") Long patientId,

        @Schema(description = "Lý do huỷ", example = "Bận việc đột xuất") String reason,

        @Schema(description = "Xác nhận đã đọc chính sách huỷ", example = "true") @NotNull Boolean confirmPolicy) {
}
