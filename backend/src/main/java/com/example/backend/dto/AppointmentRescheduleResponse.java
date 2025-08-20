package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Kết quả đổi lịch hẹn")
public record AppointmentRescheduleResponse(@Schema(example = "5001") Long appointmentId,
        SlotInfo oldSlot, SlotInfo newSlot, @Schema(example = "PENDING") String status,
        @Schema(description = "Thông báo/Chính sách") String policyMessage,
        @Schema(description = "Đã gửi thông báo hay chưa") boolean notificationQueued) {
    @Schema(description = "Thông tin slot tóm tắt")
    public record SlotInfo(@Schema(example = "2025-01-21T09:00:00") LocalDateTime startTime,
            @Schema(example = "2025-01-21T10:00:00") LocalDateTime endTime,
            @Schema(example = "101") Long doctorId) {
    }
}
