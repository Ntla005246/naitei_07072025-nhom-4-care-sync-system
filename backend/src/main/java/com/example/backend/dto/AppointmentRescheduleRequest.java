package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu đổi lịch hẹn sang một slot mới còn trống")
public record AppointmentRescheduleRequest(
        @Schema(description = "ID slot mới (phải còn AVAILABLE)", example = "1105") @NotNull Long newSlotId,

        @Schema(description = "ID bệnh nhân đang yêu cầu (chỉ cần nếu người thực hiện là patient). "
                + "Dùng để xác thực họ là chủ của cuộc hẹn.", example = "2001") Long patientId,

        @Schema(description = "Lý do thay đổi", example = "Bận công việc đột xuất") String reason,

        @Schema(description = "Xác nhận đã đọc chính sách thay đổi", example = "true") @NotNull Boolean confirmPolicy) {
}
