package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Yêu cầu tạo lịch hẹn từ một slot đang AVAILABLE")
public record AppointmentCreateRequest(
        @Schema(description = "ID slot đã chọn", example = "1001") @NotNull Long slotId,
        @Schema(description = "Danh sách ID dịch vụ cần khám", example = "[3001, 3002]") @NotEmpty List<Long> serviceIds,
        @Schema(description = "Ghi chú", example = "Đau ngực vài ngày gần đây") String notes) {
}
