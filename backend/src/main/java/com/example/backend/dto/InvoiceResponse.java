package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.constant.enums.InvoiceStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response thông tin invoice")
public record InvoiceResponse(@Schema(description = "ID của invoice", example = "2001") Long id,

        @Schema(description = "Mã invoice", example = "INV-2024-001") String invoiceCode,

        @Schema(description = "ID của appointment", example = "1001") Long appointmentId,

        @Schema(description = "Tổng số tiền", example = "500000.00") BigDecimal totalAmount,

        @Schema(description = "Trạng thái invoice", example = "PENDING") InvoiceStatus status,

        @Schema(description = "Ngày phát hành") LocalDateTime issuedDate,

        @Schema(description = "Thông tin patient") PatientInfo patientInfo,

        @Schema(description = "Thông tin doctor") DoctorInfo doctorInfo,

        @Schema(description = "Danh sách services") List<ServiceItem> services,

        @Schema(description = "Ngày tạo") LocalDateTime createdAt,

        @Schema(description = "Ngày cập nhật") LocalDateTime updatedAt) {

    @Schema(description = "Thông tin patient")
    public record PatientInfo(Long id, String fullName, String email, String phoneNumber) {
    }

    @Schema(description = "Thông tin doctor")
    public record DoctorInfo(Long id, String fullName, String specialty,
            BigDecimal consultationFee) {
    }

    @Schema(description = "Thông tin service")
    public record ServiceItem(Long id, String name, BigDecimal price) {
    }
}
