package com.example.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.constant.ApiConstants;
import com.example.backend.constant.MockConstants;
import com.example.backend.constant.enums.PaymentMethod;
import com.example.backend.constant.enums.PaymentStatus;
import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.PaymentRequest;
import com.example.backend.dto.PaymentResponse;
import com.example.backend.service.PaymentFactoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(ApiConstants.PAYMENTS_ENDPOINT)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private static final String MOMO_PREFIX = "MOMO_";
    private static final String VNPAY_PREFIX = "VNPAY_";
    private static final DateTimeFormatter TRANSACTION_CODE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmssSSS");

    private final PaymentFactoryService paymentFactoryService;
    private final MessageSource messageSource;

    @PostMapping("/create")
    @Operation(summary = "Create payment", description = "Create a new payment request for an invoice")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Creating payment request: invoiceId={}, method={}", request.invoiceId(),
                request.paymentMethod());

        PaymentResponse response = paymentFactoryService.createPayment(request);
        String message = messageSource.getMessage("success.payment.created", null,
                LocaleContextHolder.getLocale());

        return ApiResponse.success(response, message);
    }

    @GetMapping("/{paymentId}/status")
    @Operation(summary = "Get payment status", description = "Get payment status by payment ID")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ApiResponse<PaymentStatus> getPaymentStatus(@PathVariable Long paymentId) {
        log.info("Getting payment status for payment ID: {}", paymentId);

        PaymentStatus status = paymentFactoryService.getPaymentStatusById(paymentId);
        String message = messageSource.getMessage("success.payment.status.retrieved", null,
                LocaleContextHolder.getLocale());

        return ApiResponse.success(status, message);
    }

    @PostMapping("/momo/callback")
    @Operation(summary = "MoMo payment callback", description = "Handle MoMo payment callback")
    public ApiResponse<String> momoCallback(@RequestBody String callbackData) {
        log.info("Received MoMo callback: {}", callbackData);

        String transactionCode = generateTransactionCode(MOMO_PREFIX);

        PaymentResponse response = paymentFactoryService.processCallback(PaymentMethod.MOMO,
                transactionCode, MockConstants.DEFAULT_SIGNATURE, callbackData);

        String message = messageSource.getMessage("success.payment.callback.processed", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success("OK", message);
    }

    @PostMapping("/vnpay/callback")
    @Operation(summary = "VNPay payment callback", description = "Handle VNPay payment callback")
    public ApiResponse<String> vnpayCallback(@RequestBody String callbackData) {
        log.info("Received VNPay callback: {}", callbackData);

        String transactionCode = generateTransactionCode(VNPAY_PREFIX);

        PaymentResponse response = paymentFactoryService.processCallback(PaymentMethod.VNPAY,
                transactionCode, MockConstants.DEFAULT_SIGNATURE, callbackData);

        String message = messageSource.getMessage("success.payment.callback.processed", null,
                LocaleContextHolder.getLocale());
        return ApiResponse.success("OK", message);
    }

    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Refund payment", description = "Refund a successful payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> refundPayment(@PathVariable Long paymentId,
            @RequestParam PaymentMethod method, @RequestParam String reason) {
        log.info("Processing refund for payment ID: {}, method: {}, reason: {}", paymentId, method,
                reason);

        PaymentResponse response = paymentFactoryService.refundPaymentById(method, paymentId,
                reason);
        String message = messageSource.getMessage("success.payment.refunded", null,
                LocaleContextHolder.getLocale());

        return ApiResponse.success(response, message);
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancel payment", description = "Cancel a pending payment")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> cancelPayment(@PathVariable Long paymentId,
            @RequestParam PaymentMethod method) {
        log.info("Cancelling payment for payment ID: {}", paymentId);

        PaymentResponse response = paymentFactoryService.cancelPaymentById(method, paymentId);
        String message = messageSource.getMessage("success.payment.cancelled", null,
                LocaleContextHolder.getLocale());

        return ApiResponse.success(response, message);
    }

    /**
     * Generate a unique transaction code with prefix and timestamp
     * 
     * @param prefix
     *            The prefix for the transaction code (e.g., "MOMO_", "VNPAY_")
     * @return A unique transaction code
     */
    private String generateTransactionCode(String prefix) {
        String timestamp = LocalDateTime.now().format(TRANSACTION_CODE_FORMATTER);
        return prefix + timestamp;
    }
}
