package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Thông tin dịch vụ trả về cho client")
public record ServiceResponse(@Schema(description = "ID dịch vụ", example = "10") Integer id,

        @Schema(description = "ID chuyên khoa", example = "1") Long specialtyId,

        @Schema(description = "Tên chuyên khoa", example = "Nội tổng quát") String specialtyName,

        @Schema(description = "Tên dịch vụ", example = "Khám tổng quát") String name,

        @Schema(description = "Mô tả dịch vụ", example = "Khám lâm sàng cơ bản") String description,

        @Schema(description = "Thời lượng dịch vụ (phút)", example = "30") Integer durationMinutes,

        @Schema(description = "Giá dịch vụ", example = "150000") BigDecimal price) {
}
