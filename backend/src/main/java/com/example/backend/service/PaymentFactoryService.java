package com.example.backend.service;

import org.springframework.stereotype.Service;

import com.example.backend.constant.enums.PaymentMethod;
import com.example.backend.constant.enums.PaymentStatus;
import com.example.backend.dto.PaymentRequest;
import com.example.backend.dto.PaymentResponse;
import com.example.backend.entity.Payment;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.service.impl.MomoPaymentService;
import com.example.backend.service.impl.VnpayPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFactoryService {

    private final MomoPaymentService momoPaymentService;
    private final VnpayPaymentService vnpayPaymentService;
    private final PaymentRepository paymentRepository;
    private final PaymentValidationService paymentValidationService;

    public PaymentResponse createPayment(PaymentRequest request) {
        return switch (request.paymentMethod()) {
            case MOMO -> momoPaymentService.createPayment(request);
            case VNPAY -> vnpayPaymentService.createPayment(request);
            case BANK_TRANSFER, CASH, CREDIT_CARD ->
                throwUnsupportedPaymentMethod(request.paymentMethod());
        };
    }

    public PaymentResponse processCallback(PaymentMethod method, String transactionCode,
            String signature, Object callbackData) {
        return switch (method) {
            case MOMO -> momoPaymentService.processCallback(method, transactionCode, signature,
                    callbackData);
            case VNPAY -> vnpayPaymentService.processCallback(method, transactionCode, signature,
                    callbackData);
            case BANK_TRANSFER, CASH, CREDIT_CARD -> throwUnsupportedPaymentMethod(method);
        };
    }

    public PaymentResponse refundPayment(PaymentMethod method, String transactionCode,
            String reason) {
        return switch (method) {
            case MOMO -> momoPaymentService.refundPayment(transactionCode, reason);
            case VNPAY -> vnpayPaymentService.refundPayment(transactionCode, reason);
            case BANK_TRANSFER, CASH, CREDIT_CARD -> throwUnsupportedPaymentMethod(method);
        };
    }

    public PaymentResponse refundPaymentById(PaymentMethod method, Long paymentId, String reason) {
        // Find payment by ID first
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("error.payment.not.found", paymentId));

        // Validate payment status before refund
        paymentValidationService.validatePaymentForRefund(paymentId);

        return refundPayment(method, payment.getTransactionCode(), reason);
    }

    public PaymentResponse cancelPayment(PaymentMethod method, String transactionCode) {
        return switch (method) {
            case MOMO -> momoPaymentService.cancelPayment(transactionCode);
            case VNPAY -> vnpayPaymentService.cancelPayment(transactionCode);
            case BANK_TRANSFER, CASH, CREDIT_CARD -> throwUnsupportedPaymentMethod(method);
        };
    }

    public PaymentResponse cancelPaymentById(PaymentMethod method, Long paymentId) {
        // Find payment by ID first
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("error.payment.not.found", paymentId));

        // Validate payment status before cancellation
        paymentValidationService.validatePaymentForCancellation(paymentId);

        return cancelPayment(method, payment.getTransactionCode());
    }

    public PaymentStatus getPaymentStatusById(Long paymentId) {
        // Find payment by ID first
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ResourceNotFoundException("error.payment.not.found", paymentId));

        return payment.getStatus();
    }

    /**
     * Throw exception for unsupported payment methods
     * 
     * @param method
     *            The unsupported payment method
     * @return Never returns, always throws exception
     * @throws BusinessException
     *             with appropriate error message
     */
    private PaymentResponse throwUnsupportedPaymentMethod(PaymentMethod method) {
        log.warn("Payment method {} not yet implemented", method);
        throw new BusinessException("error.payment.method.not.supported");
    }
}
