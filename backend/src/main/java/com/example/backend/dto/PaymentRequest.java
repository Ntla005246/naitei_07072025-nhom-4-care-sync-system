package com.example.backend.dto;

import com.example.backend.constant.enums.PaymentMethod;
import com.example.backend.util.ValidUrl;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request DTO for creating payment")
public record PaymentRequest(
        @Schema(description = "Invoice ID", example = "1") @NotNull(message = "Invoice ID is required") @Positive(message = "Invoice ID must be a positive number") Long invoiceId,

        @Schema(description = "Payment method", example = "MOMO") @NotNull(message = "Payment method is required") PaymentMethod paymentMethod,

        @Schema(description = "Return URL after payment", example = "https://app.healthbooking.com/payment/success") @ValidUrl(message = "Return URL must be a valid HTTP/HTTPS URL with proper domain and format") String returnUrl) {
}
