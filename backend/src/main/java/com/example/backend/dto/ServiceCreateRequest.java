package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Yêu cầu tạo dịch vụ y tế mới")
public record ServiceCreateRequest(
        @Schema(description = "ID của chuyên khoa", example = "1") @NotNull Integer specialtyId,

        @Schema(description = "Tên dịch vụ", example = "Khám tổng quát") @NotBlank String name,

        @Schema(description = "Mô tả dịch vụ", example = "Khám lâm sàng cơ bản") String description,

        @Schema(description = "Thời lượng dịch vụ (phút)", example = "30") @Positive int durationMinutes,

        @Schema(description = "Giá dịch vụ", example = "150000") @DecimalMin(value = "0.0", inclusive = true) BigDecimal price) {
}
