package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Kết quả tạo lịch hẹn")
public record AppointmentCreateResponse(@Schema(example = "5001") Long appointmentId,
        @Schema(example = "2001") Long patientId, SlotInfo slot, DoctorInfo doctor,
        @Schema(example = "PENDING") String status, List<ServiceItem> services,
        @Schema(example = "550000.00") BigDecimal totalPrice,
        @Schema(example = "Đau ngực vài ngày gần đây") String notes) {
    @Schema(description = "Thông tin slot đã đặt")
    public record SlotInfo(@Schema(example = "2025-01-21T09:00:00") LocalDateTime startTime,
            @Schema(example = "2025-01-21T10:00:00") LocalDateTime endTime,
            @Schema(example = "101") Long doctorId) {
    }
    @Schema(description = "Thông tin bác sĩ tóm tắt")
    public record DoctorInfo(@Schema(example = "101") Long id,
            @Schema(example = "BS. An") String fullName, @Schema(example = "1") Long specialtyId,
            @Schema(example = "Cardiology") String specialtyName) {
    }
    @Schema(description = "Dịch vụ và giá tại thời điểm đặt")
    public record ServiceItem(@Schema(example = "3001") Long serviceId,
            @Schema(example = "Khám tim mạch tổng quát") String name,
            @Schema(example = "250000.00") BigDecimal priceAtBooking) {
    }
}
