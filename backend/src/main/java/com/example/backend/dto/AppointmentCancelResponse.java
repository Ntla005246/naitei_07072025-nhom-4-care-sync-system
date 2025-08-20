package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Kết quả huỷ lịch hẹn")
public record AppointmentCancelResponse(@Schema(example = "5001") Long appointmentId,
        @Schema(example = "PENDING") String oldStatus,
        @Schema(example = "CANCELLED") String newStatus,
        @Schema(description = "Đã nhả slot về AVAILABLE hay chưa") boolean slotReleased,
        @Schema(description = "Thông báo/Chính sách") String policyMessage,
        @Schema(description = "Đã gửi thông báo hay chưa") boolean notificationQueued) {
}
