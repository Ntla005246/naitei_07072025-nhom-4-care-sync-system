package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Yêu cầu để cập nhật dịch vụ y tế")
public record ServiceUpdateRequest(
        @Schema(description = "ID chuyên khoa mới", example = "2") Integer specialtyId,

        @Schema(description = "Tên dịch vụ mới", example = "Khám tổng quát nâng cao") String name,

        @Schema(description = "Mô tả dịch vụ mới", example = "Có xét nghiệm cơ bản") String description,

        @Schema(description = "Thời lượng mới (phút)", example = "45") @Positive Integer durationMinutes,

        @Schema(description = "Giá mới", example = "200000") @DecimalMin(value = "0.0", inclusive = true) BigDecimal price) {
}
