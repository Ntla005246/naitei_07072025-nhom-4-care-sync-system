package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.backend.constant.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for payment operations")
public record PaymentResponse(
        @Schema(description = "Payment ID")
        Long id,

        @Schema(description = "Invoice ID")
        Long invoiceId,

        @Schema(description = "Payment amount")
        BigDecimal amount,

        @Schema(description = "Payment method")
        String paymentMethod,

        @Schema(description = "Payment status")
        PaymentStatus status,

        @Schema(description = "Transaction code")
        String transactionCode,

        @Schema(description = "Payment URL for redirection")
        String paymentUrl,

        @Schema(description = "Payment creation time")
        LocalDateTime createdAt
) {} 
